# ring-cometd2-adapter

An ring adpater that includes the comet2d servlet.

## Usage

Availabel on clojure jars. For use with lein.

     [net.matthoyt/ring-cometd2-adapter "0.1.0-SNAPSHOT"]


Publishing a message on a channel:

     (publish "channel/id" data)
     (publish "channel/id" data from-channel)

Defining services

     (defservices my-services
       ("service-name" "mychannel/*" handle))

Starting the server

     (run-jetty app
                {:port 8080 :servlets
                 [(create-cometd-servlet)
                  (create-bayeux-servlet my-services)]})

Adding a listener to see when a client session ends.

     (client-remove-listener client-session
                             (fn [session timeout]))
                          

## License

Jetty 7 apdater code: https://github.com/jalpedersen/ring-jetty7-adapter

Servlet Implementation: https://github.com/maxweber/hello-cometd

Distributed under the Eclipse Public License, the same as Clojure.
