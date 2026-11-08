#!/bin/bash
set -euxo pipefail
export DEBIAN_FRONTEND=noninteractive

apt-get update
apt-get install -y docker.io docker-compose-v2 git
systemctl enable --now docker

git clone ${repo_url} /opt/polyparrot
cd /opt/polyparrot
docker compose up -d
