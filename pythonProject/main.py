import random
import time
import  sys
from  Adafruit_IO import  MQTTClient
import serial.tools.list_ports


AIO_FEED_ID = "LED"

AIO_USERNAME = "Bach14637"
AIO_KEY = "aio_YRxZ92529UzdODuJzTKXcelNe6In"

def  connected(client):
    print("Connected successfully!!!")
    client.subscribe(AIO_FEED_ID)
    # client.subscribe(AIO_FEED_ID_AIR_TEMP)

def  subscribe(client , userdata , mid , granted_qos):
    print("Subscribe successfully!!!")

def  disconnected(client):
    print("Disconnected")
    sys.exit (1)

def  message(client , feed_id , payload):
    print("Received data: " + payload)
    print("Received feed_id: " + feed_id)
    ser.write((str(payload) + "#").encode())
    # if(feed_id == 'vgu-led'):
    #     ser.write((str(payload) + "#").encode())

client = MQTTClient(AIO_USERNAME , AIO_KEY)
client.on_connect = connected
client.on_disconnect = disconnected
client.on_message = message
client.on_subscribe = subscribe
client.connect()
client.loop_background()

def getPort():
    ports = serial.tools.list_ports.comports()
    N = len(ports)
    commPort = "None"
    for i in range(0, N):
        port = ports[i]
        strPort = str(port)
        if "USB Serial Device" in strPort:
            splitPort = strPort.split(" ")
            commPort = (splitPort[0])
    return commPort

print("Testing port:", getPort())

if getPort() != "None":
    ser = serial.Serial(port=getPort(), baudrate=115200)
    print("MCU is connected!")

mess = ""
def processData(data):
    data = data.replace("!", "")
    data = data.replace("#", "")
    splitData = data.split(":")
    print(splitData)
    if splitData[1] == "TEMP":
        client.publish("Temperature", splitData[2])
    if splitData[1] == "LIGHT":
        client.publish("Light", splitData[2])


mess = ""
def readSerial():
    bytesToRead = ser.inWaiting()
    if (bytesToRead > 0):
        global mess
        mess = mess + ser.read(bytesToRead).decode("UTF-8")
        while ("#" in mess) and ("!" in mess):
            start = mess.find("!")
            end = mess.find("#")
            processData(mess[start:end + 1])
            if (end == len(mess)):
                mess = ""
            else:
                mess = mess[end+1:]

while True:
    readSerial()
    time.sleep(1)




