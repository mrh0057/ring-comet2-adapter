(defproject net.matthoyt/ring-cometd2-adapter "0.1.0-SNAPSHOT"
  :description "Ring Cometd 2 adapter."
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [ring/ring-servlet "1.1.8"]
                 [org.cometd.java/cometd-java-server "2.5.1"]
                 [org.eclipse.jetty/jetty-servlet "7.6.8.v20121106"]
                 ]
  :dev-dependencies [[clj-http "0.1.3"]
                     [lein-clojars "0.7.0"]
                     [lein-marginalia "0.6.0"]])
