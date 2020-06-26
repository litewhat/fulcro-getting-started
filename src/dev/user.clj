(ns user
  (:require [clojure.tools.namespace.repl :as tools-ns]
            [app.db :as db]
            [app.config :as cfg]
            [app.server :as server]))

;; https://github.com/clojure/tools.namespace

(tools-ns/set-refresh-dirs "src/dev" "src/main")

(defn start []
  (server/start))

(defn stop []
  (server/stop))

(defn restart []
  (server/stop)
  (tools-ns/refresh :after `user/start))

(comment
  (start)
  (stop)
  (restart)

  ;; If there are compiler errors
  (tools-ns/refresh)
  (start)
  )

(comment
  ;; before running SQL queries one have to generate .env file for docker-compose
  (cfg/generate-docker-env-file! cfg/app-config)
  ;; run docker-compose up in terminal
  ;; initialize hugsql adapter
  (db/init!)
  ;; set-up tables
  (db/set-up-tables! db/db-spec)
  ;; you can play with database now
  ;; see app.person.db.queries namespace
  ;; tear down tables when you're done
  (db/tear-down-tables! db/db-spec)
  )