import java.util.*;

final class Problems {

    // ---- LC 460: LFU Cache (O(1) get/put) ----
    static class LFUCache {
        private static class Node {
            int key, value, freq = 1;
            Node(int key, int value) { this.key = key; this.value = value; }
        }

        private final int capacity;
        private final Map<Integer, Node> keyToNode = new HashMap<>();
        private final Map<Integer, LinkedHashSet<Node>> freqToNodes = new HashMap<>();
        private int minFreq = 0;

        LFUCache(int capacity) {
            this.capacity = capacity;
        }

        int get(int key) {
            Node node = keyToNode.get(key);
            if (node == null) return -1;
            touch(node);
            return node.value;
        }

        void put(int key, int value) {
            if (capacity <= 0) return;
            Node existing = keyToNode.get(key);
            if (existing != null) {
                existing.value = value;
                touch(existing);
                return;
            }
            if (keyToNode.size() == capacity) {
                LinkedHashSet<Node> minBucket = freqToNodes.get(minFreq);
                Node evict = minBucket.iterator().next();
                minBucket.remove(evict);
                keyToNode.remove(evict.key);
            }
            Node node = new Node(key, value);
            keyToNode.put(key, node);
            freqToNodes.computeIfAbsent(1, f -> new LinkedHashSet<>()).add(node);
            minFreq = 1;
        }

        private void touch(Node node) {
            int oldFreq = node.freq;
            freqToNodes.get(oldFreq).remove(node);
            if (freqToNodes.get(oldFreq).isEmpty()) {
                freqToNodes.remove(oldFreq);
                if (minFreq == oldFreq) minFreq++;
            }
            node.freq++;
            freqToNodes.computeIfAbsent(node.freq, f -> new LinkedHashSet<>()).add(node);
        }
    }

    // ---- LC 981: Time Based Key-Value Store ----
    static class TimeMap {
        private final Map<String, List<long[]>> store = new HashMap<>(); // value packed as [timestamp, valueIndex]
        private final Map<String, List<String>> values = new HashMap<>();

        void set(String key, String value, int timestamp) {
            store.computeIfAbsent(key, k -> new ArrayList<>()).add(new long[]{timestamp});
            values.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
        }

        String get(String key, int timestamp) {
            List<long[]> timestamps = store.get(key);
            if (timestamps == null) return "";
            int lo = 0, hi = timestamps.size() - 1, result = -1;
            while (lo <= hi) {
                int mid = lo + (hi - lo) / 2;
                if (timestamps.get(mid)[0] <= timestamp) { result = mid; lo = mid + 1; }
                else hi = mid - 1;
            }
            return result == -1 ? "" : values.get(key).get(result);
        }
    }

    // ---- LC 355: Design Twitter ----
    static class Twitter {
        private int timeCounter = 0;
        private final Map<Integer, List<int[]>> userTweets = new HashMap<>(); // userId -> [time, tweetId]
        private final Map<Integer, Set<Integer>> follows = new HashMap<>();

        void postTweet(int userId, int tweetId) {
            userTweets.computeIfAbsent(userId, u -> new ArrayList<>()).add(new int[]{timeCounter++, tweetId});
        }

        List<Integer> getNewsFeed(int userId) {
            PriorityQueue<int[]> maxHeap = new PriorityQueue<>((a, b) -> b[0] - a[0]); // by time desc
            Set<Integer> sources = new HashSet<>(follows.getOrDefault(userId, Set.of()));
            sources.add(userId);
            for (int source : sources) {
                List<int[]> tweets = userTweets.get(source);
                if (tweets == null) continue;
                for (int i = tweets.size() - 1; i >= Math.max(0, tweets.size() - 10); i--) {
                    maxHeap.offer(tweets.get(i));
                }
            }
            List<Integer> result = new ArrayList<>();
            while (!maxHeap.isEmpty() && result.size() < 10) {
                result.add(maxHeap.poll()[1]);
            }
            return result;
        }

        void follow(int followerId, int followeeId) {
            if (followerId == followeeId) return;
            follows.computeIfAbsent(followerId, f -> new HashSet<>()).add(followeeId);
        }

        void unfollow(int followerId, int followeeId) {
            Set<Integer> set = follows.get(followerId);
            if (set != null) set.remove(followeeId);
        }
    }

    // ---- LC 1472: Design Browser History ----
    static class BrowserHistory {
        private final List<String> history = new ArrayList<>();
        private int current = 0;

        BrowserHistory(String homepage) {
            history.add(homepage);
        }

        void visit(String url) {
            history.subList(current + 1, history.size()).clear(); // discard forward history
            history.add(url);
            current++;
        }

        String back(int steps) {
            current = Math.max(0, current - steps);
            return history.get(current);
        }

        String forward(int steps) {
            current = Math.min(history.size() - 1, current + steps);
            return history.get(current);
        }
    }

    // ---- LC 359: Logger Rate Limiter ----
    static class Logger {
        private final Map<String, Integer> lastPrinted = new HashMap<>();

        boolean shouldPrintMessage(int timestamp, String message) {
            Integer last = lastPrinted.get(message);
            if (last != null && timestamp - last < 10) return false;
            lastPrinted.put(message, timestamp);
            return true;
        }
    }
}
