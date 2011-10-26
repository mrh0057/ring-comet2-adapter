(ns ring.adapter.internal.services
  (:use ring.adapter.comted))

(defrecord ServiceRequest [server-session
                           from
                           channel-id
                           data])

(defn make-service-request [server-session
                            from
                            channel-id
                            data])

(defn create-service [chanel-id message-handler]
  )
