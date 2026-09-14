package demo;

import demo.grpc.AddBooksSummary;
import demo.grpc.Book;
import demo.grpc.BookRequest;
import demo.grpc.BookServiceGrpc;
import demo.grpc.ChatMessage;
import demo.grpc.ListBooksRequest;
import io.grpc.Server;
import io.grpc.StatusRuntimeException;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;

import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Real, executed grpc-java 1.68.1 demo backing
 * syllabus/07-api-design/grpc-api-design.md (T-918).
 *
 * Runs a real gRPC server and a real gRPC client stub over an in-process
 * transport (no sockets needed for the demo, but the wire semantics --
 * streaming, deadlines, status codes -- are the real thing, not a mock).
 */
public class GrpcApiDemo {

    public static void main(String[] args) throws Exception {
        String serverName = InProcessServerBuilder.generateName();
        BookServiceImpl serviceImpl = new BookServiceImpl();

        Server server = InProcessServerBuilder.forName(serverName)
                .directExecutor()
                .addService(serviceImpl)
                .build()
                .start();

        ManagedChannel channel = InProcessChannelBuilder.forName(serverName)
                .directExecutor()
                .build();

        try {
            BookServiceGrpc.BookServiceBlockingStub blockingStub = BookServiceGrpc.newBlockingStub(channel);
            BookServiceGrpc.BookServiceStub asyncStub = BookServiceGrpc.newStub(channel);

            System.out.println("=== 1. Unary: GetBook(\"b2\") ===");
            Book b2 = blockingStub.getBook(BookRequest.newBuilder().setId("b2").build());
            System.out.println("got: " + b2);

            System.out.println("=== 2. Unary error path: GetBook(\"does-not-exist\") -> real gRPC status code ===");
            try {
                blockingStub.getBook(BookRequest.newBuilder().setId("does-not-exist").build());
            } catch (StatusRuntimeException e) {
                System.out.println("caught: " + e.getStatus().getCode() + " - " + e.getStatus().getDescription());
            }

            System.out.println("=== 3. Server streaming: ListBooks() ===");
            Iterator<Book> books = blockingStub.listBooks(ListBooksRequest.newBuilder().build());
            int streamed = 0;
            while (books.hasNext()) {
                Book b = books.next();
                streamed++;
                System.out.println("  streamed[" + streamed + "]: " + b.getTitle());
            }
            System.out.println(">>> received " + streamed + " books over one server-streaming call");

            System.out.println("=== 4. Client streaming: AddBooks(3 books) ===");
            CountDownLatch addBooksDone = new CountDownLatch(1);
            final AddBooksSummary[] summaryHolder = new AddBooksSummary[1];
            StreamObserver<Book> addBooksRequestObserver = asyncStub.addBooks(new StreamObserver<AddBooksSummary>() {
                @Override
                public void onNext(AddBooksSummary value) {
                    summaryHolder[0] = value;
                }

                @Override
                public void onError(Throwable t) {
                    addBooksDone.countDown();
                }

                @Override
                public void onCompleted() {
                    addBooksDone.countDown();
                }
            });
            addBooksRequestObserver.onNext(Book.newBuilder().setId("b4").setTitle("The Phoenix Project").setAuthor("Kim").build());
            addBooksRequestObserver.onNext(Book.newBuilder().setId("b5").setTitle("Accelerate").setAuthor("Forsgren").build());
            addBooksRequestObserver.onNext(Book.newBuilder().setId("b6").setTitle("The Goal").setAuthor("Goldratt").build());
            addBooksRequestObserver.onCompleted();
            addBooksDone.await(5, TimeUnit.SECONDS);
            System.out.println(">>> server accepted " + summaryHolder[0].getAcceptedCount() + " books in one client-streaming call");
            System.out.println(">>> server-side counter confirms: " + serviceImpl.addBooksReceived.get() + " onNext() calls received");

            System.out.println("=== 5. Bidirectional streaming: Chat() -- interleaved send/receive on one call ===");
            CountDownLatch chatDone = new CountDownLatch(1);
            StreamObserver<ChatMessage> chatRequestObserver = asyncStub.chat(new StreamObserver<ChatMessage>() {
                @Override
                public void onNext(ChatMessage value) {
                    System.out.println("  client received: [" + value.getSender() + "] " + value.getText());
                }

                @Override
                public void onError(Throwable t) {
                    chatDone.countDown();
                }

                @Override
                public void onCompleted() {
                    chatDone.countDown();
                }
            });
            chatRequestObserver.onNext(ChatMessage.newBuilder().setSender("client").setText("ping-1").build());
            chatRequestObserver.onNext(ChatMessage.newBuilder().setSender("client").setText("ping-2").build());
            chatRequestObserver.onCompleted();
            chatDone.await(5, TimeUnit.SECONDS);

            System.out.println(">>> post-call catalog size after AddBooks: " + streamedCount(blockingStub));

        } finally {
            channel.shutdownNow();
            server.shutdownNow();
        }
    }

    private static int streamedCount(BookServiceGrpc.BookServiceBlockingStub stub) {
        Iterator<Book> it = stub.listBooks(ListBooksRequest.newBuilder().build());
        int n = 0;
        while (it.hasNext()) {
            it.next();
            n++;
        }
        return n;
    }
}
