package demeter.v2;

public class DemeterReceiptPrinter {
    public String printLine(Customer customer) {
        // Demeter-compliant: talks only to Customer, its immediate collaborator.
        return "Receipt for " + customer.getName() + ", card ending " + customer.getCardLast4Digits();
    }
}
