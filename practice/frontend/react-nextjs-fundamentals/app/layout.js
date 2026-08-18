import "./globals.css";
import PersistentHeader from "./components/PersistentHeader";

export const metadata = {
  title: "React + Next.js Fundamentals — F-201",
  description: "Real demo app backing handbook/frontend/nextjs-fundamentals.md",
};

// Real demo app backing handbook/frontend/nextjs-fundamentals.md (F-201).
// This root layout wraps every route below via the {children} prop —
// exactly the mechanism that produces the mount-count evidence in
// PersistentHeader.js, captured in README.md.
export default function RootLayout({ children }) {
  return (
    <html lang="en">
      <body>
        <PersistentHeader />
        <main>{children}</main>
      </body>
    </html>
  );
}
