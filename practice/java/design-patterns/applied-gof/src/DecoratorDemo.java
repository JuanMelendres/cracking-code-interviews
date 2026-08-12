// Java 21. Decorator: attach additional behavior to an object dynamically, by
// wrapping it in another object implementing the same interface, instead of
// creating a subclass for every combination of optional behaviors. For N
// independent optional behaviors, inheritance needs up to 2^N subclasses;
// composition needs N decorator classes, combined however the caller wants.

interface Notifier {
    void send(String message);
}

class EmailNotifier implements Notifier {
    public void send(String message) { System.out.println("  EMAIL: " + message); }
}

abstract class NotifierDecorator implements Notifier {
    protected final Notifier wrapped;
    NotifierDecorator(Notifier wrapped) { this.wrapped = wrapped; }
    public void send(String message) { wrapped.send(message); } // delegate first, subclasses add their own channel around this
}

class SlackDecorator extends NotifierDecorator {
    SlackDecorator(Notifier wrapped) { super(wrapped); }
    @Override
    public void send(String message) {
        super.send(message);
        System.out.println("  SLACK: " + message);
    }
}

class SmsDecorator extends NotifierDecorator {
    SmsDecorator(Notifier wrapped) { super(wrapped); }
    @Override
    public void send(String message) {
        super.send(message);
        System.out.println("  SMS:   " + message);
    }
}

class DecoratorDemo {
    public static void main(String[] args) {
        System.out.println("== Email only ==");
        Notifier emailOnly = new EmailNotifier();
        emailOnly.send("Build passed");

        System.out.println();
        System.out.println("== Email + Slack, composed at runtime -- no EmailSlackNotifier subclass exists ==");
        Notifier emailAndSlack = new SlackDecorator(new EmailNotifier());
        emailAndSlack.send("Deploy started");

        System.out.println();
        System.out.println("== Email + Slack + SMS, a third layer added with zero changes to the other two ==");
        Notifier allThree = new SmsDecorator(new SlackDecorator(new EmailNotifier()));
        allThree.send("Deployment succeeded");

        System.out.println();
        System.out.println("Notice: SMS + Email (skipping Slack) is also just one more composition --");
        System.out.println("new SmsDecorator(new EmailNotifier()) -- with no new class needed at all:");
        Notifier smsAndEmail = new SmsDecorator(new EmailNotifier());
        smsAndEmail.send("Skip-Slack composition");
    }
}
