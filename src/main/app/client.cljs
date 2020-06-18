(ns app.client
  (:require [com.fulcrologic.fulcro.application :as app]
            [app.ui :as ui]
            [app.application :refer [app]]))

(defn ^:export init []
  (app/mount! app ui/Root "app")
  (js/console.log "Loaded"))

(defn ^:export refresh []
  (app/mount! app ui/Root "app")
  (js/console.log "Hot reload"))

(comment
  ;; require
  ;; [com.fulcrologic.fulcro.components :as comp]
  ;; [com.fulcrologic.fulcro.algorithms.denormalize :as fdn]
  (def test-state (app/current-state app))
  (def test-query (comp/get-query ui/Root))
  (cljs.pprint/pprint
   (fdn/db->tree test-query test-state test-state)))

(comment
  "Playing and testing"
  (js/console.log "Hello!")

  (let [agree? (js/confirm "agree?")]
    (case agree?
      true (js/alert "Agreed! :)")
      false (js/alert "Disagreed! :("))))