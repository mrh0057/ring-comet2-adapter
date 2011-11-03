(ns ring.adapter.client
  (:use ring.adapter.internal.bayeux)
  (:require [ring.adapter.internal.session :as session]))

(defn client-session
  "Used to get the client session associated with the id.

client-id
  The id of the client session.

returns
  A ServerSession object"
  [client-id]
  (session *bayeux-server* client-id))

(defn client-removed-listener
  "Used to listen for the event that a client is closed.

server-session
  The server session for the client
listener
  The listener to call when the client is disconencted. The listener takes 3 arguments.
    session
      The ServerSession.
    timeout
      true if the session was ended due to a timeout."
  [server-session listener]
  (session/add-remove-session-listener server-session listener))
