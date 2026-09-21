package demeter.v1;

public class Main {
    public static void main(String[] args) {
        Customer customer = new Customer("Alice", new Wallet(new Card("4242")));

        String trainWreckReceipt = new TrainWreckReceiptPrinter().printLine(customer);
        String demeterReceipt = new DemeterReceiptPrinter().printLine(customer);
        System.out.println("train-wreck: " + trainWreckReceipt);
        System.out.println("demeter:     " + demeterReceipt);

        boolean match = trainWreckReceipt.equals(demeterReceipt);
        System.out.println("v1 baseline: both styles produce identical output = " + match);
        if (!match) throw new IllegalStateException("v1 baseline outputs diverged");

        System.out.println(new TrainWreckRefundService().describeRefundTarget(customer));
        System.out.println(new DemeterRefundService().describeRefundTarget(customer));
        System.out.println(new TrainWreckOrderConfirmationEmailer().buildEmailBody(customer));
        System.out.println(new DemeterOrderConfirmationEmailer().buildEmailBody(customer));
    }
}
