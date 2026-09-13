import java.time.LocalTime;
import java.util.Map;
import java.util.Set;

// Real demo: RBAC grants/denies purely on role membership. ABAC evaluates a
// policy function over (subject attributes, resource attributes, environment
// attributes) at request time. The scenario below is a request RBAC literally
// cannot express correctly -- "an engineer may approve a deploy for their own
// team, during business hours, only if they are not the change's author" --
// while ABAC expresses it directly as one predicate.
public class RbacVsAbacDemo {

    record User(String id, String role, String team) {}
    record DeployChange(String id, String authorId, String team) {}

    // --- RBAC: static role -> permission map, no context ---
    static final Map<String, Set<String>> ROLE_PERMISSIONS = Map.of(
            "engineer", Set.of("deploy:approve"),
            "viewer", Set.of("deploy:read")
    );

    static boolean rbacAllow(User u, String permission) {
        return ROLE_PERMISSIONS.getOrDefault(u.role(), Set.of()).contains(permission);
    }

    // --- ABAC: policy evaluated over subject + resource + environment attributes ---
    static boolean abacAllow(User u, DeployChange change, LocalTime now) {
        boolean sameTeam = u.team().equals(change.team());
        boolean notAuthor = !u.id().equals(change.authorId());
        boolean businessHours = !now.isBefore(LocalTime.of(9, 0)) && !now.isAfter(LocalTime.of(18, 0));
        return "engineer".equals(u.role()) && sameTeam && notAuthor && businessHours;
    }

    public static void main(String[] args) {
        User alice = new User("alice", "engineer", "payments");
        User bob = new User("bob", "engineer", "payments");
        User carol = new User("carol", "engineer", "search"); // different team

        DeployChange change = new DeployChange("chg-42", "alice", "payments"); // alice authored it

        System.out.println("=== RBAC: role has deploy:approve? (no context at all) ===");
        for (User u : new User[] {alice, bob, carol}) {
            System.out.printf("%-6s role=%-9s rbacAllow(deploy:approve) = %s%n",
                    u.id(), u.role(), rbacAllow(u, "deploy:approve"));
        }
        System.out.println("RBAC says yes for all three -- it cannot see 'own change' or 'wrong team'.");

        System.out.println();
        System.out.println("=== ABAC: same three users, same change, evaluated at 14:00 (business hours) ===");
        LocalTime now = LocalTime.of(14, 0);
        for (User u : new User[] {alice, bob, carol}) {
            System.out.printf("%-6s team=%-8s abacAllow(chg-42) = %s%n",
                    u.id(), u.team(), abacAllow(u, change, now));
        }

        System.out.println();
        System.out.println("=== ABAC: bob approving the same change outside business hours (02:00) ===");
        System.out.println("bob    abacAllow(chg-42) @ 02:00 = " + abacAllow(bob, change, LocalTime.of(2, 0)));
    }
}
