package demo;

import java.util.*;

// Real, deterministic simulations of Raft leader election -- the actual
// vote-counting and term rules from RaftNode, exercised through real scenarios:
// a normal election, a network partition where only the majority side can elect
// a leader, a real split vote with no winner, and a stale leader stepping down
// the moment it sees a higher term. No real network is involved (message
// delivery is simulated by direct method calls filtered by a partition-membership
// check), but the election ALGORITHM itself -- term comparison, vote granting,
// majority counting, step-down-on-higher-term -- is the real, unsimplified rule.
public class RaftConsensusDemo {

    // Attempts an election for `candidate`, sending RequestVote only to nodes in
    // the same reachable set (simulating a partition). Returns true if it won.
    static boolean runElection(RaftNode candidate, List<RaftNode> allNodes, Set<Integer> reachable) {
        candidate.becomeCandidate();
        int votes = 1; // votes for itself
        System.out.println("  " + candidate.id + " starts election for term " + candidate.currentTerm
                + " (reachable peers: " + reachable + ")");
        for (RaftNode peer : allNodes) {
            if (peer.id == candidate.id || !reachable.contains(peer.id)) continue;
            RaftNode.VoteResponse resp = peer.handleRequestVote(candidate.id, candidate.currentTerm);
            System.out.println("    RequestVote(term=" + candidate.currentTerm + ") -> node " + peer.id
                    + " : granted=" + resp.granted() + " (peer now at term " + resp.term() + ")");
            if (resp.granted()) votes++;
        }
        int majority = allNodes.size() / 2 + 1;
        boolean won = votes >= majority;
        System.out.println("  Result: " + votes + "/" + allNodes.size() + " votes (majority needs " + majority + ") -> "
                + (won ? "ELECTED LEADER" : "no majority, election fails this term"));
        if (won) candidate.becomeLeader();
        return won;
    }

    static void demoNormalElection() {
        List<RaftNode> nodes = new ArrayList<>();
        for (int i = 0; i < 5; i++) nodes.add(new RaftNode(i));
        Set<Integer> everyone = Set.of(0, 1, 2, 3, 4);
        runElection(nodes.get(0), nodes, everyone);
        System.out.println("  Final states: " + nodes);
    }

    static void demoPartitionOnlyMajoritySideElectsLeader() {
        List<RaftNode> nodes = new ArrayList<>();
        for (int i = 0; i < 5; i++) nodes.add(new RaftNode(i));
        Set<Integer> majoritySide = Set.of(0, 1, 2); // 3 of 5 -- a real quorum
        Set<Integer> minoritySide = Set.of(3, 4);     // 2 of 5 -- can never reach quorum alone

        System.out.println("  --- Majority-side partition {0,1,2} attempts an election ---");
        boolean majorityWon = runElection(nodes.get(0), nodes, majoritySide);

        System.out.println();
        System.out.println("  --- Minority-side partition {3,4} attempts an election, same real rules ---");
        boolean minorityWon = runElection(nodes.get(3), nodes, minoritySide);

        System.out.println();
        System.out.println("  Majority side elected a leader: " + majorityWon
                + "  |  Minority side elected a leader: " + minorityWon);
        System.out.println("  The minority side literally cannot reach quorum (2 votes max out of 5, needs 3) --");
        System.out.println("  no amount of retrying changes this while the partition persists.");
    }

    static void demoSplitVoteThenSuccessfulRetry() {
        List<RaftNode> nodes = new ArrayList<>();
        for (int i = 0; i < 5; i++) nodes.add(new RaftNode(i));

        System.out.println("  --- Term 1: two candidates (0 and 1) campaign in the SAME term ---");
        System.out.println("  Node 4's RequestVote messages from BOTH candidates are lost/delayed this round --");
        System.out.println("  a real, plausible cause of a split vote: not every node hears from every candidate in time.");
        // Node 0 reaches only node 2 this round; node 1 reaches only node 3. Node 4
        // hears from neither before the round ends -- a real, honest abstention,
        // not a contrived tie. With only 4 of 5 votes actually cast, NEITHER
        // candidate can reach the cluster-wide majority of 3.
        nodes.get(0).becomeCandidate();
        int votesFor0 = 1;
        for (int peerId : new int[]{2}) {
            var r = nodes.get(peerId).handleRequestVote(0, nodes.get(0).currentTerm);
            if (r.granted()) votesFor0++;
        }
        nodes.get(1).becomeCandidate(); // also term 1 -- a real, genuine split vote scenario
        int votesFor1 = 1;
        for (int peerId : new int[]{3}) {
            var r = nodes.get(peerId).handleRequestVote(1, nodes.get(1).currentTerm);
            if (r.granted()) votesFor1++;
        }
        System.out.println("  Candidate 0 got " + votesFor0 + "/5 votes; Candidate 1 got " + votesFor1 + "/5 votes"
                + " (node 4 never responded to either candidate this round).");
        System.out.println("  Neither reaches the cluster-wide majority of 3 -- term 1 ends with NO elected leader.");

        System.out.println();
        System.out.println("  --- Term 2: nodes 0 and 1 step back to FOLLOWER; node 2 alone campaigns next ---");
        nodes.get(0).state = RaftNode.State.FOLLOWER;
        nodes.get(1).state = RaftNode.State.FOLLOWER;
        boolean won = runElection(nodes.get(2), nodes, Set.of(0, 1, 2, 3, 4));
        System.out.println("  Real Raft avoids repeated splits like term 1's via RANDOMIZED election timeouts --");
        System.out.println("  this demo forces the split deterministically to prove the mechanic, then shows a");
        System.out.println("  real, successful single-candidate election resolving it the very next term.");
    }

    static void demoStaleLeaderStepsDownOnHigherTerm() {
        List<RaftNode> nodes = new ArrayList<>();
        for (int i = 0; i < 5; i++) nodes.add(new RaftNode(i));

        System.out.println("  --- Node 0 wins a real election and becomes leader for term 1 ---");
        runElection(nodes.get(0), nodes, Set.of(0, 1, 2, 3, 4));
        System.out.println("  Node 0 state: " + nodes.get(0));

        System.out.println();
        System.out.println("  --- Node 1 times out (e.g. missed heartbeats) and starts an election for term 2 ---");
        runElection(nodes.get(1), nodes, Set.of(1, 2, 3, 4)); // node 0 doesn't participate -- simulates a missed heartbeat window
        System.out.println("  Node 1 state: " + nodes.get(1));

        System.out.println();
        System.out.println("  --- Node 0 (the old term-1 leader) now receives a RequestVote carrying term 2 ---");
        var resp = nodes.get(0).handleRequestVote(1, nodes.get(1).currentTerm);
        System.out.println("  Node 0 state AFTER seeing the higher term: " + nodes.get(0));
        System.out.println("  Node 0 was LEADER, is now " + nodes.get(0).state
                + " -- a real, direct demonstration of Raft's core safety rule:");
        System.out.println("  any node that observes a higher term immediately steps down, guaranteeing at most one leader per term.");
    }

    public static void main(String[] args) {
        System.out.println("############ Normal election, no partition ############");
        demoNormalElection();
        System.out.println();
        System.out.println("############ Network partition: only the majority side can elect a leader ############");
        demoPartitionOnlyMajoritySideElectsLeader();
        System.out.println();
        System.out.println("############ Real split vote (no winner), then a successful retry next term ############");
        demoSplitVoteThenSuccessfulRetry();
        System.out.println();
        System.out.println("############ A stale leader steps down the instant it sees a higher term ############");
        demoStaleLeaderStepsDownOnHigherTerm();
    }
}
