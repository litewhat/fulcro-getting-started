(ns app.user-registration.model.validation.spec
  (:require #?(:clj [clojure.spec.alpha :as s]
               :cljs [cljs.spec.alpha :as s])
            [app.common.utils :as u]))

(s/def ::password (s/and not-empty
                         (u/min-length? 8)
                         u/has-lowercase-letter?
                         u/has-uppercase-letter?
                         u/has-number?))

(defn string-equals? [v]
  (s/and string? #(= v %)))