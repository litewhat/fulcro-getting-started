#!/bin/bash

# Runs all clojure tests inside docker container
clj -m app.config.cli generate --target docker --output-file .env
docker-compose build
docker-compose run --rm app clojure -A:dev:test:runner
docker-compose down --rmi local --remove-orphans --volumes
