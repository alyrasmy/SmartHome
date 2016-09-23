#!/usr/bin/env python
# Author: Aly Rasmy
# Quick python script to execute scheduled service

#    POST    /api/smarthome/job/{action}/{snapshot} (com.ciena.tools.asgard.service.SmartHomeJobRESTService)

import schedule
import jobs
import time

if __name__ == "__main__":
    # schedule.every().day.at("23:59").do(jobs.led_job); 
    schedule.every(1).seconds.do(jobs.temperature_job);
    schedule.every(1).seconds.do(jobs.humidity_job);
    while True:
    	schedule.run_pending();
 	time.sleep(1);
