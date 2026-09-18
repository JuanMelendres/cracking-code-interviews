package demo;

public class OrderDto {
    public long id;
    public String customerName;
    public long amountCents;

    public OrderDto() { }

    public OrderDto(long id, String customerName, long amountCents) {
        this.id = id;
        this.customerName = customerName;
        this.amountCents = amountCents;
    }
}
