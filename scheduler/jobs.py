#!/usr/bin/env python
import requests;
import datetime;
import time;

HOST = "http://localhost";
PORT = "8080";
WarFileName = "/release-0.0.1-SNAPSHOT";
PHOTON_CORE_ID = "53ff72066667574817532367";
ACCESSTOKEN = "04b90f278a1415636513f0f71fe9f89e92cdfcba";

def temperature_job():
    print "Start Temperature Job";
    requests.post(HOST + ":" + PORT + WarFileName + "/rest/api/smarthome/job/temperature/start/", {});
    print "Finish Temperature Job";

def humidity_job():
    print "Start Humidity Job";
    requests.post(HOST + ":" + PORT + WarFileName + "/rest/api/smarthome/job/humidity/start/", {});
    print "Finished Humidity Job";

def led_job():
    print "Start LED Job";
    requests.post(HOST + ":" + PORT + WarFileName + "/rest/api/smarthome/job/led/start/", {});
    print "Finished LED Job";
    
def resetLedTimer():
    print "Start request to resetLedTimer";
    requests.post("https://api.particle.io/v1/devices/" + PHOTON_CORE_ID + "/resetTime?access_token=" + ACCESSTOKEN, {});
    print "Finished request to resetLedTimer";

if __name__ == "__main__":
    temperature_job();
    humidity_job();
    led_job();

