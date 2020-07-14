(ns app.user-registration.model-test
  (:require [cljs.test :refer [deftest testing is]]
            [app.user-registration.model :as sut]
            [app.test.mock :as mock]))

(deftest list-errors-test
  (let [env    (mock/env)
        errors (sut/list-errors env #uuid"3f65cf59-4049-4ed4-be1e-f51cc93e331c")]
    (is (= 3 (count errors)))
    (is (= #{#uuid"2630743f-8d83-4e14-a577-3493dad4c802"
             #uuid"9b496605-9371-4c9e-9ab3-b7824e5af59f"
             #uuid"c5d4f45d-524c-4542-b702-4b1e02a62d69"}
           (set (map :error/id errors))))))
