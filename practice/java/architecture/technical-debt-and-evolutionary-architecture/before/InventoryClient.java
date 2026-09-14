package before;

public final class InventoryClient {
    public void reserve(String orderId) {
        System.out.println("[Inventory] reserved stock for " + orderId);
    }
}
