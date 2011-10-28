(ns ring.adapter.service
  (:require [ring.adapter.internal.services :as services]))

(defn- emit-services [body]
  (map (fn [exp]
         (if (symbol? exp)
           `(~exp)
           `(ring.adapter.internal.services/create-service
             ~(if (string? (first exp))
                (first exp)
                (throw (Exception. "Expecting a string value")))
             ~(if (string? (second exp))
                (second exp)
                (throw (Exception. "Expecting a string value")))
             ~(nth exp 2)))) body))

(defmacro defservices
  "Used to define services for cometd.

name
  The name of the services to create
body
  defines the path and handler for the services.
  format:
    symbol is consider to be other services
    (name channel-id my-handler)
      name
        The name of the service.
      channel-id 
        is the id of the channel.
        The channel supports wild cards.  To match one level use * to match anything beneath use **
        channel-ids are in the form \"/my/channel\".
      my-handler
        is a function that takes one argument which is map containing the following keys
          :from
            Who is ending the message.
          :channel-id
            The id of the channel sending the message.
          :data
            A hashmap containing the data.  Currently doesn't convert the keys of the map to keywords and
            is the same map returned by cometd. The map is readonly.
          :id
            The id of the message.
          :message
            The message obj for cometd.  The map contains data from the extension enable on the bayeux server."
  [name & body]
  `(defn ~name []
     ~@(emit-services body)))
