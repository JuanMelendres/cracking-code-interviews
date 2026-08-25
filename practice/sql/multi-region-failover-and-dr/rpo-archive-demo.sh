#!/bin/bash
# Real RPO exposure for WAL-archiving (log-shipping) DR — the cheaper alternative to a
# continuously-streaming hot standby, and a genuinely different, bounded RPO trade.
#
# The primary is configured with archive_mode=on, archive_timeout=3 (a WAL segment is
# forced closed and archived at least every 3 real seconds, even under light write
# load, not only when a segment fills). We write real, individually-timestamped rows
# roughly once a second, then destroy the primary's container AND its live WAL
# entirely — leaving only whatever made it into the archive directory on the host.
# Any row committed after the last real archive boundary is genuinely unrecoverable
# from the archive alone. This is a real, observable, bounded RPO window, in contrast
# to rpo-demo.sh's near-zero result for a live streaming standby.
set -euo pipefail
cd "$(dirname "$0")"

PSQL="docker exec -e PGPASSWORD=primarypass dr-region-primary psql -U postgres -d appdb"

rm -rf archive && mkdir -p archive

echo "=== Bringing up region-primary only (archive_mode=on, archive_timeout=3s) ==="
docker compose up -d region-primary

echo "=== Waiting for region-primary to accept connections ==="
until $PSQL -tAc "SELECT 1;" >/dev/null 2>&1; do
    sleep 1
done

ROWLOG=$(mktemp)
echo "=== Writing one real, timestamped row roughly every second for 10 seconds ==="
for i in $(seq 1 10); do
    row=$($PSQL -tAqc "INSERT INTO ledger(payload) VALUES ('archived-test-$i') RETURNING seq, written_at;")
    epoch=$(date +%s)
    echo "$epoch|$row" | tee -a "$ROWLOG"
    sleep 1
done

echo
echo "=== Real archived WAL segments on the host (ground truth: what actually survived) ==="
ls -la archive/

LAST_ARCHIVE_EPOCH=0
for f in archive/*; do
    [ -f "$f" ] || continue
    m=$(stat -f '%m' "$f")
    if [ "$m" -gt "$LAST_ARCHIVE_EPOCH" ]; then
        LAST_ARCHIVE_EPOCH=$m
    fi
done

echo
echo "=== Destroying region-primary NOW (container + live WAL) — only ./archive survives ==="
docker rm -f -v dr-region-primary >/dev/null

echo
echo "=== Real committed rows during the write window ==="
cut -d'|' -f2- "$ROWLOG"

echo
echo "=== REAL RPO EXPOSURE (log-shipping) ==="
if [ "$LAST_ARCHIVE_EPOCH" -eq 0 ]; then
    echo "No WAL segment was archived during the write window at all (archive_timeout=3s"
    echo "hadn't yet elapsed, or the segment simply hadn't filled) — every single row"
    echo "written in this run is genuinely unrecoverable from the archive alone."
    LOST_COUNT=$(wc -l < "$ROWLOG" | tr -d ' ')
else
    echo "Last WAL segment actually archived at (host epoch): $LAST_ARCHIVE_EPOCH ($(date -r "$LAST_ARCHIVE_EPOCH"))"
    echo "Rows committed strictly after that real archive boundary — genuinely unrecoverable"
    echo "from the archive alone, since they were still sitting in the live primary's"
    echo "not-yet-closed WAL segment when the primary was destroyed:"
    LOST_COUNT=0
    while IFS='|' read -r write_epoch seq written_at; do
        if [ "$write_epoch" -gt "$LAST_ARCHIVE_EPOCH" ]; then
            echo "  seq=$seq written_at=$written_at ($(( write_epoch - LAST_ARCHIVE_EPOCH ))s after last archive)"
            LOST_COUNT=$((LOST_COUNT + 1))
        fi
    done < "$ROWLOG"
fi
echo
echo "Rows genuinely lost: $LOST_COUNT / $(wc -l < "$ROWLOG" | tr -d ' ')"

rm -f "$ROWLOG"

echo
echo "=== Tearing down ==="
docker compose down -v >/dev/null
rm -rf archive
echo "Done. (./archive removed — real WAL segment files are multi-megabyte and not meant to be kept around.)"
