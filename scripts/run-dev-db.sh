#!/bin/bash

# Generate `.env` file used by docker-compose
clj -m app.config.cli generate --target docker --output-file .env

# Ensure required images are built:
docker-compose build database

# Run database
docker-compose up database

# Stop containers and remove containers, networks, volumes, and images created by `docker-compose up` command.
# Details: https://docs.docker.com/compose/reference/down/
docker-compose down --rmi local --remove-orphans

# Remove all stopped containers
docker container prune --force

# Remove unused images
docker image prune --force

# Remove generated `.env` file
rm .env
