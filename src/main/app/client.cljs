(ns app.client
  (:require [com.fulcrologic.fulcro.application :as app]
            [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [com.fulcrologic.fulcro.dom :as dom]))

(defonce app (app/fulcro-app))

(defsc Root [this props]
  (dom/div "I am the Root component!"))

(defn ^:export init []
  (app/mount! app Root "app")
  (js/console.log "Loaded"))

(defn ^:export refresh []
  (app/mount! app Root "app")
  (js/console.log "Hot reload"))


(comment
  "Playing and testing"
  (js/console.log "Hello!")

  (let [agree? (js/confirm "agree?")]
    (case agree?
      true (js/alert "Agreed! :)")
      false (js/alert "Disagreed! :("))))