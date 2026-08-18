import MountCounter from "../components/MountCounter";

// (marketing) is a ROUTE GROUP: the parentheses tell the App Router
// "use this folder for organization/layout scoping only — do not
// include it in the URL." This layout applies to every route inside
// this group, but this file's own presence at app/(marketing)/layout.js
// does NOT add a "/marketing" segment to any of those routes' URLs —
// verified directly in this chapter against a real build's route
// manifest and a real browser's window.location.pathname.
export default function MarketingLayout({ children }) {
  return (
    <section data-testid="marketing-layout">
      <MountCounter label="MarketingLayout" testId="marketing-layout-mount-count" />
      {children}
    </section>
  );
}
