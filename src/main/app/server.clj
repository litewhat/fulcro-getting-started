(ns app.server
  (:require [com.fulcrologic.fulcro.server.api-middleware :as api-middleware]
            [org.httpkit.server :as http]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [ring.middleware.resource :refer [wrap-resource]]
            [taoensso.timbre :as log]
            [app.parser :refer [api-parser]]))

(def ^:private not-found-handler
  (fn [req]
    {:status 404
     :headers {"Content-Type" "text/plain"}
     :body "Not Found"}))

(def app-handler
  (-> not-found-handler
      (api-middleware/wrap-api {:uri "/api"
                                :parser api-parser})
      (api-middleware/wrap-transit-params)
      (api-middleware/wrap-transit-response)
      (wrap-resource "public")
      (wrap-content-type)))

(defonce server (atom nil))

(defn start []
  (log/debug "Starting application http server")
  (reset! server (http/run-server app-handler {:port 3000})))

(defn stop []
  (log/debug "Stopping application http server")
  (when @server
    (@server)
    (reset! server nil)))