# Week 8, Day 5: CD Basics & Build Artifacts

[![CI/CD](https://img.shields.io/badge/CI%2FCD-GitHub%20Actions-blue.svg)]()
[![Day](https://img.shields.io/badge/Day-5-blue.svg)]()
[![Topic](https://img.shields.io/badge/Topic-Continuous%20Delivery-orange.svg)]()

> **"CI answers 'does it work?' CD answers 'is a working copy sitting somewhere I can actually deploy from?' Different questions, and you need both answered before every merge."**

---

## 🎯 Learning Objectives

- ✅ Understand the difference between CI and CD, and why CD should depend on CI passing
- ✅ Add a second job to the Day 4 workflow that builds and pushes a Docker image
- ✅ Understand image tagging — why `latest` alone isn't enough
- ✅ Authenticate to a container registry from a GitHub Actions workflow
- ✅ Understand Docker layer caching *across workflow runs*, not just within one build
- ✅ Get an overview of deployment strategies (manual vs automated, blue-green) at a conceptual level

---

## 💡 What I Learned Today

### 1. CI vs CD — Two Different Questions

| | CI (Day 4) | CD (today) |
|---|---|---|
| Question it answers | "Does this code work?" | "Is a deployable artifact of this code sitting somewhere ready to ship?" |
| Runs on | Every push + every PR | Only on `main`, only after CI passes |
| Output | A pass/fail signal + test report | A tagged, pushed Docker image |

The critical link is `needs: build-and-test` on the second job — **CD is gated behind CI**, not parallel to it. A failing test means no image is ever built or pushed, full stop.

### 2. Why the Image-Push Job Only Runs on `main`

```yaml
if: github.event_name == 'push' && github.ref == 'refs/heads/main'
```

A pull request — especially one from a fork — shouldn't be able to push images to your registry using your credentials. And a not-yet-merged feature branch publishing an image that looks like a real, deployable artifact is confusing at best, a security concern at worst. Only a push that's actually landed on `main` triggers a real image push.

### 3. Tagging — Why `latest` Alone Isn't Enough

```yaml
tags: |
  type=raw,value=latest
  type=sha,prefix=,format=short
```

`latest` is convenient but tells you **nothing** about which commit produced it — if something breaks in production, "which version is actually running?" becomes an unanswerable question if `latest` is the only tag you ever push. Also tagging with the short git SHA (e.g. `ghcr.io/you/task-manager:a1b2c3d`) means every image is traceable back to the exact commit that built it — you can always answer "what code is this container actually running?" precisely.

### 4. Registry Authentication — Using the Built-In Token

```yaml
- uses: docker/login-action@v3
  with:
    registry: ghcr.io
    username: ${{ github.actor }}
    password: ${{ secrets.GITHUB_TOKEN }}
```

This project uses **GitHub Container Registry (ghcr.io)** rather than Docker Hub specifically because it needs **zero extra secrets configured**: `secrets.GITHUB_TOKEN` is automatically provided by GitHub Actions to every workflow run, already scoped with permission to push to your own repository's package registry.

**Using Docker Hub instead** (what the roadmap describes) needs two repo secrets you'd add yourself under *Settings → Secrets and variables → Actions*:
```yaml
- uses: docker/login-action@v3
  with:
    username: ${{ secrets.DOCKERHUB_USERNAME }}
    password: ${{ secrets.DOCKERHUB_TOKEN }}   # a Docker Hub access token, not your account password
# and change the image reference in the metadata step to:
#   images: yourdockerhubusername/task-manager
```
Both are valid — ghcr.io is simpler to get started with since it needs no separate account/token setup; Docker Hub is what most tutorials (and this roadmap) default to and is worth knowing too.

### 5. Caching Across Workflow Runs (Not Just Within One Build)

```yaml
cache-from: type=gha
cache-to: type=gha,mode=max
```

Day 1's `Dockerfile` already has layer caching *within a single build* (copy `pom.xml` before `src`). This is the same idea applied *across separate CI runs*: GitHub Actions' own cache backend stores Docker layers between workflow runs, so a push that only changes application code (not `pom.xml`) can skip re-downloading dependencies entirely, even in a brand-new runner VM that's never seen this repo before.

### 6. Deployment Strategies — A Conceptual Overview

This project pushes an image; it does **not** deploy it anywhere yet (that's genuinely Day 6's job). But it's worth understanding the landscape before getting there:

- **Manual deployment** — someone runs `docker pull` + `docker run` (or updates a Compose file) on a server by hand. Simple, but slow and error-prone as you scale past one person or one server.
- **Automated deployment** — a pipeline step (not covered in this project) automatically deploys the newly-pushed image to a target environment on every successful push to `main`. This is the natural next step once CD reliably produces a trustworthy image, which is exactly what today's workflow sets up.
- **Blue-green deployment** (conceptual only) — you run two identical production environments ("blue" and "green"). At any moment, only one is receiving live traffic. To deploy a new version, you deploy it to the *idle* environment, verify it's healthy, then switch traffic over — instantly, and with an instant rollback available (just switch traffic back) if something's wrong. The trade-off is cost: you're running double the infrastructure, at least during the switch window.

None of these are implemented here — this project's job is specifically "produce a trustworthy artifact automatically." *Where* that artifact goes is a separate, later concern.

---

## 🖥️ Trying This Yourself

1. Push this project to a GitHub repo.
2. Go to **Settings → Actions → General → Workflow permissions**, and ensure "Read and write permissions" is enabled — `GITHUB_TOKEN` needs write access to push to `ghcr.io`.
3. Push a commit to `main`. Watch the **Actions** tab: `build-and-test` runs first; only once it's green does `build-and-push-image` start.
4. Once it finishes, check your GitHub profile/org's **Packages** tab — you'll see the pushed image, tagged both `latest` and with the commit's short SHA.
5. Pull it yourself: `docker pull ghcr.io/<your-username>/<your-repo>:latest`

---

## ❌ Common Mistakes

| Mistake | Fix |
|---------|-----|
| Pushing images on every PR, including from forks | Gate the push job to `push` events on `main` only |
| No `needs:` dependency between the test job and the push job | An image can get pushed even when tests fail — defeats the entire purpose |
| Only ever tagging `latest` | Add a commit-SHA tag too, so every image is traceable to exact source |
| Storing Docker Hub credentials as your actual account password | Use a Docker Hub **access token**, scoped and revocable, never your real password |
| Rebuilding the image with a different, "CI-only" Dockerfile | Reuse the same Dockerfile as local dev — what you test locally should be what ships |
| No build cache across CI runs | `cache-from`/`cache-to: type=gha` turns "download every dependency on every push" into "download once, reuse after" |

---

## ✅ Day 5 Checklist

- [x] Second workflow job (`build-and-push-image`) added, gated behind `needs: build-and-test`
- [x] Runs only on pushes to `main`, not on pull requests
- [x] Authenticates to a container registry using a token, not a hardcoded password
- [x] Tags include both `latest` and a commit-SHA-based tag
- [x] Reuses the same `Dockerfile` from Day 1 — no separate CI-only build definition
- [x] Docker layer caching enabled across workflow runs (`type=gha`)
- [x] Verified: pushed to `main`, watched both jobs run in sequence, confirmed the image appeared in the registry

---

**Date**: August 16, 2026
**Status**: ✅ Week 8, Day 5 Complete!
**Next**: Day 6 — Cloud Deployment: actually deploying this pushed image to Railway or Render, connecting it to a real managed Postgres instance, with a live public URL.

> *"An image sitting in a registry, tagged to a commit, built by a pipeline that only runs after tests pass — that's not just automation. That's a paper trail for 'what is actually running in production,' which is worth more than the automation itself."*
