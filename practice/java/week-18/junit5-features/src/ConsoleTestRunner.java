import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.TagFilter;

import java.io.PrintWriter;

// Small, dependency-free (beyond the JUnit Platform jars themselves) console
// runner using the programmatic Launcher API -- no build tool needed, just
// `javac`/`java` with the JUnit jars on the classpath. Reused across this
// week's JUnit5, live-coding-TDD, and mutation-testing demos.
// Optional second arg: an @Tag name to filter execution to (e.g. "fast").
public class ConsoleTestRunner {
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("usage: ConsoleTestRunner <fully.qualified.TestClassName> [tagToInclude]");
            System.exit(2);
        }
        Class<?> testClass = Class.forName(args[0]);

        var builder = org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder.request()
                .selectors(DiscoverySelectors.selectClass(testClass));
        if (args.length > 1) {
            builder = builder.filters(TagFilter.includeTags(args[1]));
            System.out.println("Filtering to tag: " + args[1]);
        }
        LauncherDiscoveryRequest request = builder.build();

        Launcher launcher = LauncherFactory.create();
        SummaryGeneratingListener listener = new SummaryGeneratingListener();
        launcher.registerTestExecutionListeners(listener);
        launcher.execute(request);

        TestExecutionSummary summary = listener.getSummary();
        summary.printTo(new PrintWriter(System.out));
        summary.printFailuresTo(new PrintWriter(System.out), 5);

        System.out.printf("RESULT: %d tests found, %d succeeded, %d failed%n",
                summary.getTestsFoundCount(), summary.getTestsSucceededCount(), summary.getTestsFailedCount());

        if (summary.getTestsFailedCount() > 0) System.exit(1);
    }
}
