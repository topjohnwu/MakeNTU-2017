import websocket
import time
import uuid
import ASUS.GPIO as GPIO

GPIO.setmode(GPIO.BOARD)
GPIO.setup(11, GPIO.OUT)
GPIO.setup(10, GPIO.OUT)
GPIO.output(10, True)
GPIO.output(11, False)

status = "off"

def get_mac():
    mac_num = hex(uuid.getnode()).replace('0x', '').upper()
    mac = '-'.join(mac_num[i : i + 2] for i in range(0, 11, 2))
    return mac

def get_temp():
    f = open('/sys/class/thermal/thermal_zone1/temp')
    temp = int(f.read())
    return str((float(temp) / 1000))

def toggle(stat):
    status = stat
    if status == "on":
        GPIO.output(11, True)
    elif status == "off":
        GPIO.output(11, False)

def on_message(ws, message):
    req = message.split(':')
    print req
    if req[0] == 'set':
        toggle(req[1])
    elif req[0] == 'detail':
        res = "{ \"mode\": \"detail\", \"temp\": \"" + get_temp() + "\", \"status\": \"" + status + "\"} "
        ws.send(res); 

def on_error(ws, error):
    print error

def on_close(ws):
    print "### Closed... Reconnect ###"

def on_open(ws):
    res = "{ \"mode\": \"init\", \"mac\": \"" + get_mac() + "\" }"
    ws.send(res)

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
