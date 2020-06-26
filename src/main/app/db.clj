(ns app.db
  (:require [hugsql.core :as hc]
            [hugsql.adapter.clojure-java-jdbc :as had]
            [taoensso.timbre :as log]
            [app.config :as cfg]
            [app.person.db.queries :as person-queries]))

(def db-spec
  {:dbtype   "postgres"
   :host     "localhost"
   :port     15432
   :dbname   (get-in cfg/app-config [:database :name])
   :user     (get-in cfg/app-config [:database :user])
   :password (get-in cfg/app-config [:database :password])})

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
  (person-queries/insert-person db-spec {:name "Paweł" :age 28})
  (count (person-queries/get-all-people db-spec))
  (person-queries/get-all-people db-spec)
  (person-queries/get-person-by-id db-spec {:id 6})

  (person-queries/insert-person-list db-spec {:id (str :friends)})
  (person-queries/insert-person-list db-spec {:id (str :enemies)})

  (let [res (person-queries/get-all-person-lists db-spec)]
   (map #(update % :id read-string) res))

  (person-queries/get-person-list-by-id db-spec {:id (str :friends)})
  (person-queries/get-person-list-by-id db-spec {:id (str :enemies)})

  (person-queries/add-person-to-list db-spec {:list_id (str :friends)
                                              :person_id 1})
  (person-queries/add-person-to-list db-spec {:list_id (str :friends)
                                              :person_id 3})
  (person-queries/add-person-to-list db-spec {:list_id (str :friends)
                                              :person_id 5})

  (clojure.pprint/pprint
   (let [res (person-queries/get-people-by-list-id db-spec {:list_id (str :enemies)})]
     (map #(update % :list_id read-string) res)))

  (let [list-id (str :enemies)
        people-ids [2 4 6]
        res (person-queries/add-people-to-list db-spec {:people (mapv (partial vector list-id) people-ids)})]
    res)
  )