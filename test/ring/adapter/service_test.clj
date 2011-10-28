(ns ring.adapter.service-test
  (:use clojure.test
        ring.adapter.service))

(defn- handler-func [val])

(deftest defservices-test
  (println
   (macroexpand-1
    '(defservices my-services
       handler-func
       ("name" "/channel/id" handler-func)
       ("second" "/channel2" handler-func)))))
