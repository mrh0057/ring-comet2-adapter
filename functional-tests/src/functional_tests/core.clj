(ns functional-tests.core
  (:use compojure.core
        compojure.route
        compojure.handler
        ring.adapter.messaging
        ring.middleware.params
        ring.adapter.cometd
        ring.adapter.service
        ring.adapter.jetty7
        ring.adapter.client))

(defroutes all-routes
  (files "/" {:root "public"}))

(def app (-> all-routes
             wrap-params))

(defn test-service [val]
  (println val)
  (publish "/publish/test" {:hello "world"})
  (client-removed-listener (:from val)
                           (fn [session timeout]
                             (println session)))
  (println "returning"))

(defservices all-services
  ("hello" "/my/channel" test-service))

(defn run-server []
  (future (do
            (run-jetty (var app)
                       {:port 9095 :servlets
                        [(create-cometd-servlet)
                         (create-bayeux-servlet all-services :timestamp
                                                :acknowledge
                                                :timesync)]}))))
