(ns #^{:doc "These function are internal and are subject to change.
The interface are kept internal to allow them to be change.  The functionality
provided by the protocols are going to be wrap inside of function."}
  ring.adapter.internal.channel
  (:use ring.adapter.internal.bayeux)
  (:import [org.cometd.bayeux.server BayeuxServer ServerChannel ConfigurableServerChannel]
           [org.cometd.bayeux Channel]))

(defn get-channel
  "Used to get a channel by its id.  If the channel doesn't exists
it creates the channel.  The reason the bayeux-server is referenced global is because there
can only be one.

*id*
  The id of the channel to get. <br />
*return* The channel with the specified id."
  [^String id]
  (.createIfAbsent *bayeux-server* id (into-array org.cometd.bayeux.server.ConfigurableServerChannel$Initializer []))
  (.getChannel *bayeux-server* id))

(defprotocol ServerChannelProtocol
  (remove-channel [this]
    "Removes a channel and all of its children")
  (publish [this from data] [this from data id]
    "Publishes a message on the channel.

from
  The session the message is from.  This may be nil <br />
data
  The data for the message <br />
id
  The id of the message.")
  (subscribe [this session]
    "Subscribes to a session.

session
  The session to subscribe to the channel")
  (unsubscribe [this session]
    "Unsubscribes the session from the channel

session
  The session to unscribe to the channel."))

(extend-type ServerChannel
  ServerChannelProtocol
  (remove-channel [this]
    (.removeChannel this))
  (publish
    ([this from data]
       (publish this from data nil))
    ([this from data id]
       (.publish this from data id)))
  (subscribe [this session]
    (.subscribe this session))
  (unsubscribe [this session]
    (.unsubscribe this session)))

(defprotocol ConfigurableServerChannelProtocol
  (add-listener [this listener]
    "Adds a listener to a channel

listener
  The implementation of the listener for the channel.")
  (lazy? [this]
    "Is the channel lazy")
  (persistent? [this]
    "Is the channel persistent")
  (remove-listener [this listener]
    "Removes a listener from a channel.

listener
  The listener to remove.")
  (persistent [this val]
    "Sets a channel as persistent
val
  true for the channel to be persistent")
  (lazy [this val]
    "Sets the channel as lazy.

val
  true for the channel to be lazy"))

(extend-type ConfigurableServerChannel
  ConfigurableServerChannelProtocol
  (add-listener [this listener]
    (.addListener this listener))
  (lazy? [this]
    (.isLazy this))
  (persistent? [this]
    (.isPersistent this))
  (remove-listener [this listener]
    (.removeListener this listener))
  (persistent [this val]
    (.setPersistent this val))
  (lazy [this val]
    (.setLazy this val)))

(defprotocol ChannelProtocol
  (attribute [this name]
    "Gets an attribute

name
  The name of the attribute")
  (broadcast? [this]
    "true if the channel is a broadcast channel")
  (deep-wild? [this]
    "true if the channel contains a deep wild character ** ex. foo/**")
  (meta? [this]
    "true if a meta channel")
  (service? [this]
    "true if a service channel")
  (wild? [this]
    "true if wild")
  (id [this]
    "Gets the id of the channel")
  (remove-attribute [this name]
    "Removes an attribute from a channel

name 
  The name of the attribute")
  (set-attribute [this name value]
    "Sets the attribute of the channel.

name
  The name of the attribute
value
  The vale for the attribute"))

(extend-type Channel
  ChannelProtocol
  (attribute [this name]
    (.getAttribute this name))
  (id [this]
    (.getId this))
  (broadcast? [this]
    (.isBroadcast this))
  (deep-wild? [this]
    (.isDeepWild this))
  (meta? [this]
    (.isMeta this))
  (wild? [this]
    (.isWild this))
  (remove-attribute [this name]
    (.removeAttribute this name))
  (set-attribute [this name value]
    (.setAttribute this name value)))

(extend-type String
  ServerChannelProtocol
  (remove-channel [this]
    (remove-channel (get-channel this)))
  (publish
    ([this from data]
       (publish this from data nil))
    ([this from data id]
       (publish (get-channel this)
                from data id)))
  (subscribe [this session]
    (subscribe (get-channel this)
               session))
  (unsubscribe [this session]
    (unsubscribe (get-channel this)
                 session))
  ConfigurableServerChannelProtocol
  (add-listener [this listener]
    (add-listener (get-channel this)
                  listener))
  (lazy? [this]
    (lazy? (get-channel this)))
  (persistent? [this]
    (persistent? (get-channel this)))
  (remove-listener [this listener]
    (remove-listener (get-channel this)
                     listener))
  (persistent [this val]
    (persistent (get-channel this)
                val))
  (lazy [this val]
    (lazy (get-channel this)
          val))
  ChannelProtocol
  (attribute [this name]
    (attribute (get-channel this)
               name))
  (id [this]
    this)
  (broadcast? [this]
    (broadcast? (get-channel this)))
  (deep-wild? [this]
    (deep-wild? (get-channel this)))
  (meta? [this]
    (meta? (get-channel this)))
  (wild? [this]
    (wild? (get-channel this)))
  (remove-attribute [this name]
    (remove-attribute (get-channel this)
                      name))
  (set-attribute [this name value]
    (set-attribute (get-channel this)
                   name
                   value)))
