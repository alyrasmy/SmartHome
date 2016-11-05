#!/usr/bin/env python
# Author: Aly Rasmy
# Quick python script to execute scheduled service

#    POST    /rest/api/smarthome/job/{sensor}/{action} (com.ciena.tools.asgard.service.SmartHomeJobRESTService)

import schedule
import jobs
import time

if __name__ == "__main__":
    schedule.every().day.at("23:58").do(jobs.led_job);
    schedule.every().day.at("23:59").do(jobs.resetLedTimer);
    schedule.every(10).minutes.do(jobs.temperature_job);
    schedule.every(10).minutes.do(jobs.humidity_job);
    while True:
    	schedule.run_pending();
 	time.sleep(1);
