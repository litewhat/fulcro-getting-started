(ns app.ui-test-runner
  (:require [clojure.test :as t]
            [app.client :as a]
            [app.example-test]))

(defn run []
  (t/run-tests 'app.example-test))

(defn ^:export init []
  (js/console.log "Initializing app...")
  (a/init)
  (js/console.log "App initialized successfully!")
  (js/console.log "Running tests...")
  (run))