(ns ring.adapter.cometd
  (:import [org.cometd.server CometdServlet AbstractService]
           [org.cometd.bayeux.server BayeuxServer]
           [javax.servlet GenericServlet ServletException])
  (:use [ring.adapter.internal.bayeux]))

(defn create-cometd-servlet
  ([] (create-cometd-servlet "/cometd/*"))
  ([url-pattern]
     {:servlet (CometdServlet.)
      :url-pattern url-pattern
      :load-on-startup 1}))

(defn create-bayeux-servlet
  "Used to create the servlet for bayeux.

*services*
  The services for the bayeux server. <br />
*extensions*
  The extensions for the bayeux server.
  ### options

   `:acknowledge`
     Provides the reliable ordering messaging to the bayeux protocol and it also receives any unacknowledge messages. <br />
   `:timestamp`
     Adds a timestamp to each message object. <br />
   `:timesync`
     Provides the time offset between the client and the server.
      For more information: [docs](http://cometd.org/documentation/2.x/cometd-ext/timesync) <br />
*returns*
  The servlet map for bayeux"
  [services & extensions]
  (create-bayeux-initializer services extensions))
