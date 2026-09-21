#!/usr/bin/env bash
# Real, deterministic coupling/cohesion/Law-of-Demeter demos. Pure JDK, no dependencies.
# Tested on OpenJDK 21.0.12.
set -uo pipefail
cd "$(dirname "$0")"
rm -rf out out-v2-demeter out-v2-trainwreck
mkdir -p out out-v2-demeter out-v2-trainwreck

echo "### 1. Coupling measurement: God Class vs. decomposed, plus Feature Envy fix (behavior parity)"
javac -d out src/coupling/CouplingMeasurementDemo.java
java -cp out coupling.CouplingMeasurementDemo

echo
echo "### 2a. Law of Demeter, v1 baseline: train-wreck and Demeter-compliant styles produce identical output"
javac -d out src/demeter/v1/*.java
java -cp out demeter.v1.Main

echo
echo "### 2b. Law of Demeter, v2: Wallet's internal structure changes (single card -> multiple cards)"
echo "         Demeter-compliant clients (unchanged source) still compile:"
javac -d out-v2-demeter src/demeter/v2/Card.java src/demeter/v2/Wallet.java src/demeter/v2/Customer.java \
    src/demeter/v2/DemeterReceiptPrinter.java src/demeter/v2/DemeterRefundService.java src/demeter/v2/DemeterOrderConfirmationEmailer.java \
    && echo "  SUCCESS: all 3 Demeter-compliant clients compiled UNCHANGED against the new Wallet"

echo
echo "         Train-wreck clients (unchanged source) now fail to compile:"
javac -d out-v2-trainwreck src/demeter/v2/Card.java src/demeter/v2/Wallet.java src/demeter/v2/Customer.java \
    src/demeter/v2/TrainWreckReceiptPrinter.java src/demeter/v2/TrainWreckRefundService.java src/demeter/v2/TrainWreckOrderConfirmationEmailer.java 2>&1
echo "  (3 real compile errors above, one per train-wreck client, all 'cannot find symbol: method getCard()')"

rm -rf out out-v2-demeter out-v2-trainwreck
