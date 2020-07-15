(ns app.db
  (:require [hugsql.core :as hc]
            [hugsql.adapter.clojure-java-jdbc :as had]
            [taoensso.timbre :as log]
            [app.config :as cfg]
            [app.person.db.queries :as person-queries]
            [app.user.db.queries :as user-queries]))

(def conn-spec
  {:dbtype   "postgres"
   :host     (get-in cfg/app-config [:database :hostname])
   :port     (get-in cfg/app-config [:database :port])
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
  (log/debugf "Created %s table" "person_list_person")

  (user-queries/create-user-table db-spec)
  (log/debugf "Created %s table" "app_user")
  )

(defn tear-down-tables!
  [db-spec]
  (user-queries/drop-user-table db-spec)
  (log/debugf "Dropped %s table" "app_user")

  (person-queries/drop-person-list-people-table db-spec)
  (log/debugf "Dropped %s table" "person_list_person")

  (person-queries/drop-person-list-table db-spec)
  (log/debugf "Dropped %s table" "person_list")

  (person-queries/drop-person-table db-spec)
  (log/debugf "Dropped %s table" "person"))
