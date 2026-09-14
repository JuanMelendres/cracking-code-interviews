import java.util.*;

final class Main {
    public static void main(String[] args) {
        // LC 460
        Problems.LFUCache lfu = new Problems.LFUCache(2);
        lfu.put(1, 1);
        lfu.put(2, 2);
        Check.eq(1, lfu.get(1), "LC460 get(1) after put(1,1),put(2,2) = 1");
        lfu.put(3, 3); // evicts key 2 (LFU: key1 freq=2 from the get, key2 freq=1)
        Check.eq(-1, lfu.get(2), "LC460 get(2) after eviction = -1");
        Check.eq(3, lfu.get(3), "LC460 get(3) = 3");
        lfu.put(4, 4); // evicts key 1 or 3? key1 freq=2(from earlier get)+1(from this get)... check tie logic
        // At this point: key1 freq=2 (put+get), key3 freq=2 (put+get). Both tied at freq=2; key1 is LRU among freq=2 -> evicted
        Check.eq(-1, lfu.get(1), "LC460 get(1) after tie-break eviction = -1 (least recently used among tied freq)");
        Check.eq(4, lfu.get(4), "LC460 get(4) = 4");
        Check.eq(3, lfu.get(3), "LC460 get(3) survives = 3");

        // LC 981
        Problems.TimeMap timeMap = new Problems.TimeMap();
        timeMap.set("foo", "bar", 1);
        Check.eq("bar", timeMap.get("foo", 1), "LC981 get(foo,1) = bar");
        Check.eq("bar", timeMap.get("foo", 3), "LC981 get(foo,3) = bar (no exact match, uses floor)");
        timeMap.set("foo", "bar2", 4);
        Check.eq("bar2", timeMap.get("foo", 4), "LC981 get(foo,4) = bar2");
        Check.eq("bar2", timeMap.get("foo", 8), "LC981 get(foo,8) = bar2 (floor of 4)");
        Check.eq("", timeMap.get("foo", 0), "LC981 get(foo,0) before any set = empty string");

        // LC 355
        Problems.Twitter twitter = new Problems.Twitter();
        twitter.postTweet(1, 5);
        Check.eq(List.of(5), twitter.getNewsFeed(1), "LC355 getNewsFeed(1) after own post = [5]");
        twitter.follow(1, 2);
        twitter.postTweet(2, 6);
        Check.eq(List.of(6, 5), twitter.getNewsFeed(1), "LC355 getNewsFeed(1) after following user 2 who posts 6 = [6,5]");
        twitter.unfollow(1, 2);
        Check.eq(List.of(5), twitter.getNewsFeed(1), "LC355 getNewsFeed(1) after unfollow = [5]");

        // LC 1472
        Problems.BrowserHistory browser = new Problems.BrowserHistory("leetcode.com");
        browser.visit("google.com");
        browser.visit("facebook.com");
        browser.visit("youtube.com");
        Check.eq("facebook.com", browser.back(1), "LC1472 back(1) from youtube = facebook.com");
        Check.eq("google.com", browser.back(1), "LC1472 back(1) again = google.com");
        Check.eq("facebook.com", browser.forward(1), "LC1472 forward(1) = facebook.com");
        browser.visit("linkedin.com"); // discards forward history (facebook.com, youtube.com)
        Check.eq("google.com", browser.back(2), "LC1472 back(2) after new visit clips forward history, clamps at google.com");
        Check.eq("linkedin.com", browser.forward(2), "LC1472 forward(2) clamps at linkedin.com (youtube.com discarded)");

        // LC 359
        Problems.Logger logger = new Problems.Logger();
        Check.isTrue(logger.shouldPrintMessage(1, "foo"), "LC359 shouldPrintMessage(1,foo) -> true (first time)");
        Check.isTrue(!logger.shouldPrintMessage(2, "foo"), "LC359 shouldPrintMessage(2,foo) -> false (within 10s)");
        Check.isTrue(!logger.shouldPrintMessage(10, "foo"), "LC359 shouldPrintMessage(10,foo) -> false (still within 10s window, 10-1=9)");
        Check.isTrue(logger.shouldPrintMessage(11, "foo"), "LC359 shouldPrintMessage(11,foo) -> true (11-1=10, window elapsed)");

        Check.summary("Week 22 — Design (LC 460, 981, 355, 1472, 359)");
    }
}
