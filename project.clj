(defproject net.matthoyt/ring-cometd2-adapter "0.1.0-SNAPSHOT"
  :description "Ring Cometd 2 adapter."
  :dependencies [[org.clojure/clojure "1.2.1"]
                 [ring/ring-servlet "0.3.11"]
                 [org.cometd.java/cometd-java-server "2.3.1"]
                 [org.eclipse.jetty/jetty-servlet "7.4.4.v20110707"]]
  :dev-dependencies [[clj-http "0.1.3"]
                     [swank-clojure "1.4.0-SNAPSHOT"]
                     [lein-clojars "0.7.0"]])
