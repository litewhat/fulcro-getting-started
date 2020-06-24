(ns app.db
  (:require [hugsql.core :as hc]
            [hugsql.adapter.clojure-java-jdbc :as had]
            [app.person.db.queries :as person-queries]
            [taoensso.timbre :as log]))

(def db-spec
  {:dbtype   "postgres"
   :host     "localhost"
   :port     15432
   :dbname   "fulcro_getting_started"
   :user     "fulcro_getting_started"
   :password "pass123"})

(defn init! []
  (log/debug "Initalizing hugsql adapter")
  (hc/set-adapter! (had/hugsql-adapter-clojure-java-jdbc)))

;; migrations

(defn set-up-tables!
  [db-spec]
  (person-queries/create-person-table db-spec)
  (log/debugf "Created %s table" "person")

  (person-queries/create-person-list-table db-spec)
  (log/debugf "Created %s table" "person_list")

  (person-queries/create-person-list-people-table db-spec)
  (log/debugf "Created %s table" "person_list_person"))

(defn tear-down-tables!
  [db-spec]
  (person-queries/drop-person-list-people-table db-spec)
  (log/debugf "Dropped %s table" "person_list_person")

  (person-queries/drop-person-list-table db-spec)
  (log/debugf "Dropped %s table" "person_list")

  (person-queries/drop-person-table db-spec)
  (log/debugf "Dropepd %s table" "person"))

(comment
  (init!)
  (set-up-tables! db-spec)
  (tear-down-tables! db-spec))

(comment
  (person-queries/insert-person db-spec {:name "Paweł" :age 28})
  (count (person-queries/get-all-people db-spec))
  (person-queries/get-all-people db-spec)
  (person-queries/get-person-by-id db-spec {:id 3})

  (person-queries/insert-person-list db-spec {:id (str :friends)})
  (person-queries/insert-person-list db-spec {:id (str :enemies)})
  (let [res (person-queries/get-all-person-lists db-spec)]
   (map #(update % :id read-string) res))
  (person-queries/get-person-list-by-id db-spec {:id (str :friends)})
  (person-queries/get-person-list-by-id db-spec {:id (str :friends)}))