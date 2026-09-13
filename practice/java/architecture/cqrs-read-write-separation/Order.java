import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** Write-side (command-side) domain object. Normalized: items live separately from the order. */
final class Order {
    final long id;
    final String customerId;
    final List<OrderItem> items = new ArrayList<>();
    String status = "OPEN";

    Order(long id, String customerId) {
        this.id = id;
        this.customerId = customerId;
    }

    BigDecimal totalAmount() {
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : items) {
            total = total.add(item.unitPrice.multiply(BigDecimal.valueOf(item.quantity)));
        }
        return total;
    }

    int totalItemCount() {
        int count = 0;
        for (OrderItem item : items) {
            count += item.quantity;
        }
        return count;
    }
}

final class OrderItem {
    final String sku;
    final int quantity;
    final BigDecimal unitPrice;

    OrderItem(String sku, int quantity, BigDecimal unitPrice) {
        this.sku = sku;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }
}
