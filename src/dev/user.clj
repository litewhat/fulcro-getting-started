(ns user
  (:require [clojure.tools.namespace.repl :as tools-ns]
            [clojure.tools.trace :as trace]
            [clojure.java.shell :as shell]
            [db.seed :as dbs]
            [app.config :as cfg]
            [app.db :as db]
            [app.db.queries :as db-queries]
            [app.person.db.queries :as person-queries]
            [app.user.db.queries :as user-queries]
            [app.server :as server]
            ))

;; https://github.com/clojure/tools.namespace

(tools-ns/set-refresh-dirs "src/dev" "src/main")

(defn start []
  (server/start))

(defn stop []
  (server/stop))

(defn restart []
  (stop)
  (tools-ns/refresh :after `user/start))

(defn setup-db []
  (db/init!)
  (db/set-up-tables! db/conn-spec)
  (dbs/seed! db/conn-spec))

(defn teardown-db []
  (db/tear-down-tables! db/conn-spec))

(defn reset-db []
  (teardown-db)
  (tools-ns/refresh :after `user/setup-db))

(comment
  (start)
  (stop)
  (restart)

  (setup-db)
  (teardown-db)
  (reset-db)

  ;; If there are compiler errors
  (tools-ns/refresh)
  (start)
  )

(comment
  (trace/trace-vars #'com.fulcrologic.fulcro.server.api-middleware/handle-api-request)
  (trace/untrace-vars #'com.fulcrologic.fulcro.server.api-middleware/handle-api-request))

(comment
  (shell/sh "pwd")
  (shell/sh "docker" "container" "ls")
  (shell/sh "docker" "image" "ls"))

(comment
  (def db-proc
    (future
      (shell/sh "./scripts/run-dev-db.sh"))))
