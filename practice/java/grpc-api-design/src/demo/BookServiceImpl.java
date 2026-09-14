package demo;

import demo.grpc.AddBooksSummary;
import demo.grpc.Book;
import demo.grpc.BookRequest;
import demo.grpc.BookServiceGrpc;
import demo.grpc.ChatMessage;
import demo.grpc.ListBooksRequest;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/** Real gRPC service implementation backing all four call shapes. */
public class BookServiceImpl extends BookServiceGrpc.BookServiceImplBase {

    private final List<Book> catalog = new ArrayList<>(List.of(
            Book.newBuilder().setId("b1").setTitle("Designing Data-Intensive Applications").setAuthor("Kleppmann").build(),
            Book.newBuilder().setId("b2").setTitle("Database Internals").setAuthor("Petrov").build(),
            Book.newBuilder().setId("b3").setTitle("Release It!").setAuthor("Nygard").build()
    ));

    final AtomicInteger addBooksReceived = new AtomicInteger(0);

    @Override
    public void getBook(BookRequest request, StreamObserver<Book> responseObserver) {
        Book found = catalog.stream().filter(b -> b.getId().equals(request.getId())).findFirst().orElse(null);
        if (found == null) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("no book with id " + request.getId())
                    .asRuntimeException());
            return;
        }
        responseObserver.onNext(found);
        responseObserver.onCompleted();
    }

    @Override
    public void listBooks(ListBooksRequest request, StreamObserver<Book> responseObserver) {
        for (Book b : catalog) {
            if (request.getAuthorFilter().isEmpty() || b.getAuthor().equals(request.getAuthorFilter())) {
                responseObserver.onNext(b);
            }
        }
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<Book> addBooks(StreamObserver<AddBooksSummary> responseObserver) {
        return new StreamObserver<Book>() {
            int count = 0;

            @Override
            public void onNext(Book value) {
                count++;
                addBooksReceived.incrementAndGet();
                catalog.add(value);
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("server: addBooks stream errored: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(AddBooksSummary.newBuilder().setAcceptedCount(count).build());
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public StreamObserver<ChatMessage> chat(StreamObserver<ChatMessage> responseObserver) {
        return new StreamObserver<ChatMessage>() {
            @Override
            public void onNext(ChatMessage value) {
                // Echo back immediately, interleaved with the client's own sends --
                // this is what "bidirectional" actually buys over two separate unary calls.
                responseObserver.onNext(ChatMessage.newBuilder()
                        .setSender("server")
                        .setText("echo: " + value.getText())
                        .build());
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("server: chat stream errored: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }
}
