import java.util.HashMap;
import java.util.Map;

// Real, compiling second worked OOD example: a vending machine, modeled as an
// explicit state machine (the classic recognition signal for this prompt family
// -- "an object whose *legal next actions* depend on which state it's currently
// in" should make a candidate reach for the State pattern, not a pile of
// booleans). Demonstrates the same illegal-transition rejection and real
// change-making arithmetic a real machine needs.
public class VendingMachineDemo {

    enum State { IDLE, HAS_MONEY, DISPENSING }

    static class Item {
        final String name;
        final int priceCents;
        int stock;
        Item(String name, int priceCents, int stock) { this.name = name; this.priceCents = priceCents; this.stock = stock; }
    }

    static class VendingMachine {
        private State state = State.IDLE;
        private int insertedCents = 0;
        private final Map<String, Item> inventory = new HashMap<>();

        void stock(Item item) { inventory.put(item.name, item); }

        void insertCoin(int cents) {
            if (state == State.DISPENSING) {
                throw new IllegalStateException("Cannot insert coins while dispensing");
            }
            insertedCents += cents;
            state = State.HAS_MONEY;
            System.out.println("  Inserted " + cents + "c. Balance now " + insertedCents + "c. State=" + state);
        }

        int select(String itemName) {
            if (state != State.HAS_MONEY) {
                throw new IllegalStateException("Insert money before selecting an item (state=" + state + ")");
            }
            Item item = inventory.get(itemName);
            if (item == null || item.stock <= 0) {
                throw new IllegalStateException(itemName + " is sold out or does not exist");
            }
            if (insertedCents < item.priceCents) {
                throw new IllegalStateException(itemName + " costs " + item.priceCents + "c, only " + insertedCents + "c inserted");
            }
            state = State.DISPENSING;
            item.stock--;
            int change = insertedCents - item.priceCents;
            System.out.println("  Dispensing " + itemName + ". Change returned: " + change + "c. State=" + state);
            insertedCents = 0;
            state = State.IDLE;
            return change;
        }
    }

    public static void main(String[] args) {
        VendingMachine machine = new VendingMachine();
        machine.stock(new Item("Soda", 125, 1));
        machine.stock(new Item("Chips", 175, 0)); // sold out on purpose

        System.out.println("=== Try to select BEFORE inserting any money -- illegal transition ===");
        try {
            machine.select("Soda");
            System.out.println("  Selected (BUG -- should have been rejected)");
        } catch (IllegalStateException e) {
            System.out.println("  Rejected: " + e.getMessage());
        }

        System.out.println();
        System.out.println("=== Insert 150c, then select Soda (125c) -- real change calculated ===");
        machine.insertCoin(100);
        machine.insertCoin(50);
        int change = machine.select("Soda");
        System.out.println("  Caller received " + change + "c change");

        System.out.println();
        System.out.println("=== Try to buy the now-sold-out Soda again ===");
        machine.insertCoin(125);
        try {
            machine.select("Soda");
            System.out.println("  Selected (BUG -- should have been rejected, stock is 0)");
        } catch (IllegalStateException e) {
            System.out.println("  Rejected: " + e.getMessage());
        }

        System.out.println();
        System.out.println("=== Try to select an item that was already sold out from the start (Chips) ===");
        try {
            machine.select("Chips");
            System.out.println("  Selected (BUG)");
        } catch (IllegalStateException e) {
            System.out.println("  Rejected: " + e.getMessage());
        }
    }
}
