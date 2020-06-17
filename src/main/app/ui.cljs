(ns app.ui
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [com.fulcrologic.fulcro.dom :as dom]))

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