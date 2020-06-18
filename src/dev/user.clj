(ns user
  (:require [clojure.tools.namespace.repl :as tools-ns :refer [set-refresh-dirs refresh]]
            [app.server :as server]))

;; https://github.com/clojure/tools.namespace

(set-refresh-dirs "src/dev" "src/main")

(defn start []
  (server/start))

(defn restart []
  (server/stop)
  (refresh :after `user/start))

(comment
  (start)
  (restart)
  (server/stop))