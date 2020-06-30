
### Build

Build docker image for application:
```shell script
$ docker build --tag=fulcro_getting_started:alpha .
```

### Test
Ensure whether required images are built:
```shell script
$ docker-compose build
```

Run tests in docker container:
```shell script
$ docker-compose run --rm app clojure -A:dev:test:runner
```

You can also run tests in one command:
```shell script
$ ./scripts/test-clj.sh
```

### Run

#### Locally

Generate `.env` file for `docker-compose` on the basis of `resources/config.edn`:
```shell script
$ clj -m app.config.cli generate --target docker --output-file .env
```

Run services:
```shell script
$ docker-compose up
```

Check database connection via psql:
```shell script
$ psql -h localhost -p 15432 -U fulcro_getting_started
```

Run build watcher for frontend app:
```shell script
$ shadow-cljs watch main
```

Start Clojure REPL:
```shell script
$ clj -A:dev
```

Start application within `user` namespace:
```clojure
user=> (start)
```

Open browser and navigate to `localhost:3000`.

#### Docker

```shell script
$ docker-compose run --rm app bash
```