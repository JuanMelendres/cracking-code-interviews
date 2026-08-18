import Link from "next/link";
import MountCounter from "../components/MountCounter";

// A NESTED layout, scoped only to routes under app/dashboard/. It
// wraps app/dashboard/page.js and app/dashboard/settings/page.js, and
// nests INSIDE the root layout (root layout's <body> -> this layout ->
// the specific dashboard page). This proves two things this chapter
// measures separately: (1) this layout persists across navigation
// WITHIN /dashboard/*, same guarantee as the root layout in F-201;
// (2) navigating AWAY to a route that does NOT share this layout
// (e.g. /about) unmounts it, because layout persistence is scoped to
// the subtree that actually uses it, not global.
export default function DashboardLayout({ children }) {
  return (
    <section data-testid="dashboard-layout">
      <MountCounter label="DashboardLayout" testId="dashboard-layout-mount-count" />
      <nav>
        <Link href="/dashboard">Dashboard home</Link> |{" "}
        <Link href="/dashboard/settings">Dashboard settings</Link>
      </nav>
      {children}
    </section>
  );
}
