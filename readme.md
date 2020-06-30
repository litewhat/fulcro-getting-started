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
