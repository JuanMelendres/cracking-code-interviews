#!/bin/bash
# Usage: writeskew-tx.sh <isolation-level> <doctor-name>
LEVEL="$1"
NAME="$2"
cat <<SQL
BEGIN ISOLATION LEVEL $LEVEL;
SELECT count(*) AS on_call_count FROM on_call WHERE is_on_call;
\! sleep 2
UPDATE on_call SET is_on_call = false WHERE doctor = '$NAME';
COMMIT;
SELECT doctor, is_on_call FROM on_call ORDER BY doctor;
SQL
