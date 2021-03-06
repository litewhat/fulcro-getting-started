
### Build

Build docker image for application:
```shell script
$ docker build --tag=fulcro_getting_started:alpha .
```

### Test

#### Integration

Execute following script to run integration tests via docker-compose.
```shell script
$ ./scripts/test-clj.sh
```

### Run

#### Locally

Run database for `dev` environment:
```shell script
$ ./scripts/run-dev-db.sh
```

You can check database connection via psql:
```shell script
$ psql -h localhost -p 15432 -U <username_from_config>
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

Open browser and navigate to `localhost:3000/index.html`.

#### Docker

```shell script
$ docker-compose run --rm app bash
```