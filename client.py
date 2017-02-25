import websocket
import time

def on_message(ws, message):
    print message

def on_error(ws, error):
    print error

def on_close(ws):
    print "### Closed... Reconnect ###"

def new_socket():
    ws = websocket.WebSocketApp("ws://localhost:3001/",
                              subprotocols=["echo-protocol"],
                              on_message = on_message,
                              on_error = on_error,
                              on_close = on_close)
    return ws

websocket.enableTrace(True)
socket = websocket.WebSocketApp("ws://localhost:3001/",
                          subprotocols=["echo-protocol"],
                          on_message = on_message,
                          on_error = on_error,
                          on_close = on_close)
while True:
    socket = new_socket()
    socket.run_forever()
    time.sleep(5)
