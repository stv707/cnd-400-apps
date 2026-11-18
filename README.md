# CND-400 Clean-Room Sample Applications (`cnd-400-apps`)

Public starter repository hosting the **5 clean-room vendor-neutral sample applications** used across **CND-400: Cloud-Native CI/CD, Git, TDD & DevSecOps** (hosted locally inside each student's self-hosted Gitea instance during labs).

> **Note:** Only starter code and intentionally broken exercise branches belong here. Reference solutions and automated lab verification scripts reside in the private `betalab-cnd-400` course repository.

## Repositories Seeded by `seed-gitea.sh`

| # | Repository | Stack | Modules / Exercises |
| :-: | :--- | :--- | :--- |
| 1 | `unit-converter` | Python 3.12 + `pytest` | Ex 1.1, Ex 2.1–2.4, Challenge Lab 1, Ex 5.1 |
| 2 | `campus-ops-java` | Spring Boot 3.3 / Java 21 / Maven 3.9 (`library-service`, `order-cart`, `math-microservices`, `freight-calculator` + `quality-smells` & `lab3-starter` overlays) | Ex 3.1, Ex 3.2, Challenge Lab 2, Ex 4.1–4.2, Challenge Lab 3, Challenge Lab 4 |
| 3 | `live-scoreboard` | Node.js 20 LTS Express + Vanilla SPA + TypeScript CLI + Playwright | Ex 3.3, Ex 5.2, Ex 5.3, Ex 6.4 |
| 4 | `smart-facility-cd` | Spring Boot 3.3 / Java 21 + Kaniko/Jib + Kubernetes YAML + `probe-availability.py` | Ex 6.1–6.3, Challenge Lab 5 |
| 5 | `payment-ledger-secops` *(Private in Gitea)* | Spring Boot 3.3 / Java 21 + legacy `Dockerfile` + DOKS RBAC | Ex 7.1–7.5, Capstone Lab 6 |

## Seeding Student Gitea (`seed-gitea.sh`)

```bash
export GITEA_URL="https://git.example.com"
export GITEA_ADMIN_USER="droot"
export GITEA_ADMIN_TOKEN="<gitea-admin-pat>"
./seed-gitea.sh
```
