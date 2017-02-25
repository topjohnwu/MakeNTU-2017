import websocket
import time
import uuid
import ASUS.GPIO as GPIO

GPIO.setmode(GPIO.BOARD)
GPIO.setup(11, GPIO.OUT)
GPIO.setup(10, GPIO.OUT)
GPIO.output(10, True)

def get_mac():
    mac_num = hex(uuid.getnode()).replace('0x', '').upper()
    mac = '-'.join(mac_num[i : i + 2] for i in range(0, 11, 2))
    return mac

def on_message(ws, message):
    if message == "on":
        GPIO.output(11, True)
    elif message == "off":
        GPIO.output(11, False)

def on_error(ws, error):
    print error

def on_close(ws):
    print "### Closed... Reconnect ###"

def on_open(ws):
    ws.send(get_mac())

def new_socket():
    ws = websocket.WebSocketApp("ws://52.175.20.174:3001/",
                              subprotocols=["echo-protocol"],
                              on_message = on_message,
                              on_open = on_open,
                              on_error = on_error,
                              on_close = on_close)
    return ws

websocket.enableTrace(True)

while True:
    socket = new_socket()
    socket.run_forever()
    time.sleep(5)
