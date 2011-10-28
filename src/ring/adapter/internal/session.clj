(ns ring.adapter.internal.session
  (:import [org.cometd.bayeux.server LocalSession]))

(defprotocol LocalSessionProtocol
  (server-session [this]
    "Used to get the server sesssion."))

(extend-type LocalSession
  LocalSessionProtocol
  (server-session [this]
    (.getServerSession this)))

(defprotocol ServerSessionProtocol
  (add-remove-session-listener [this listener]
    "Listener is called with the session is removed.

listener
  The listener to call when the session is removed.
    Takes 2 arguments.
    session
      The server session the that has been removed.
    timeout
      The timout of the session.")
  (add-dequeue-listener [this listener]
    "Listener is called when a messages are dequed from the server.  Can be used
to removed duplicate messages.
  listener The function to call.  
    Takes 1 argument
      session
        The server session.")
  (add-max-queue-listener [this listener]
    "Called when the maximium number of message is reached for a queue.
listener
  A function that takes 3 arguments.
   session
     The ServerSession the messages are being delivered to.
   from
     The person who is sending the messages.
   message
     The message that is being sent.
  
  The listener is expected to return a true/false value.
   true 
    The message should be queue for the client.
   false
    The message should not be queued.")
  (add-message-listener [this listener]
    "Called before a message is queue.

listener
  Takes 3 arguments.
    to
      The ServerSession the message is being sent to.
    from
      The ServerSession that is sending the message.  This may be nil
    message
      The server message.

  The listener can return false for the message not to be sent or true for the message to be sent."))


