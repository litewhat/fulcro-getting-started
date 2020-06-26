(ns app.config
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [aero.core :as a]))

(def app-config (a/read-config (io/resource "config.edn")))


(defn- docker-env-file-content
  "Helper function for generating .env file used in docker-compose."
  [config envs-mapping]
  (->> envs-mapping
       (map (fn [[var-name val-path]] (vector var-name (get-in config val-path))))
       (map (partial string/join "="))
       (string/join "\n")))

(defn generate-docker-env-file!
  [config]
  (let [mapping {"DATABASE_NAME"     [:database :name]
                 "DATABASE_USER"     [:database :user]
                 "DATABASE_PASSWORD" [:database :password]}
        content (docker-env-file-content config mapping)]
    (spit ".env" content)))

(comment
  ;; TODO: Move to script
  (generate-docker-env-file! app-config))
