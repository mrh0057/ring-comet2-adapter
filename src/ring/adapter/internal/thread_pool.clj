(ns ring.adapter.internal.thread-pool
  (:import [java.util.concurrent Executors ExecutorService]))

(def *thread-pool*)

(defn init [thread-pool-size]
  (def *thread-pool* (Executors/newFixedThreadPool thread-pool-size)))

(defn execute [func]
  (.submit ^ExecutorService *thread-pool* ^Runnable (cast Runnable  func)))
