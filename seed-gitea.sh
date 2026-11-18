#!/bin/bash
set -euo pipefail

# ==============================================================================
# CND-400 Gitea & ~/exercises Repository Seeder
# ==============================================================================
# Splits this monorepo directory into 5 independent Gitea repositories under
# ${GITEA_ORG} and prepares clean student working copies in ${EXERCISES_DIR}.
#
# Required Environment Variables:
#   GITEA_URL          Base URL (e.g. https://git.cnd400-stu01.lab.example.com)
#   GITEA_ADMIN_USER   Gitea admin username (default: droot)
#   GITEA_ADMIN_TOKEN  Gitea personal access token with org/repo admin scopes
# Optional Environment Variables:
#   GITEA_ORG          Target Gitea organization (default: cloudops-labs)
#   EXERCISES_DIR      Student workspace directory (default: /home/droot/exercises)
#   STUDENT_USER       Local Linux owner for EXERCISES_DIR (default: droot)
# ==============================================================================

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
GITEA_URL="${GITEA_URL:?ERROR: GITEA_URL is required (e.g. https://git.student-domain)}"
GITEA_URL="${GITEA_URL%/}"
GITEA_ADMIN_USER="${GITEA_ADMIN_USER:-droot}"
GITEA_ADMIN_TOKEN="${GITEA_ADMIN_TOKEN:?ERROR: GITEA_ADMIN_TOKEN is required}"
GITEA_ORG="${GITEA_ORG:-cloudops-labs}"
EXERCISES_DIR="${EXERCISES_DIR:-/home/droot/exercises}"
STUDENT_USER="${STUDENT_USER:-droot}"

GIT_AUTH_URL="${GITEA_URL/https:\/\//https:\/\/${GITEA_ADMIN_USER}:${GITEA_ADMIN_TOKEN}@}"
GIT_AUTH_URL="${GIT_AUTH_URL/http:\/\//http:\/\/${GITEA_ADMIN_USER}:${GITEA_ADMIN_TOKEN}@}"

for cmd in git curl jq; do
  if ! command -v "${cmd}" >/dev/null 2>&1; then
    echo "ERROR: Missing required CLI tool: ${cmd}" >&2
    exit 1
  fi
done

api_call() {
  local method="$1"
  local path="$2"
  local data="${3:-}"
  if [[ -n "${data}" ]]; then
    curl -sS -o /dev/null -w "%{http_code}" -X "${method}" \
      -H "Authorization: token ${GITEA_ADMIN_TOKEN}" \
      -H "Content-Type: application/json" \
      -d "${data}" \
      "${GITEA_URL}/api/v1${path}"
  else
    curl -sS -o /dev/null -w "%{http_code}" -X "${method}" \
      -H "Authorization: token ${GITEA_ADMIN_TOKEN}" \
      "${GITEA_URL}/api/v1${path}"
  fi
}

ensure_org() {
  local status
  status=$(api_call GET "/orgs/${GITEA_ORG}")
  if [[ "${status}" == "200" ]]; then
    echo "[OK] Gitea org '${GITEA_ORG}' already exists."
    return
  fi
  status=$(api_call POST "/orgs" "{\"username\":\"${GITEA_ORG}\",\"visibility\":\"public\"}")
  if [[ "${status}" != "201" && "${status}" != "422" ]]; then
    echo "ERROR: Failed to create org '${GITEA_ORG}' (HTTP ${status})" >&2
    exit 1
  fi
  echo "[CREATED] Gitea org '${GITEA_ORG}'."
}

ensure_repo() {
  local repo="$1"
  local is_private="$2"
  local status
  status=$(api_call GET "/repos/${GITEA_ORG}/${repo}")
  if [[ "${status}" == "200" ]]; then
    echo "[OK] Repo '${GITEA_ORG}/${repo}' exists."
    return
  fi
  status=$(api_call POST "/orgs/${GITEA_ORG}/repos" \
    "{\"name\":\"${repo}\",\"private\":${is_private},\"default_branch\":\"main\",\"auto_init\":false}")
  if [[ "${status}" != "201" && "${status}" != "409" ]]; then
    echo "ERROR: Failed to create repo '${GITEA_ORG}/${repo}' (HTTP ${status})" >&2
    exit 1
  fi
  echo "[CREATED] Repo '${GITEA_ORG}/${repo}' (private=${is_private})."
}

seed_repo() {
  local repo="$1"
  local is_private="$2"
  local src_dir="${SCRIPT_DIR}/${repo}"
  local work_tmp
  work_tmp="$(mktemp -d)"

  ensure_repo "${repo}" "${is_private}"

  mkdir -p "${work_tmp}/repo"
  cp -a "${src_dir}/." "${work_tmp}/repo/"
  rm -rf "${work_tmp}/repo/overlays"

  pushd "${work_tmp}/repo" >/dev/null
  git init -b main -q
  git config user.name "CloudOps Lab Bot"
  git config user.email "lab-bot@cloudops.local"
  if [[ "${repo}" == "unit-converter" && -f "${work_tmp}/repo/requirements.txt" ]]; then
    git add README.md requirements.txt
    git commit -q -m "chore: initialize unit-converter skeleton"
  fi
  git add -A
  git commit -q -m "feat: initial starter state for ${repo}"
  git remote add origin "${GIT_AUTH_URL}/${GITEA_ORG}/${repo}.git"
  git push -u -f origin main -q

  # Apply optional branch overlays (e.g. quality-smells, lab3-starter on campus-ops-java)
  if [[ -d "${src_dir}/overlays" ]]; then
    for overlay_dir in "${src_dir}/overlays/"*; do
      [[ -d "${overlay_dir}" ]] || continue
      local branch_name
      branch_name="$(basename "${overlay_dir}")"
      git checkout -q -b "${branch_name}" main
      cp -a "${overlay_dir}/." "${work_tmp}/repo/"
      git add -A
      if ! git diff --cached --quiet; then
        git commit -q -m "chore: seed ${branch_name} exercise branch"
      fi
      git push -u -f origin "${branch_name}" -q
      git checkout -q main
    done
  fi
  popd >/dev/null
  rm -rf "${work_tmp}"

  # Prepare student workspace clone in EXERCISES_DIR
  local target_dir="${EXERCISES_DIR}/${repo}"
  if [[ ! -d "${target_dir}/.git" ]]; then
    rm -rf "${target_dir}"
    if [[ "${is_private}" == "true" ]]; then
      echo "[SKIP_PUBLIC_CLONE] Private repo '${repo}' staged in Gitea only (students configure credentials in Ex 7.1)."
    else
      git clone -q "${GITEA_URL}/${GITEA_ORG}/${repo}.git" "${target_dir}"
      echo "[CLONED] ${target_dir}"
    fi
  fi
}

mkdir -p "${EXERCISES_DIR}"
ensure_org

seed_repo "unit-converter" "false"
seed_repo "campus-ops-java" "false"
seed_repo "live-scoreboard" "false"
seed_repo "smart-facility-cd" "false"
seed_repo "payment-ledger-secops" "true"

if id "${STUDENT_USER}" >/dev/null 2>&1 && [[ "$(id -u)" -eq 0 ]]; then
  chown -R "${STUDENT_USER}:${STUDENT_USER}" "${EXERCISES_DIR}"
fi

echo "COMPLETE: Seeded 5 CND-400 repositories in ${GITEA_URL}/${GITEA_ORG}."
