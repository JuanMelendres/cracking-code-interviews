import "./globals.css";
import PersistentHeader from "./components/PersistentHeader";

// metadataBase lets every URL-based field below (OG images, canonical
// links) use a relative path instead of a required absolute URL --
// F-209's demo proves this resolves for real, and that removing this
// line while a relative OG image path exists is a real build error.
export const metadata = {
  metadataBase: new URL("http://localhost:5198"),
  title: {
    default: "React + Next.js Fundamentals — F-201",
    template: "%s | Next.js Fundamentals Demo",
  },
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
