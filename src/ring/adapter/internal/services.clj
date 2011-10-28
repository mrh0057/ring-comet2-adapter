(ns ring.adapter.internal.services
  (:use ring.adapter.internal.thread-pool
        ring.adapter.internal.channel
        ring.adapter.internal.bayeux
        ring.adapter.internal.session)
  (:import [org.cometd.bayeux.server ServerChannel
            ServerSession
            ServerMessage]))

(defrecord ServiceRequest [from
                           channel-id
                           data
                           id
                           message])

(defn make-service-request [from
                            channel-id
                            data
                            id
                            message]
  (ServiceRequest. from channel-id data id message))

(defn- same-session? [service from]
  (= (server-session (:session service)) from))

(defn- create-message-listener [message-handler service]
  (proxy [org.cometd.bayeux.server.ServerChannel$MessageListener] []
    (onMessage [^ServerSession from ^ServerChannel channel ^ServerMessage  message]
      (if (not (same-session? service from))
        (execute (fn []
                   (message-handler (make-service-request from
                                                          (id channel)
                                                          (.getDataAsMap message)
                                                          (.getId message)
                                                          message)))))
      true)))

(defrecord Service [session])

(defn make-service [session]
  (Service. session))

(defn create-service
  "Used to create a service on a channel.  Makes sure that
messages sent from the channel are not also received by the channel.

Adds an message listener to bayeux server.

name
  The name of the service
channel-id
  The id of the channel to bind the message handler to
message-handler
  The message handler."
  [name channel-id message-handler]
  (add-listener channel-id (create-message-listener message-handler
                                                    (make-service (new-local-session-bayeux name)))))
