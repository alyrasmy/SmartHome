#!/usr/bin/env python
import requests;
import datetime;
import time;

HOST = "http://localhost";
PORT = "8080";

def get_time():
    return datetime.date.fromtimestamp(time.time()).isoformat() + '(' + datetime.datetime.now().time().isoformat().split('.')[0] + ')';

def temperature_job():
    print "Start Temperature Job";
    requests.post(HOST + ":" + PORT + "/smarthome/api/temperature/job/start/" + get_time(), {});
    print "Finish Temperature Job";

def humidity_job():
    print "Start Humidity Job";
    requests.post(HOST + ":" + PORT + "/smarthome/api/humidity/job/start/"+ get_time(), {});
    print "Finished Humidity Job";

def led_job():
    print "Start LED Job";
    requests.post(HOST + ":" + PORT + "/smarthome/api/led/job/start/" + get_time(), {});
    print "Finished LED Job";

if __name__ == "__main__":
    temperature_job();
    humidity_job();
    led_job();

