// Three levels deep: root layout -> DashboardLayout -> this page. Both
// ancestor layouts wrap this page automatically, purely from folder
// nesting — no explicit composition anywhere in this file.
export default function DashboardSettingsPage() {
  return (
    <div>
      <h1>Dashboard Settings — app/dashboard/settings/page.js</h1>
      <p data-testid="page-path">Route: /dashboard/settings</p>
    </div>
  );
}
