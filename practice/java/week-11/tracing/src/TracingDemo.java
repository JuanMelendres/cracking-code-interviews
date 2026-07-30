import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import io.opentelemetry.exporter.logging.LoggingSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;

/**
 * T-1205 -- a real, executed distributed trace: one root span (the
 * incoming request) with two child spans (calls to two downstream
 * "services"), one of which itself has a grandchild span (a database
 * call) and fails. Real OpenTelemetry SDK, real span export -- not a
 * diagram of what a trace looks like.
 */
public class TracingDemo {
    public static void main(String[] args) throws Exception {
        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
                .addSpanProcessor(SimpleSpanProcessor.create(LoggingSpanExporter.create()))
                .build();
        OpenTelemetry otel = OpenTelemetrySdk.builder().setTracerProvider(tracerProvider).build();
        Tracer tracer = otel.getTracer("week11-tracing-demo");

        Span root = tracer.spanBuilder("POST /orders").startSpan();
        try (Scope rootScope = root.makeCurrent()) {
            root.setAttribute("http.method", "POST");
            root.setAttribute(AttributeKey.stringKey("http.route"), "/orders");

            // child 1: a call to the order-validation service -- succeeds
            Span validate = tracer.spanBuilder("order-service.validate").startSpan();
            try (Scope s = validate.makeCurrent()) {
                Thread.sleep(15);
                validate.setStatus(StatusCode.OK);
            } finally {
                validate.end();
            }

            // child 2: a call to the payment service, which itself calls a database
            // (grandchild span) -- the database call fails, and the failure
            // propagates up through both spans' status
            Span payment = tracer.spanBuilder("payment-service.charge").startSpan();
            try (Scope s = payment.makeCurrent()) {
                Span dbCall = tracer.spanBuilder("payment-db.insert").setParent(io.opentelemetry.context.Context.current()).startSpan();
                try (Scope dbScope = dbCall.makeCurrent()) {
                    Thread.sleep(8);
                    dbCall.setStatus(StatusCode.ERROR, "connection pool exhausted");
                    dbCall.recordException(new RuntimeException("connection pool exhausted"));
                } finally {
                    dbCall.end();
                }
                payment.setStatus(StatusCode.ERROR, "payment failed: downstream db error");
            } finally {
                payment.end();
            }

            root.setStatus(StatusCode.ERROR, "order failed: payment error");
        } finally {
            root.end();
        }

        tracerProvider.close();
        System.out.println();
        System.out.println("Trace complete. Note the shared traceId across all 4 spans above, "
                + "and the parent/child spanId relationships -- that's what lets a tracing backend "
                + "reconstruct the full call tree and show exactly which downstream call caused the failure.");
    }
}
