# Week 8 Java — Kafka — runnable verification

Three real demos against a live single-broker Kafka cluster (KRaft mode, no ZooKeeper), plain `kafka-clients` jar from Maven Central (no Spring/Maven build), same no-framework approach as Weeks 3 and 7's internals demos.

**Run in order, same broker session: 1 -> 2 -> 3.** Each demo ensures the `orders` topic exists with 4 partitions on its own, so none of them will fail if run standalone -- but demos 2 and 3 read whatever records are actually on the topic, and the "18 records" / specific offsets quoted in `study-packs/week-08/`'s chapters come from demo 1 having produced them first in the same session. Running 2 or 3 against a topic demo 1 hasn't touched yet still demonstrates the real rebalance/commit mechanics, just against an empty log.

## Setup

```bash
cd practice/java/week-08/kafka
./fetch-deps.sh
mkdir -p out
javac -cp "lib/*" -d out src/*.java
cp src/simplelogger.properties out/   # suppresses noisy client config dumps

# start a single-broker KRaft cluster (~8s to become ready)
docker run -d --name week08-kafka -p 9092:9092 \
  -e KAFKA_NODE_ID=1 \
  -e KAFKA_PROCESS_ROLES=broker,controller \
  -e KAFKA_LISTENERS=PLAINTEXT://:9092,CONTROLLER://:9093 \
  -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
  -e KAFKA_CONTROLLER_LISTENER_NAMES=CONTROLLER \
  -e KAFKA_LISTENER_SECURITY_PROTOCOL_MAP=CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT \
  -e KAFKA_CONTROLLER_QUORUM_VOTERS=1@localhost:9093 \
  -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 \
  -e CLUSTER_ID=MkU3OEVBNTcwNTJENDM2Qk \
  apache/kafka:3.7.0
sleep 8
```

Tear down when done: `docker rm -f week08-kafka`.

## 1. Producer fundamentals + partition-key routing — `ProducerPartitionKeyDemo.java`

Creates the 4-partition `orders` topic and sends: (a) six records with the same key, (b) six records with six different keys, (c) six records with no key.

```bash
java -cp "out:lib/*" ProducerPartitionKeyDemo
```

**Real observed output (last run):**

```
== same key -> same partition, every time ==
key=customer-42 value=order-0 -> partition=1 offset=0
key=customer-42 value=order-1 -> partition=1 offset=1
key=customer-42 value=order-2 -> partition=1 offset=2
key=customer-42 value=order-3 -> partition=1 offset=3
key=customer-42 value=order-4 -> partition=1 offset=4
key=customer-42 value=order-5 -> partition=1 offset=5
== different keys -> spread across partitions ==
key=customer-1   -> partition=1 offset=6
key=customer-2   -> partition=2 offset=0
key=customer-3   -> partition=2 offset=1
key=customer-4   -> partition=1 offset=7
key=customer-5   -> partition=2 offset=2
key=customer-6   -> partition=3 offset=0
== null key -> sticky partitioner batches onto one partition per batch ==
key=null value=unkeyed-0 -> partition=2 offset=3
key=null value=unkeyed-1 -> partition=2 offset=4
key=null value=unkeyed-2 -> partition=2 offset=5
key=null value=unkeyed-3 -> partition=2 offset=6
key=null value=unkeyed-4 -> partition=2 offset=7
key=null value=unkeyed-5 -> partition=2 offset=8
```

**What this proves:** the same key deterministically maps to the same partition every time (all six `customer-42` records land on partition 1); different keys spread across partitions; a null key doesn't round-robin per record but sticky-batches onto one partition per in-flight batch. This run leaves 18 records on the `orders` topic, consumed by the next two demos.

## 2. Consumer groups & rebalancing — `ConsumerGroupDemo.java`

`consumer-1` joins a fresh group alone (gets all 4 partitions), then `consumer-2` joins the same group (triggers a rebalance, splits 2/2), then both leave and a solo `consumer-3` joins (gets everything back).

```bash
java -cp "out:lib/*" ConsumerGroupDemo
```

**Real observed output (last run):**

```
== consumer-1 joins group 'order-processors-...' alone -> gets all 4 partitions ==
[consumer-1] assigned partitions: [orders-0, orders-1, orders-2, orders-3]
== consumer-2 joins the same group -> triggers rebalance, partitions split ==
[consumer-1] assigned partitions: [orders-0, orders-1]
[consumer-2] assigned partitions: [orders-2, orders-3]
consumer-1 processed 18 records, consumer-2 processed 0 records
== both leave; a solo consumer-3 joins the same group -> full rebalance, gets all partitions back ==
[consumer-3] assigned partitions: [orders-0, orders-1, orders-2, orders-3]
consumer-3 alone was assigned: processed 0 NEW records (rest already committed by consumer-1/2)
```

**What this proves:** partition assignment is exclusive within a group and rebalances on membership change; a newly-joined member resumes from committed offsets, not from the start (which is why `consumer-2` and `consumer-3` see 0 new records here — `consumer-1` already drained and committed the whole backlog before they joined).

## 3. Delivery semantics — `DeliverySemanticsDemo.java`

Runs the same 18-record backlog through two different commit orderings, each against a fresh consumer group, simulating a crash at the critical point.

```bash
java -cp "out:lib/*" DeliverySemanticsDemo
```

**Real observed output (last run):**

```
== at-least-once: commit AFTER processing ==
-- attempt 1: process batch, crash before commit --
  processed 18 records, simulating crash BEFORE commitSync()
-- attempt 2 (same group, no commit landed): reprocess from last committed offset --
  processed 18 records, committed successfully
attempt 1 processed 18 records (uncommitted) + attempt 2 processed 18 records (redelivered) = 36 total deliveries for 18 unique records -> duplicates observed

== at-most-once: commit BEFORE processing ==
-- attempt 1: commit offsets immediately on poll, then crash before processing --
  committed offsets for 18 records, simulating crash BEFORE processing them
-- attempt 2 (same group, offsets already committed): poll returns nothing left --
  committed and processed 0 records (0 expected -- backlog was already drained by attempt 1's commit)
attempt 1 committed offsets for 0 records but crashed before processing them + attempt 2 processed 0 records = 0 records actually processed out of 18 -> loss observed
```

**What this proves:** committing after processing risks real, observed duplicate delivery on a simulated crash; committing before processing risks real, observed silent loss. Both are actual offset-commit behaviors against a live broker, not a description.
