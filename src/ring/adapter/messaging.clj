(ns ring.adapter.messaging
  (:use ring.adapter.internal.bayeux)
  (:require [ring.adapter.internal.channel :as channel]))

(defn publish
  "Publishes a message on the channel.

*channel-id*
  The id of the channel to publish the message on. <br />
*data*
  The string of JSON to send to the client.<br />
*from*
  Who the message is from. Can be nil <br />
*id*
  The message id.  Can be nil"
  ([channel-id data]
     (channel/publish channel-id nil data))
  ([channel-id data from]
     (channel/publish channel-id from data)))
