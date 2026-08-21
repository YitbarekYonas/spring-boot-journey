# Week 8, Day 4: CI Basics with GitHub Actions

[![CI](https://img.shields.io/badge/CI-GitHub%20Actions-blue.svg)]()
[![Day](https://img.shields.io/badge/Day-4-blue.svg)]()
[![Topic](https://img.shields.io/badge/Topic-Continuous%20Integration-orange.svg)]()

> **"CI doesn't catch every bug. It catches the bug you would've pushed to `main` without noticing."**

---

## 🎯 Learning Objectives

- ✅ Understand what CI actually automates: build + test on every push
- ✅ Write a `.github/workflows/ci.yml` from scratch
- ✅ Understand why compile and test are separate steps
- ✅ Use dependency caching to keep pipeline runs fast
- ✅ Run the FULL Week 7 test suite — including the Testcontainers test — in CI with zero extra Docker setup
- ✅ Read a pipeline run, find a failure, and know where to look

---

## 💡 What I Learned Today

### 1. What CI Actually Does (And Doesn't)

CI (Continuous Integration) doesn't magically make code correct. It automates one specific, valuable thing: **running the same build-and-test steps, on the same clean environment, every single time code changes** — regardless of whether the person pushing remembered to run `mvn test` locally first, or whether their local Java version quietly differs from everyone else's.

The value isn't "CI is smarter than you." It's "CI never forgets to check, never skips it because it's late, and shows the result to everyone on the team, not just the person who pushed."

### 2. The Workflow File, Piece by Piece

```yaml
on:
  push:
    branches: [ "main" ]
  pull_request:
    branches: [ "main" ]
```
Runs on every push to `main` AND every pull request targeting `main`. The PR trigger matters most in practice — it means a red ❌ shows up on the PR itself, before anyone reviews or merges it, not after.

```yaml
- uses: actions/setup-java@v4
  with:
    distribution: 'temurin'
    java-version: '21'
    cache: maven
```
`cache: maven` is a single line that caches `~/.m2/repository` between runs, keyed on `pom.xml`'s contents. Without it, every single push would re-download the entire dependency tree from Maven Central — this is the CI equivalent of Day 1's Docker layer-caching trick (order your steps so the expensive, rarely-changing part gets cached).

### 3. Why Compile and Test Are Separate Steps

```yaml
- name: Build (compile only)
  run: mvn -B clean compile

- name: Run test suite
  run: mvn -B test
```
`mvn test` alone would also compile first — so why split it? **Readability of failure.** If the pipeline goes red, the very first thing you should be able to tell at a glance is "did the code not even compile, or did it compile fine but a test caught a real bug?" Those are different kinds of problems with different urgency, and separating them into two named steps means the failed step's name alone tells you which one happened, without reading a single log line.

### 4. Testcontainers in CI — The Part That "Just Works"

The Week 7 capstone's `TaskManagerCriticalFlowIT` spins up a real Postgres container via Testcontainers. Locally, that requires Docker Desktop (or equivalent) running on your machine. In this pipeline, `runs-on: ubuntu-latest` — GitHub's own hosted runners — **already have a Docker daemon installed and running by default.** No extra setup step, no `docker info` check, no service container configuration needed for this to work. `mvn test` runs, `TaskManagerCriticalFlowIT` calls out to Docker exactly like it would on your laptop, and it works.

(This is also why the compile-first / test-second split matters even more here: if the pipeline is red because Postgres/Docker had a transient issue on the runner, that's a different signal than "someone broke the code," and the step names make that distinction obvious immediately.)

### 5. A `test` Profile, Kept Deliberately Empty of Requirements

```yaml
# src/test/resources/application.yml
spring:
  profiles:
    active: test
```

`application-prod.yml` requires `${DB_URL}` with **no fallback default** (Day 3's deliberate choice — prod should fail loudly on missing config, not guess). Without pinning tests to an explicit `test` profile, they'd fall back to `dev` (which has safe localhost defaults, so it'd actually still work) — but relying on that by accident is fragile. Being explicit here means test behavior is documented, not incidental.

### 6. Publishing Results and Artifacts

```yaml
- uses: dorny/test-reporter@v1
  if: always()
```
`if: always()` is important — this step runs even when the test step above it failed, so a red build still gets a readable breakdown of exactly which tests failed, right in the GitHub Actions UI, instead of forcing you to scroll through raw Maven console output.

```yaml
- uses: actions/upload-artifact@v4
  if: success()
```
Attaches the built `.jar` to the workflow run itself — anyone can download exactly what this specific commit produced, straight from the Actions tab, without needing to check out the code and build it themselves.

---

## 🖥️ Trying This Yourself

1. Push this project to a GitHub repository (with this exact folder structure — `.github/workflows/ci.yml` must be at the repo root's `.github/workflows/` path to be picked up).
2. Go to the repo's **Actions** tab — you'll see the workflow run automatically on the push.
3. Make a small change (e.g. add a whitespace-only edit to `TaskService.java`), commit, push again.
4. Watch the **Actions** tab: a new run starts within seconds, works through Checkout → Setup JDK → Build → Test → Publish Results → Upload Artifact.
5. Try intentionally breaking a test (e.g. change an assertion in `TaskServiceTest`) and push — watch the pipeline go red, click into the failed step, and see exactly which test failed without leaving the GitHub UI.

---

## ❌ Common Mistakes

| Mistake | Fix |
|---------|-----|
| No dependency caching | Every run re-downloads the full dependency tree — `cache: maven` fixes this in one line |
| `mvn test` as the only step | Splitting compile and test makes failure diagnosis instant instead of requiring a log dive |
| Assuming Docker needs manual setup for Testcontainers in CI | GitHub-hosted `ubuntu-latest` runners already have Docker running — nothing extra needed |
| Test reporting step without `if: always()` | A failed test run skips the readable report entirely, leaving only raw console output to debug from |
| Committing the workflow file somewhere other than `.github/workflows/` | GitHub only discovers workflows at that exact path |
| Not pinning a `test` profile explicitly | Test behavior can end up silently depending on whichever profile happens to be the default |

---

## ✅ Day 4 Checklist

- [x] `.github/workflows/ci.yml` created at the correct path
- [x] Triggers on push to `main` and on pull requests targeting `main`
- [x] Checkout → Set up JDK 21 (with Maven dependency caching) → Compile → Test
- [x] All four Week 7 test layers run in CI, including the Testcontainers integration test
- [x] Test results published in a readable format, even on failure
- [x] Built jar uploaded as a downloadable workflow artifact
- [x] Verified: pushed a change, watched the pipeline run automatically

---

**Date**: August 16, 2026
**Status**: ✅ Week 8, Day 4 Complete!
**Next**: Day 5 — CD Basics: extending this pipeline to build and push a tagged Docker image to a container registry on every successful test run.

> *"A pipeline that only runs when you remember to run it isn't a pipeline — it's a suggestion. CI is the difference between 'we usually test before merging' and 'it is not possible to merge without passing tests.'"*
