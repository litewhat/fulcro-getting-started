(ns app.client
  (:require [com.fulcrologic.fulcro.application :as app]
            [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [com.fulcrologic.fulcro.dom :as dom]))

(defonce app (app/fulcro-app))

(defsc ClickCounter
  [this {:keys [:counter/clicks :counter/name]}]
  {}
  (js/console.log this)
  (dom/div {:id (str "ClickCounter-" name)}
    (dom/h1 "Counter")
    (dom/p "My name is " name)
    (dom/p (str "Clicked: " clicks " times"))))

(defsc Person
  [this {:keys [:person/name :person/age]}]
  {}
  (dom/div
    (dom/h3 "Person")
    (dom/p
      (dom/span (str "Name: " name))
      (dom/br)
      (dom/span (str "Age: " age)))))

(def ui-person (comp/factory Person))

(defsc Root
  [this props]
  {}
  (js/console.log "Props of Root:" (comp/props this))
  (dom/div :.container
    (dom/h1 "Root component")
    (dom/p "I am the Root component!")
    (dom/div
      (ui-person {:person/name "Paweł" :person/age 28}))))

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