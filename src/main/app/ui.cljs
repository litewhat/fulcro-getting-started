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
  (dom/li
    (dom/h5 (str name " " "(age: " age ")"))))

(def ui-person (comp/factory Person {:keyfn :person/name}))

(defsc PersonList
  [this {:keys [:list/label :list/people]}]
  {}
  (dom/div
    (dom/h4 label)
    (dom/ul
      (map ui-person people))))

(def ui-person-list (comp/factory PersonList))

(defsc Root
  [this {:keys [ui/react-key]}]
  {}
  (let [ui-data {:friends {:list/label  "Friends"
                           :list/people [{:person/name "Andrew"
                                          :person/age  26}
                                         {:person/name "Diana"
                                          :person/age  18}]}
                 :enemies {:list/label  "Enemies"
                           :list/people [{:person/name "Jonathan"
                                          :person/age  45}
                                         {:person/name "Daren"
                                          :person/age  25}]}}]
    (js/console.log "Root" this)
    (js/console.log "Props of Root:" (comp/props this))
    (dom/div :.container
      (dom/h1 "Root component")
      (dom/div
        (ui-person-list (:friends ui-data))
        (ui-person-list (:enemies ui-data))))))