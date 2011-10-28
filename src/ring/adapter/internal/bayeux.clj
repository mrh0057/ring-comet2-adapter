(ns ring.adapter.internal.bayeux
  (:import [org.cometd.bayeux.server BayeuxServer]
           [javax.servlet GenericServlet ServletException])
  (:require [ring.adapter.internal.thread-pool :as thread-pool]))

(def ^BayeuxServer *bayeux-server*)

(defn add-extensions [extensions]
  (if (contains? extensions :acknowledge)
    (.addExtension *bayeux-server* (new org.cometd.server.ext.TimestampExtension)))
  (if (contains? extensions :timestamp)
    (.addExtension *bayeux-server* (new org.cometd.server.ext.TimestampExtension)))
  (if (contains? extensions :timesync)
    (.addExtension *bayeux-server* (new org.cometd.server.ext.TimesyncExtension))))

(defn create-bayeux-initializer [services extensions]
  {:servlet (proxy [GenericServlet] []
      (init [config]
        (proxy-super init config)
        (let [sc (proxy-super getServletContext)]
          (def *bayeux-server* (.getAttribute sc BayeuxServer/ATTRIBUTE))
          (if (nil? *bayeux-server*)
            (throw (Exception. "No BayeuxServer available. Servlet start order correct?"))
            (do
              (thread-pool/init 100)
              (add-extensions (apply hash-set extensions))
              (services)))))
      (service [request response]
        (throw (ServletException.))))
   :load-on-startup 2})

(defprotocol BayeuxServerProtocol
  (session [this client-id]
    "Gets the session for the specified client id

client-id
  The id of the client to get the session for.")
  (new-local-session [this id-hint]
    "Used to get a new local session and performs the handshake for that connection.

id-hint 
  The id hint for the session."))

(extend-type BayeuxServer
  BayeuxServerProtocol
  (session [this client-id]
    (.getSession this client-id))
  (new-local-session [this id-hint]
    (doto (.newLocalSession this id-hint)
      (.handshake))))

(defn new-local-session-bayeux [name]
  (new-local-session *bayeux-server* name))
