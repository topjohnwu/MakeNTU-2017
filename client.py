import websocket
import time
import ASUS.GPIO as GPIO

GPIO.setmode(GPIO.BOARD)
GPIO.setup(11, GPIO.OUT)
GPIO.setup(10, GPIO.OUT)
GPIO.output(10, True)

def on_message(ws, message):
    if message == "on":
        GPIO.output(11, True)
    elif message == "off":
        GPIO.output(11, False)

def on_error(ws, error):
    print error

def on_close(ws):
    print "### Closed... Reconnect ###"

def new_socket():
    ws = websocket.WebSocketApp("ws://192.168.2.113:3001/",
                              subprotocols=["echo-protocol"],
                              on_message = on_message,
                              on_error = on_error,
                              on_close = on_close)
    return ws

websocket.enableTrace(True)

while True:
    socket = new_socket()
    socket.run_forever()
    time.sleep(5)
