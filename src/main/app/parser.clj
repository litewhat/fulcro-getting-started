(ns app.parser
  (:require [com.wsscode.pathom.core :as p]
            [com.wsscode.pathom.connect :as pc]
            [taoensso.timbre :as log]
            [app.person.resolvers :as person]))

(def resolvers [person/resolvers])

(def pathom-parser
  (p/parser {::p/env {::p/reader [p/map-reader
                                  pc/reader2
                                  pc/ident-reader
                                  pc/index-reader]
                      ::pc/mutation-join-globals [:tempids]}
             ::p/mutate pc/mutate
             ::p/plugins [(pc/connect-plugin {::pc/register resolvers})]}))

(defn api-parser [query]
  (log/info "Process" query)
  (pathom-parser {} query))

(comment
  (api-parser [{[:person/id 99] [:person/name]}])
  (api-parser [{[:person/id 99] [:person/name :person/age]}])
  (api-parser [{[:list/id :friends] [:list/label {:list/people [:person/id]}]}])
  (api-parser [{[:list/id :friends] [:list/label {:list/people [:person/id :person/name :person/age]}]}])
  (api-parser [{[:list/id :friends] [:list/label {:list/people [:person/id :person/name :person/age]}]}
               {[:list/id :enemies] [:list/label {:list/people [:person/id :person/name :person/age]}]}]))
