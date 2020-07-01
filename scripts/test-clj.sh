#!/bin/bash

export DATABASE_HOSTNAME=localhost
export DATABASE_PORT=15432
export DATABASE_NAME=test_db_name
export DATABASE_USER=test_db_user
export DATABASE_PASSWORD=test_db_password

# Generate `.env` file used by docker-compose
clj -m app.config.cli generate --target docker --output-file .env

# Ensure required images are built:
docker-compose build

# Run integration tests
docker-compose run --rm app sleep 10 && clojure -A:dev:test:runner -i :integration

# Stop containers and remove containers, networks, volumes, and images created by `docker-compose up` command.
# Details: https://docs.docker.com/compose/reference/down/
docker-compose down --rmi local --remove-orphans

# Remove all stopped containers
docker container prune --force

# Remove unused images
docker image prune --force

# Remove generated `.env` file
rm .env
