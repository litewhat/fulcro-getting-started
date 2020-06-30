(ns app.config.cli
  (:require [clojure.tools.cli :as c]
            [clojure.core.match :refer [match]]
            [taoensso.timbre :as log]
            [app.config :as config]))

(def options
  [["-o" "--output-file FILE" "Output file path."
    :id :output-file
    :default-fn (fn [opts] ".env")]
   ["-t" "--target TARGET_NAME" "Target platform name, that the file should be generated for."
    :id :target-platform]
   ["-h" "--help"]])

(defmulti execute (fn [args]
                    (match [args]
                      [{:arguments (["generate"] :seq) :options {:target-platform "docker"}}]
                      ::generate-docker-env-file)))

(defmethod execute ::generate-docker-env-file
  [args]
  (log/debug "Generating .env file for docker...")
  (let [{{:keys [output-file]} :options} args]
    (config/generate-docker-env-file! config/app-config output-file))
  (log/info "Generated .env file for docker successfully."))

(defn -main [& args]
  (let [args (c/parse-opts args options)]
    (execute args)))