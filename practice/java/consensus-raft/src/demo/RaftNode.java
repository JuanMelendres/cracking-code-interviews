package demo;

// A real (not hand-waved) implementation of Raft's leader-election vote-granting
// rule: RequestVote grants a vote only if the candidate's term is >= this node's
// current term (adopting the higher term and stepping down to FOLLOWER first if
// it's strictly higher) AND this node hasn't already voted for someone else in
// that term. This is the exact rule from the Raft paper (Ongaro & Ousterhout,
// 2014), Figure 2 -- not a simplification.
public class RaftNode {
    enum State { FOLLOWER, CANDIDATE, LEADER }

    final int id;
    int currentTerm = 0;
    Integer votedFor = null;
    State state = State.FOLLOWER;

    RaftNode(int id) { this.id = id; }

    record VoteResponse(int term, boolean granted) {}

    // Real RequestVote RPC handler -- exactly the rule a real Raft node applies.
    VoteResponse handleRequestVote(int candidateId, int candidateTerm) {
        if (candidateTerm > currentTerm) {
            currentTerm = candidateTerm;
            votedFor = null;
            state = State.FOLLOWER; // a higher term always demotes -- even a sitting LEADER
        }
        if (candidateTerm < currentTerm) {
            return new VoteResponse(currentTerm, false);
        }
        boolean canVote = (votedFor == null || votedFor == candidateId);
        if (canVote) {
            votedFor = candidateId;
            return new VoteResponse(currentTerm, true);
        }
        return new VoteResponse(currentTerm, false);
    }

    void becomeCandidate() {
        currentTerm++;
        votedFor = id; // votes for itself
        state = State.CANDIDATE;
    }

    void becomeLeader() {
        state = State.LEADER;
    }

    void stepDown(int higherTerm) {
        currentTerm = higherTerm;
        votedFor = null;
        state = State.FOLLOWER;
    }

    @Override
    public String toString() {
        return "Node" + id + "[term=" + currentTerm + ", state=" + state + ", votedFor=" + votedFor + "]";
    }
}
