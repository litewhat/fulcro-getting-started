(ns user
  (:require [clojure.tools.namespace.repl :as tools-ns]
            [app.server :as server]))

;; https://github.com/clojure/tools.namespace

(tools-ns/set-refresh-dirs "src/dev" "src/main")

(defn start []
  (server/start))

(defn restart []
  (server/stop)
  (tools-ns/refresh :after `user/start))

(comment
  (start)
  (restart)
  (server/stop)

  ;; If there are compiler errors
  (tools-ns/refresh)
  (start)
  )