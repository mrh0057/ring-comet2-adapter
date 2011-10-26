(ns ring.adapter.cometd
  (:import [org.cometd.server CometdServlet AbstractService]
           [org.cometd.bayeux.server BayeuxServer]
           [javax.servlet GenericServlet ServletException]))

(def ^BayeuxServer *bayeux-server*)

(defn create-cometd-servlet
  ([] (create-cometd-servlet "/cometd/*"))
  ([url-pattern]
     {:servlet (CometdServlet.)
      :url-pattern url-pattern
      :load-on-startup 1}))

(defn create-bayeux-initializer []
  {:servlet (proxy [GenericServlet] []
      (init [config]
        (proxy-super init config)
        (let [sc (proxy-super getServletContext)]
          (def *bayeux-server* (.getAttribute sc BayeuxServer/ATTRIBUTE))
          (if (nil? *bayeux-server*)
            (throw (Exception. "No BayeuxServer available. Servlet start order correct?")))))
      (service [request response]
        (throw (ServletException.))))
   :load-on-startup 2})

(defn create-service
  "Used to create a service with the specified routing key.

channel-id
  The channel-id for the service.
  syntax: my/channel/*"
  [channel-id]
)

(defn send-message
  "Publishes a message to a channel.

channel-id 
  The id of the channel to publish to.
data
  The data to publish on the channel. Make sure you serialize the data to json before sending the message.
id
  The id of the message."
  [channel-id data id]
  (.getChannel *bayeux-server* channel-id))
