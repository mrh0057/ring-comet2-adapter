(ns ring.adapter.messaging)

(defprotocol MessageProcessor
  (process-message [this remote routing-key message message-id]
    "Used to process an incoming message

remote
  The remote connection
channel-id
  The channel id
message
  The message from the channel
message-id
  The id of the message.")
  (see-own-publishes? [this])
  (server-session
    "Used to get the server session associated with the message processor."
    [this]))

(defprotocol Channel
  (subscribe [this channel-id]
    "Used to subscribe a user to the connection.

channel-id
  The id of the channel to subscribe the session to.")
  (unsubscribe [this channel-id]
    "Used to unsubscribe to the channel.

channel-id
  The id of the channel to unsubscribe from.")
  (publish [this data id] [this session data id]))
