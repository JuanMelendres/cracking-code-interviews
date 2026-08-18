/** @type {import('next').NextConfig} */
const nextConfig = {
  experimental: {
    // Without this, unauthorized() throws a real, generic React error
    // (an error digest, not the custom unauthorized.js UI, and no real
    // 401 status) -- proven directly in F-211's demo before this flag
    // was enabled.
    authInterrupts: true,
  },
  images: {
    // Without this, next/image's /_next/image endpoint real-rejects any
    // remote host with a 400 -- proven directly in F-210's demo before
    // this config existed.
    remotePatterns: [{ protocol: "https", hostname: "httpbin.org" }],
    // Required as of Next.js 16 -- an app-requested quality not in this
    // list is silently clamped by the <Image> component, but a raw
    // /_next/image request for it real-rejects with a 400 (also proven
    // directly in F-210's demo before this config existed).
    qualities: [75, 90],
  },
};

export default nextConfig;
