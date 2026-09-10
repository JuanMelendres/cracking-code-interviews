# Object-Oriented Design Interview Problems — Real Demos

Backs [`syllabus/04-software-design/ood-interview-problems.md`](../../../syllabus/04-software-design/ood-interview-problems.md) (T-1702).

Pure JDK, no dependencies. Two fully worked, real, compiling OOD interview
prompts.

## Run it

```bash
mkdir -p out
javac -d out src/ParkingLotDemo.java src/VendingMachineDemo.java
java -cp out ParkingLotDemo
java -cp out VendingMachineDemo
```

Real combined output captured in [`output-transcript.txt`](output-transcript.txt).

## What it proves

- **`ParkingLotDemo`** — a `Vehicle`/`ParkingSpot` size-fit model correctly
  places a motorcycle, car, and bus; correctly rejects a car when no
  matching-size spot remains; correctly computes a real time-based fee
  (`Duration.between`, not a hardcoded number) on exit; correctly frees the
  spot for a previously-rejected vehicle once it's available again.
- **`VendingMachineDemo`** — an explicit `State` enum (`IDLE`/`HAS_MONEY`/`DISPENSING`)
  correctly rejects selecting an item before money is inserted, correctly
  rejects selecting a sold-out item, and correctly computes real change
  (`insertedCents - item.priceCents`) on a successful purchase.
