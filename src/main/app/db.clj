(ns app.db
  (:require [hugsql.core :as hc]
            [hugsql.adapter.clojure-java-jdbc :as had]))

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

(comment
  (init!)
  (create-person-table db-spec)
  (drop-person-table db-spec))