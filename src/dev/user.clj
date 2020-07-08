(ns user
  (:require [clojure.tools.namespace.repl :as tools-ns]
            [clojure.java.shell :as shell]
            [db.seed :as dbs]
            [app.db :as db]
            [app.config :as cfg]
            [app.person.db.queries :as person-queries]
            [app.server :as server]))

;; https://github.com/clojure/tools.namespace

(tools-ns/set-refresh-dirs "src/dev" "src/main")

(defn start []
  (server/start)
  ;; db start
  (db/init!)
  (db/set-up-tables! db/conn-spec)
  )

(defn stop []
  (server/stop)
  ;; db-stop
  (db/tear-down-tables! db/conn-spec)
  )

(defn restart []
  (stop)
  (tools-ns/refresh :after `user/start))

(comment
  (start)
  (dbs/seed! db/conn-spec)
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
  (db/set-up-tables! db/conn-spec)
  ;; you can play with database now
  ;; see app.person.db.queries namespace

  ;; tear down tables when you're done
  (db/tear-down-tables! db/conn-spec)
  )

(comment
  (person-queries/insert-person db/conn-spec {:name "Paweł" :age 28})
  (person-queries/insert-person db/conn-spec {:name "Anthony" :age 50})

  (count (person-queries/get-all-people db/conn-spec))

  (person-queries/get-all-people db/conn-spec)
  (person-queries/get-person-by-id db/conn-spec {:id 6})

  (person-queries/insert-person-list db/conn-spec {:id (str :friends)})
  (person-queries/insert-person-list db/conn-spec {:id (str :enemies)})

  (let [res (person-queries/get-all-person-lists db/conn-spec)]
    (map #(update % :id read-string) res))

  (person-queries/get-person-list-by-id db/conn-spec {:id (str :friends)})
  (person-queries/get-person-list-by-id db/conn-spec {:id (str :enemies)})

  (person-queries/add-person-to-list db/conn-spec {:list_id   (str :friends)
                                                   :person_id 2})
  (person-queries/add-person-to-list db/conn-spec {:list_id   (str :friends)
                                                   :person_id 3})
  (person-queries/add-person-to-list db/conn-spec {:list_id   (str :friends)
                                                   :person_id 5})

  (clojure.pprint/pprint
    (let [res (person-queries/get-people-by-list-id db/conn-spec {:list_id (str :friends)})]
      (map #(update % :list_id read-string) res)))

  (let [list-id    (str :enemies)
        people-ids [2 4 6]
        res        (person-queries/add-people-to-list db/conn-spec {:people (mapv (partial vector list-id) people-ids)})]
    res)

  (person-queries/remove-person-from-list
    db/conn-spec
    {:list_id (str :friends)
     :person_id 1})
  )

(comment
  (shell/sh "pwd")
  (shell/sh "docker" "container" "ls")
  (shell/sh "docker" "image" "ls"))

(comment
  (def db-proc
    (future
      (shell/sh "./scripts/run-dev-db.sh"))))