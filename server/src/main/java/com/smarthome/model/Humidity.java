package com.smarthome.model;

public class Humidity implements Comparable<Humidity> {
	private String time;
	private String value;
	
	public Humidity () {
		time = null;
		value = null;
	}
	
	public Humidity ( String time, String value) {
		this.time = time;
		this.value = value;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public int compareTo(Humidity o) {
		return getTime().compareTo(o.getTime());
	}
}