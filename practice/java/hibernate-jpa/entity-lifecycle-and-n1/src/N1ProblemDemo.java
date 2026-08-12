import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.stat.Statistics;

import java.util.List;

class N1ProblemDemo {
    private static final int AUTHOR_COUNT = 5;
    private static final int BOOKS_PER_AUTHOR = 3;

    public static void main(String[] args) {
        SessionFactory sf = HibernateSupport.buildSessionFactory(false); // SQL printed manually below via statistics + explicit markers
        Statistics stats = sf.getStatistics();

        try (Session session = sf.openSession()) {
            Transaction tx = session.beginTransaction();
            for (int i = 1; i <= AUTHOR_COUNT; i++) {
                Author author = new Author("Author " + i);
                for (int j = 1; j <= BOOKS_PER_AUTHOR; j++) {
                    author.addBook(new Book("Author " + i + "'s Book " + j));
                }
                session.persist(author);
            }
            tx.commit();
        }
        System.out.println("Seeded " + AUTHOR_COUNT + " authors, " + (AUTHOR_COUNT * BOOKS_PER_AUTHOR) + " books.");

        System.out.println();
        System.out.println("== N+1, measured: fetch all authors, then touch each author's LAZY books collection ==");
        stats.clear();
        try (Session session = sf.openSession()) {
            List<Author> authors = session.createQuery("select a from Author a", Author.class).list();
            long afterInitialQuery = stats.getPrepareStatementCount();
            System.out.println("Prepared statements after the initial author query: " + afterInitialQuery + "  (1 -- just the SELECT for all authors)");

            for (Author author : authors) {
                int bookCount = author.getBooks().size(); // each call lazily triggers its OWN SELECT
            }
            long afterTouchingBooks = stats.getPrepareStatementCount();
            System.out.println("Prepared statements after touching " + AUTHOR_COUNT + " authors' lazy books collections: " + afterTouchingBooks);
            System.out.println("RESULT: " + afterInitialQuery + " initial query + " + (afterTouchingBooks - afterInitialQuery) + " lazy-load queries (one per author) = " + afterTouchingBooks + " total -- the classic N+1.");
        }

        System.out.println();
        System.out.println("== The fix, measured: JOIN FETCH pulls authors AND their books in a single query ==");
        stats.clear();
        try (Session session = sf.openSession()) {
            List<Author> authors = session.createQuery(
                "select distinct a from Author a join fetch a.books", Author.class
            ).list();

            for (Author author : authors) {
                int bookCount = author.getBooks().size(); // already loaded -- no additional query
            }
            long total = stats.getPrepareStatementCount();
            System.out.println("Prepared statements for the same " + AUTHOR_COUNT + " authors + their books, via JOIN FETCH: " + total);
            System.out.println("RESULT: " + total + " query total -- N+1 eliminated by fetching the association eagerly, for THIS specific access pattern, in THIS specific query.");
        }

        sf.close();
    }
}
