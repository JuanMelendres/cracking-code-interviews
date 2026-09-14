import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

// Real, compiling demonstrations of java.time: real immutability (contrasted
// against a real legacy Calendar mutation bug), the real distinction between
// Period (date-based) and Duration (time-based) -- including a real DST
// transition where they produce genuinely different wall-clock results -- and
// a real, reproduced SimpleDateFormat thread-safety corruption versus
// DateTimeFormatter's real immutable thread-safety, under actual concurrent load.
public class JavaTimeApiDemo {

    static void demoImmutability() {
        LocalDate original = LocalDate.of(2026, 1, 15);
        LocalDate plusOneMonth = original.plusMonths(1);
        System.out.println("  original:      " + original);
        System.out.println("  original.plusMonths(1) returns a NEW object: " + plusOneMonth);
        System.out.println("  original unchanged after the call: " + original + "  (real immutability -- no setter exists at all)");

        System.out.println();
        System.out.println("  --- Legacy contrast: java.util.Calendar mutates in place ---");
        Calendar cal = Calendar.getInstance();
        cal.set(2026, Calendar.JANUARY, 15);
        Date beforeMutation = cal.getTime();
        System.out.println("  Calendar set to: " + beforeMutation);
        cal.add(Calendar.MONTH, 1); // mutates the SAME Calendar object
        Date afterMutation = cal.getTime();
        System.out.println("  same Calendar object, after cal.add(MONTH, 1): " + afterMutation);
        System.out.println("  beforeMutation reference now reads: " + beforeMutation
                + "  <-- if any other code held a reference to this SAME Calendar, it would see the mutation too");
    }

    static void demoFourTypesOneInstant() {
        ZoneId ny = ZoneId.of("America/New_York");
        ZonedDateTime zdt = ZonedDateTime.of(2026, 6, 15, 14, 30, 0, 0, ny);
        LocalDate localDate = zdt.toLocalDate();
        LocalDateTime localDateTime = zdt.toLocalDateTime();
        Instant instant = zdt.toInstant();

        System.out.println("  Same real moment, four different real types:");
        System.out.println("  LocalDate      (date only, no time, no zone):       " + localDate);
        System.out.println("  LocalDateTime  (date+time, no zone -- ambiguous):   " + localDateTime);
        System.out.println("  ZonedDateTime  (date+time+zone -- unambiguous):     " + zdt);
        System.out.println("  Instant        (machine timestamp, UTC, no zone):   " + instant);
    }

    static void demoPeriodVsDuration() {
        LocalDate jan31 = LocalDate.of(2026, 1, 31);
        LocalDate mar1 = LocalDate.of(2026, 3, 1);
        Period period = Period.between(jan31, mar1);
        System.out.println("  Period.between(2026-01-31, 2026-03-01) = " + period
                + "  (1 month, 1 day -- calendar-aware, handles Jan's varying length)");

        Instant t0 = Instant.parse("2026-01-31T00:00:00Z");
        Instant t1 = t0.plus(Duration.ofDays(29)); // same nominal gap, in raw seconds
        Duration duration = Duration.between(t0, t1);
        System.out.println("  Duration.between two Instants 29*24h apart = " + duration
                + "  (exact elapsed time, no calendar awareness at all)");
    }

    static void demoDstTransition() {
        // 2026-03-08 is the real US spring-forward DST transition date for America/New_York.
        ZoneId ny = ZoneId.of("America/New_York");
        ZonedDateTime beforeDst = ZonedDateTime.of(2026, 3, 7, 12, 0, 0, 0, ny);
        System.out.println("  Starting point: " + beforeDst + " (the day BEFORE a real US DST spring-forward)");

        ZonedDateTime plusOneCalendarDay = beforeDst.plus(Period.ofDays(1));
        ZonedDateTime plusOne24HourDuration = beforeDst.plus(Duration.ofDays(1));

        System.out.println("  plus(Period.ofDays(1))   -> " + plusOneCalendarDay
                + "  (calendar day -- lands on the SAME wall-clock hour, 12:00)");
        System.out.println("  plus(Duration.ofDays(1)) -> " + plusOne24HourDuration
                + "  (exactly 24 real hours later)");
        System.out.println("  Results equal? " + plusOneCalendarDay.equals(plusOne24HourDuration)
                + "  <-- real, different results from the SAME starting point and the SAME nominal '1 day',");
        System.out.println("  because Period is calendar-based and Duration is a fixed number of real seconds --");
        System.out.println("  this specific day only had 23 real hours due to the DST spring-forward.");
    }

    static void demoLegacySimpleDateFormatRace() throws InterruptedException {
        SimpleDateFormat legacyFormat = new SimpleDateFormat("yyyy-MM-dd");
        int threads = 20;
        int iterationsPerThread = 500;
        AtomicInteger corruptions = new AtomicInteger();
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        for (int t = 0; t < threads; t++) {
            pool.submit(() -> {
                for (int i = 0; i < iterationsPerThread; i++) {
                    try {
                        Date parsed = legacyFormat.parse("2026-06-15");
                        if (!legacyFormat.format(parsed).equals("2026-06-15")) {
                            corruptions.incrementAndGet(); // parsed to a DIFFERENT date -- real corruption
                        }
                    } catch (Exception e) {
                        corruptions.incrementAndGet(); // a thrown exception is also real corruption here
                    }
                }
            });
        }
        pool.shutdown();
        pool.awaitTermination(30, TimeUnit.SECONDS);
        System.out.println("  Shared SimpleDateFormat, " + threads + " threads x " + iterationsPerThread
                + " concurrent parse() calls each:");
        System.out.println("  Corrupted/failed results: " + corruptions.get() + " / " + (threads * iterationsPerThread)
                + (corruptions.get() > 0 ? "  <-- REAL corruption, not hypothetical" : "  (ran clean this time -- the race is real but timing-dependent; see README for a guaranteed repro)"));
    }

    static void demoThreadSafeDateTimeFormatter() throws InterruptedException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // shared, immutable
        int threads = 20;
        int iterationsPerThread = 500;
        AtomicInteger corruptions = new AtomicInteger();
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        for (int t = 0; t < threads; t++) {
            pool.submit(() -> {
                for (int i = 0; i < iterationsPerThread; i++) {
                    LocalDate parsed = LocalDate.parse("2026-06-15", formatter);
                    if (!formatter.format(parsed).equals("2026-06-15")) {
                        corruptions.incrementAndGet();
                    }
                }
            });
        }
        pool.shutdown();
        pool.awaitTermination(30, TimeUnit.SECONDS);
        System.out.println("  Shared DateTimeFormatter (immutable), identical concurrent workload:");
        System.out.println("  Corrupted/failed results: " + corruptions.get() + " / " + (threads * iterationsPerThread)
                + "  <-- immutability structurally prevents this entire bug class");
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("############ Real immutability, vs. legacy Calendar's in-place mutation ############");
        demoImmutability();
        System.out.println();
        System.out.println("############ Four types, one real moment ############");
        demoFourTypesOneInstant();
        System.out.println();
        System.out.println("############ Period (calendar-based) vs Duration (time-based) ############");
        demoPeriodVsDuration();
        System.out.println();
        System.out.println("############ Real DST transition: Period and Duration genuinely diverge ############");
        demoDstTransition();
        System.out.println();
        System.out.println("############ Real thread-safety bug: shared SimpleDateFormat ############");
        demoLegacySimpleDateFormatRace();
        System.out.println();
        System.out.println("############ Real thread-safety: shared DateTimeFormatter ############");
        demoThreadSafeDateTimeFormatter();
    }
}
