(ns ring.adapter.jetty7
  "Adapter for the Jetty 7 webserver."
  (:import (org.eclipse.jetty.server.handler AbstractHandler)
           (org.eclipse.jetty.server Server Request Response)
           (org.eclipse.jetty.server.nio SelectChannelConnector)
           (org.eclipse.jetty.server.ssl SslSelectChannelConnector)
           (org.eclipse.jetty.servlet ServletContextHandler ServletHolder)
           (javax.servlet.http HttpServletRequest HttpServletResponse)
           (javax.servlet GenericServlet ServletException))
  (:require [ring.util.servlet :as servlet]))

;; Set the jetty server globally so it can be shutdown.
;; ===================================================
;;
;; The server needs to be started in another thread.

(def jetty-server)

(defn- proxy-handler
  "Returns an Jetty Handler implementation for the given Ring handler."
  [handler]
  (proxy [AbstractHandler] []
    (handle [target ^Request request http-request http-response]
      (let [request-map  (servlet/build-request-map request)
            response-map (handler request-map)]
        (when response-map
          (servlet/update-servlet-response http-response response-map)
          (.setHandled request true))))))

(defn- add-ssl-connector!
  "Add an SslSocketConnector to a Jetty Server instance."
  [^Server server options]
  (let [ssl-connector (SslSelectChannelConnector.)]
    (doto ssl-connector
      (.setPort        (options :ssl-port 8443))
      (.setHost        (options :host))
      (.setKeystore    (options :keystore))
      (.setKeyPassword (options :key-password)))
    (when (options :truststore)
      (.setTruststore ssl-connector (options :truststore)))
    (when (options :trust-password)
      (.setTrustPassword ssl-connector (options :trust-password)))
    (.addConnector server ssl-connector)))

(defn- create-server
  "Construct a Jetty Server instance."
  [options]
  (let [connector (doto (SelectChannelConnector.)
                    (.setPort (options :port 8080))
                    (.setHost (options :host)))
        server    (doto (Server.)
                    (.addConnector connector)
                    (.setSendDateHeader true))]
    (when (or (options :ssl?) (options :ssl-port))
      (add-ssl-connector! server options))
    server))

(defn ^Server run-jetty
  "Serve the given handler according to the options.

Servlet code taken from Maximilian Weber. 

###  Options

 `:configurator`   - A function called with the Server instance. <br />
 `:port` <br />
 `:host`
 `:join?`          - Block the caller: defaults to true. <br />
 `:ssl?`           - Use SSL. <br />
 `:ssl-port`       - SSL port: defaults to 443, implies :ssl? <br />
 `:keystore` <br />
 `:key-password` <br />
 `:truststore` <br />
 `:trust-password` <br />
 `:servlets`       - Additional servlets to register in the form
 `[{:url-pattern \"/orders/*\" :servlet orders-servlet :load-on-startup 1}]`"
  [handler options]
  (let [^Server s (create-server (dissoc options :configurator))]
    (def jetty-server s)
    (when-let [configurator (:configurator options)]
      (configurator s))
    (let [context (ServletContextHandler. ServletContextHandler/SESSIONS)
          handler-servlet (servlet/servlet handler)]
      (do
        (.setContextPath context "/")
        (.addServlet context (ServletHolder. handler-servlet) "/*"))
      (dorun
       (map #(let [{:keys [url-pattern servlet load-on-startup]} %1
                   holder (ServletHolder. servlet)]
               (.setInitOrder holder load-on-startup)
               (.addServlet context holder url-pattern))
            (:servlets options)))
      (doto s
        (.setHandler context)
        (.start)))
    (when (:join? options true)
      (.join s))
    s))
