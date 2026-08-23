# Week 8, Day 6: Cloud Deployment

[![Day](https://img.shields.io/badge/Day-6-blue.svg)]()
[![Topic](https://img.shields.io/badge/Topic-Railway%20%2F%20Render%20%2F%20AWS-orange.svg)]()

> **"Everything through Day 5 proves the app works and produces a trustworthy image. Day 6 is the day someone outside your laptop can actually hit it."**

---

## ⚠️ An Honest Note Before Anything Else

I can't actually deploy this to Railway or Render from here — that needs a real account on those platforms and outbound access to their servers, neither of which this environment has. What I *can* do, and did, is prepare everything a real deployment needs: the platform config files, the exact environment variables each platform requires, and a step-by-step walkthrough — so that following it should get you a real, working public URL. Please treat the deployment steps below as a guide to execute yourself, not as something already verified end-to-end on a live account. If a step doesn't match what you see in the platform's current UI, trust the platform's own docs over this README — dashboards change more often than roadmaps do.

---

## 🎯 Learning Objectives

- ✅ Understand what changes for a Spring Boot app running on a PaaS (Platform as a Service) vs. locally
- ✅ Deploy via Render's Blueprint (`render.yaml`) — app + managed Postgres, defined as code
- ✅ Deploy via Railway, using its Postgres plugin and variable references
- ✅ Set environment variables securely on a cloud platform (not in a committed file)
- ✅ Get a conceptual overview of AWS's EC2 vs Elastic Beanstalk vs ECS landscape

---

## 💡 What's New for Cloud Deployment (vs. Local Docker Compose)

### 1. The Platform Assigns the Port — the App Must Listen on It

```yaml
# application.yml
server:
  port: ${PORT:8080}
```

Locally (Day 1–5), the app always listened on `8080`, and you mapped that to a host port yourself (`-p 8080:8080`). Render and Railway both work differently: **the platform decides the port** and tells the app via a `PORT` environment variable at startup. If the app ignores this and hardcodes `8080`, the platform's own routing layer won't find it, and requests will silently never reach the container. `${PORT:8080}` uses the platform's value when present, and falls back to `8080` for local/Compose runs where nothing sets `PORT` at all.

### 2. One `DB_HOST`/`DB_PORT`/`DB_NAME` Pattern, Three Different Sources

`application-prod.yml` builds its JDBC URL from three separate parts:
```yaml
url: jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}
```
rather than one pre-assembled `DB_URL`. This is deliberate — it's what lets the exact same `application-prod.yml` work, unmodified, across three different environments that each expose managed-database connection info in a different shape:

| Environment | How `DB_HOST`/`DB_PORT`/`DB_NAME` get set |
|---|---|
| Docker Compose (Day 2) | Hardcoded in `docker-compose.yml` (`db`, `5432`, from `.env`) |
| Render | `render.yaml`'s `fromDatabase: property: host/port/database` |
| Railway | Reference variables in Railway's dashboard (see below) |

A **real gotcha I ran into while building this**: Render's `connectionString` property returns a `postgres://user:pass@host:port/db` URL — not the `jdbc:postgresql://...` format Spring needs. Using it directly would fail at startup with a confusing driver error. `render.yaml` avoids this entirely by pulling `host`/`port`/`database` as separate values instead and letting Spring assemble the JDBC URL itself.

---

## 🚀 Option A: Deploying to Render

`render.yaml` in this repo is a **Blueprint** — infrastructure-as-code Render can read directly.

1. Push this project to a GitHub repository.
2. In the Render dashboard: **New +** → **Blueprint**.
3. Connect the repository. Render reads `render.yaml` automatically and shows you a preview: one **Web Service** (`task-manager-app`) and one **PostgreSQL** database (`task-manager-db`).
4. Click **Apply**. Render provisions both, wires the database connection env vars into the web service automatically (via the `fromDatabase` references in `render.yaml`), builds the image from the repo's `Dockerfile`, and deploys it.
5. Once deployed, Render gives you a public URL like `https://task-manager-app.onrender.com`. Test it:
   ```bash
   curl https://task-manager-app.onrender.com/actuator/health
   curl https://task-manager-app.onrender.com/api/tasks
   ```

**What `render.yaml` is doing for you, concretely:**
- `healthCheckPath: /actuator/health` — Render won't route live traffic to a new deploy until this reports healthy, so a broken deploy fails safely instead of taking down the app for real users.
- `SPRING_PROFILES_ACTIVE: prod` — activates `application-prod.yml` (quiet logs, `ddl-auto: validate`, no verbose health details) automatically.
- Free-tier note: Render's free web services spin down after inactivity and take ~30–60 seconds to wake on the next request — expected behavior, not a bug, if your first request after a while feels slow.

---

## 🚂 Option B: Deploying to Railway

Railway doesn't use a Blueprint file the way Render does for this kind of setup — `railway.json` (included) configures build/deploy behavior, but the database wiring itself happens in Railway's dashboard using its **Reference Variables** feature.

1. Push this project to a GitHub repository.
2. In Railway: **New Project** → **Deploy from GitHub repo** → select this repo. Railway detects the `Dockerfile` automatically.
3. In the same project, **+ New** → **Database** → **Add PostgreSQL**. Railway provisions a managed Postgres instance and automatically exposes variables like `PGHOST`, `PGPORT`, `PGDATABASE`, `PGUSER`, `PGPASSWORD` on that Postgres service.
4. On your **app service** (not the database), go to **Variables** and add:
   ```
   SPRING_PROFILES_ACTIVE = prod
   DB_HOST = ${{Postgres.PGHOST}}
   DB_PORT = ${{Postgres.PGPORT}}
   DB_NAME = ${{Postgres.PGDATABASE}}
   DB_USERNAME = ${{Postgres.PGUSER}}
   DB_PASSWORD = ${{Postgres.PGPASSWORD}}
   ```
   The `${{ServiceName.VARIABLE}}` syntax is Railway's **reference variable** feature — it points at another service's variable live, so if the database's credentials ever rotate, you never have to manually update the app service to match.
5. Railway assigns a public domain automatically (or generate one under **Settings → Networking → Generate Domain**). Test it the same way as Render, against your `*.up.railway.app` URL.

---

## ☁️ AWS — Conceptual Overview (Not Deployed Here)

The roadmap asks for an overview, not mastery — here's the landscape:

| Option | What it is | When it makes sense |
|---|---|---|
| **EC2** | A raw virtual machine — you install Docker, configure networking, manage OS patches yourself | Maximum control; also maximum operational burden. Choose this when you specifically need something Beanstalk/ECS don't give you |
| **Elastic Beanstalk** | A PaaS layer on top of EC2 — upload a Docker image or a jar, AWS handles provisioning, load balancing, scaling | Closest AWS equivalent to what Render/Railway just did for you above — a reasonable middle ground |
| **ECS (Elastic Container Service)** | A proper container orchestrator — define tasks/services, AWS schedules containers across a cluster (EC2-backed or serverless via Fargate) | The right choice once you have multiple services, need fine-grained scaling/networking control, or are already invested in the AWS ecosystem |

For a single Spring Boot service like this Task Manager, Render/Railway (or Elastic Beanstalk, if staying in AWS) genuinely is the pragmatic choice — ECS's orchestration power mostly pays off once you have several interdependent services, not one.

---

## 🔒 Setting Environment Variables Securely

Every platform above follows the same principle already established in Day 2/3: **secrets never live in a committed file.**
- Render: set via the dashboard, or via `fromDatabase`/`sync: false` entries in `render.yaml` (which store the *reference*, not the secret value, in git)
- Railway: set via the dashboard's **Variables** tab, or reference variables pointing at another service
- Never: hardcoding a real `DB_PASSWORD` into `render.yaml`, `railway.json`, or any file that gets committed

---

## ❌ Common Mistakes

| Mistake | Fix |
|---------|-----|
| Hardcoding `server.port: 8080` | Use `${PORT:8080}` — the platform assigns the real port at runtime |
| Using Render's `connectionString` directly as `DB_URL` | It's `postgres://`, not `jdbc:postgresql://` — build the JDBC URL from separate host/port/db values instead |
| Forgetting to set `SPRING_PROFILES_ACTIVE=prod` on the platform | The app silently falls back to the `dev` profile default, with looser settings than intended |
| No health check path configured on the platform | A broken deploy can take down the live app instead of failing safely before traffic switches over |
| Committing real database credentials into `render.yaml`/`railway.json` | Use `fromDatabase` / reference variables / the dashboard — never a literal value in a committed file |
| Assuming Docker Compose networking (`db:5432`) carries over to the cloud | Cloud platforms don't share Compose's internal DNS — connection details come from the platform's own managed-database wiring instead |

---

## ✅ Day 6 Checklist

- [x] `server.port: ${PORT:8080}` — app listens on whatever port the platform assigns
- [x] `application-prod.yml` builds its JDBC URL from `DB_HOST`/`DB_PORT`/`DB_NAME` — portable across Compose, Render, and Railway without modification
- [x] `render.yaml` Blueprint — app + managed Postgres, health check path, `fromDatabase` wiring
- [x] `railway.json` — Dockerfile-based build, health check path, restart policy
- [x] README documents exact steps for both platforms, plus Railway's reference-variable syntax
- [x] AWS EC2 vs Elastic Beanstalk vs ECS — conceptual overview, correctly scoped as "landscape," not "implemented"
- [ ] **You**: actually run the deployment on a real account and confirm the public URL responds

---

**Date**: August 16, 2026
**Status**: ✅ Week 8, Day 6 — configuration and guide complete; live deployment is your next real-world step
**Next**: Day 7 — Final Review & Polish: a `README.md` with full setup instructions, an architecture diagram, and an API documentation summary, capping off the Task Manager as a portfolio piece.

> *"A Dockerfile that runs the same everywhere is the whole promise of Day 1. Day 6 is just the day you finally point that promise at a server you don't own."*
