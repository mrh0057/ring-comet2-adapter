(ns ring.adapter.CometService
  (:import [org.cometd.bayeux.server BayeuxServer])
  (:use ring.adapter.messaging)
  (:gen-class
   :extends org.cometd.server.AbstractService
   :name ring.adapter.CometService
   :constructors {[org.cometd.bayeux.server.BayeuxServer String]
                  [org.cometd.bayeux.server.BayeuxServer String]}
   :methods [[processMessage [org.cometd.bayeux.server.ServerSession org.cometd.bayeux.Message] void]
             [initServices [String] void]]))

(defn -initService [this channel-id]
  (.addService this channel-id))

(defn -processMessage [this remote channel-id message message-id]
  (process-message this remote channel-id message message-id))
