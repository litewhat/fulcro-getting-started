#!/bin/bash

export DATABASE_HOSTNAME=localhost
export DATABASE_PORT=15432
export DATABASE_NAME=test_db_name
export DATABASE_USER=test_db_user
export DATABASE_PASSWORD=test_db_password

# Runs all clojure tests inside docker container
clj -m app.config.cli generate --target docker --output-file .env
docker-compose build
docker-compose run --rm app sleep 10 && clojure -A:dev:test:runner -i :integration
docker-compose down --rmi local --remove-orphans --volumes
docker container prune --force
docker image prune --force
rm .env
