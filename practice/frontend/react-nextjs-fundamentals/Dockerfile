# F-213: a real, standard multi-stage Dockerfile following the official
# Next.js Docker example pattern (output: "standalone" in next.config.mjs
# is what makes this possible -- see handbook/frontend/
# nextjs-server-actions-and-mutations.md's sibling chapter,
# nextjs-deployment-models.md, for the real, captured evidence this
# Dockerfile is built on: a real ~13x size reduction (543MB naive vs 42MB
# standalone) and a real, reproduced "missing static assets" failure mode
# if the manual COPY steps below are ever dropped.
#
# NOT executed with `docker build`/`docker run` in the session that wrote
# this file -- the local Docker daemon was not running. The underlying
# `.next/standalone` output this Dockerfile packages WAS verified
# directly with plain `node .next/standalone/server.js` (see the chapter's
# Real Verified Demos section). Run `docker build -t nextjs-fundamentals .`
# yourself to exercise this file end to end.

FROM node:22-alpine AS base

FROM base AS deps
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci

FROM base AS builder
WORKDIR /app
COPY --from=deps /app/node_modules ./node_modules
COPY . .
RUN npm run build

FROM base AS runner
WORKDIR /app
ENV NODE_ENV=production

RUN addgroup --system --gid 1001 nodejs \
  && adduser --system --uid 1001 nextjs

# Only the files standalone output actually needs -- NOT the full
# node_modules tree, per this chapter's real, measured size comparison.
COPY --from=builder /app/public ./public
COPY --from=builder --chown=nextjs:nodejs /app/.next/standalone ./
COPY --from=builder --chown=nextjs:nodejs /app/.next/static ./.next/static

USER nextjs
EXPOSE 3000
ENV PORT=3000
ENV HOSTNAME="0.0.0.0"

CMD ["node", "server.js"]
