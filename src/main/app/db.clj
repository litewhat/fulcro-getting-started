(ns app.db
  (:require [hugsql.core :as hc]
            [hugsql.adapter.clojure-java-jdbc :as had]
            [taoensso.timbre :as log]))

(def db-spec
  {:dbtype   "postgres"
   :host     "localhost"
   :port     15432
   :dbname   "fulcro_getting_started"
   :user     "fulcro_getting_started"
   :password "pass123"})

(defn init! []
  (hc/set-adapter! (had/hugsql-adapter-clojure-java-jdbc))
  (hc/def-db-fns "app/person/db/queries.sql"))

(defn set-up-tables!
  [db-spec]
  (create-person-table db-spec)
  (log/debugf "Created %s table" "person")

  (create-person-list-table db-spec)
  (log/debugf "Created %s table" "person_list")

  (create-person-list-person-table db-spec)
  (log/debugf "Created %s table" "person_list_person"))

(defn tear-down-tables!
  [db-spec]
  (drop-person-list-person-table db-spec)
  (log/debugf "Dropped %s table" "person_list_person")

  (drop-person-list-table db-spec)
  (log/debugf "Dropped %s table" "person_list")

  (drop-person-table db-spec)
  (log/debugf "Dropepd %s table" "person"))

(comment
  (init!)

  (set-up-tables! db-spec)
  (tear-down-tables! db-spec)

  )