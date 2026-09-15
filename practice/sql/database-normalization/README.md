# Database Normalization — Real Lab

Backs [`syllabus/06-databases/database-normalization-1nf-through-bcnf.md`](../../../syllabus/06-databases/database-normalization-1nf-through-bcnf.md) (T-2411).

Real PostgreSQL 16, run in a disposable Docker container. No app code —
pure SQL.

## Run it

```bash
docker run -d --name norm-demo -e POSTGRES_PASSWORD=demo -p 15932:5432 postgres:16
docker exec -i norm-demo psql -U postgres -a < normalization-lab.sql > normalization-lab-output.txt
docker stop norm-demo && docker rm norm-demo
```

Full captured output in [`normalization-lab-output.txt`](normalization-lab-output.txt).
Timings vary somewhat by run and machine; the qualitative findings below
are consistent.

## What it proves

1. **1NF — a repeating group makes an obvious query genuinely hard.**
   "Find every customer sharing a phone number with Alice" against a
   comma-separated `phones` column requires a fragile `LIKE` scan; the
   1NF-normalized version (one phone per row) answers it with a plain,
   indexable equality join.
2. **2NF — a real, captured update anomaly from a partial dependency.**
   Renaming product 55 in one `order_items` row (where `product_name`
   depends only on `product_id`, not the full `(order_id, product_id)`
   key) leaves a real, captured inconsistency: the same product shows
   both `Widget` and `Widget Pro` depending on which row you read.
   Extracting `products` into its own table makes the rename exactly
   one row, and the anomaly becomes structurally impossible.
3. **3NF — the identical anomaly shape from a transitive dependency.**
   Renaming department 10 in one `employees` row (where
   `department_name` depends on `department_id`, which depends on
   `employee_id` — a dependency *through* a non-key column) leaves a
   real, captured inconsistency: two employees in the same department
   show two different department names. Extracting `departments`
   fixes it the same way.
4. **BCNF — a 3NF-satisfying schema can still have a real anomaly.**
   `enrollments(student_id, course_id, instructor)` is already in 3NF,
   but nothing stops the same instructor being recorded against two
   different courses — a real, captured violation of the intended
   real-world rule (each instructor teaches exactly one course),
   because that rule lives in a non-candidate-key column's functional
   dependency. The BCNF-normalized version turns it into a real,
   captured `duplicate key value violates unique constraint` error —
   the rule is now actually enforced, not just usually true.
5. **The real, measured denormalization trade-off.** The identical
   revenue-by-country aggregation: **84.152ms** as a normalized
   3-table join (100,000 orders, 400,000 order lines, 5,000
   customers) versus **25.763ms** against a deliberately denormalized
   flat table — a real ~3.27× read speedup. The real, honest cost:
   renaming one customer touches **1 row** in the normalized schema
   and **80 rows** in the denormalized one (every order line that
   customer ever generated).
