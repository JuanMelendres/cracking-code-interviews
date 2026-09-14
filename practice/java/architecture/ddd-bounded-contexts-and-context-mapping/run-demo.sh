#!/bin/bash
set -uo pipefail
cd "$(dirname "$0")"

echo "=== Step 1: diff check -- which files are identical across v1/v2, which differ ==="
for f in fulfillment/FulfillmentOrder.java conformist/ConformistFulfillmentService.java acl/AclFulfillmentService.java; do
    if diff -q "v1-original-schema/$f" "v2-upstream-renamed-field/$f" > /dev/null; then
        echo "  IDENTICAL: $f"
    else
        echo "  DIFFERS (unexpected!): $f"
    fi
done
for f in sales/SalesOrder.java acl/OrderTranslator.java; do
    if diff -q "v1-original-schema/$f" "v2-upstream-renamed-field/$f" > /dev/null; then
        echo "  IDENTICAL (unexpected!): $f"
    else
        echo "  DIFFERS (expected -- upstream change / its absorption): $f"
    fi
done

echo
echo "=== Step 2: compile v1-original-schema (everything) ==="
rm -rf out-v1 && mkdir -p out-v1
javac -d out-v1 v1-original-schema/sales/*.java v1-original-schema/fulfillment/*.java \
    v1-original-schema/conformist/*.java v1-original-schema/acl/*.java
echo "Exit code: $? (expected 0)"

echo
echo "=== Step 3: compile v2-upstream-renamed-field, ACL path only ==="
rm -rf out-v2-acl && mkdir -p out-v2-acl
javac -d out-v2-acl v2-upstream-renamed-field/sales/*.java v2-upstream-renamed-field/fulfillment/*.java \
    v2-upstream-renamed-field/acl/*.java
echo "Exit code: $? (expected 0 -- AclFulfillmentService.java is unchanged and still compiles)"

echo
echo "=== Step 4: compile v2-upstream-renamed-field, CONFORMIST path only ==="
rm -rf out-v2-conformist && mkdir -p out-v2-conformist
javac -d out-v2-conformist v2-upstream-renamed-field/sales/*.java v2-upstream-renamed-field/conformist/*.java
echo "Exit code: $? (expected 1 -- ConformistFulfillmentService.java is unchanged and no longer compiles)"

rm -rf out-v1 out-v2-acl out-v2-conformist
