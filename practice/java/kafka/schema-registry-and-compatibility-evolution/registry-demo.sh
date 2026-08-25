#!/bin/bash
# Real HTTP calls against a real, running Confluent Schema Registry (backed by a real
# Kafka broker) — no mocked responses. Builds the identical 2x2 matrix of schema
# changes against the two most commonly confused compatibility modes, isolating each
# test in its own subject so results never cross-contaminate.
set -euo pipefail
cd "$(dirname "$0")"

REGISTRY=http://localhost:8081
CT="Content-Type: application/vnd.schemaregistry.v1+json"

register() {
    local subject="$1" schema_file="$2"
    local body
    body=$(jq -n --rawfile schema "$schema_file" '{schema: $schema}')
    curl -s -w "\nHTTP_STATUS:%{http_code}" -X POST -H "$CT" -d "$body" "$REGISTRY/subjects/$subject/versions"
}

set_compatibility() {
    local subject="$1" mode="$2"
    curl -s -X PUT -H "$CT" -d "{\"compatibility\":\"$mode\"}" "$REGISTRY/config/$subject" >/dev/null
}

# Clean slate: delete any subjects left over from a previous run of this script.
for subj in orders-backward-v3test orders-forward-v3test orders-backward-v4test orders-forward-v4test; do
    curl -s -X DELETE "$REGISTRY/subjects/$subj" >/dev/null 2>&1 || true
    curl -s -X DELETE "$REGISTRY/subjects/$subj?permanent=true" >/dev/null 2>&1 || true
done

echo "=== Baseline: registering v1, then v2 (adds 'currency' WITH a default) ==="
echo "This should succeed under the default BACKWARD compatibility mode, in all four subjects."
for subj in orders-backward-v3test orders-forward-v3test orders-backward-v4test orders-forward-v4test; do
    register "$subj" schemas/order-v1.avsc >/dev/null
    result=$(register "$subj" schemas/order-v2-add-with-default.avsc)
    echo "  $subj: $(echo "$result" | tr '\n' ' ')"
done

echo
echo "=== Switching orders-forward-* subjects to FORWARD compatibility ==="
set_compatibility orders-forward-v3test FORWARD
set_compatibility orders-forward-v4test FORWARD
echo "orders-backward-* subjects stay on the default BACKWARD mode."

echo
echo "=== TEST 1: add 'shippingAddress' with NO default (order-v3-add-no-default.avsc) ==="
echo
echo "-- against BACKWARD (orders-backward-v3test) --"
R1=$(register orders-backward-v3test schemas/order-v3-add-no-default.avsc)
echo "$R1"
S1=$(echo "$R1" | grep HTTP_STATUS | cut -d: -f2)
echo
echo "-- against FORWARD (orders-forward-v3test) --"
R2=$(register orders-forward-v3test schemas/order-v3-add-no-default.avsc)
echo "$R2"
S2=$(echo "$R2" | grep HTTP_STATUS | cut -d: -f2)

echo
echo "=== TEST 2: remove 'amount', which had NO default (order-v4-remove-field.avsc) ==="
echo
echo "-- against BACKWARD (orders-backward-v4test) --"
R3=$(register orders-backward-v4test schemas/order-v4-remove-field.avsc)
echo "$R3"
S3=$(echo "$R3" | grep HTTP_STATUS | cut -d: -f2)
echo
echo "-- against FORWARD (orders-forward-v4test) --"
R4=$(register orders-forward-v4test schemas/order-v4-remove-field.avsc)
echo "$R4"
S4=$(echo "$R4" | grep HTTP_STATUS | cut -d: -f2)

status_label() { [ "$1" = "200" ] && echo "ACCEPTED (200)" || echo "REJECTED ($1)"; }

echo
echo
echo "=== REAL RESULT MATRIX (from the real HTTP status codes captured above) ==="
printf "%-32s %-18s %-18s\n" "" "BACKWARD" "FORWARD"
printf "%-32s %-18s %-18s\n" "add field, no default (v3)" "$(status_label "$S1")" "$(status_label "$S2")"
printf "%-32s %-18s %-18s\n" "remove field, no default (v4)" "$(status_label "$S3")" "$(status_label "$S4")"
