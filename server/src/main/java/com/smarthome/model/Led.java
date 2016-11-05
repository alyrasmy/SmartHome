package com.smarthome.model;

public class Led implements Comparable<Led> {
	private String time;
	private String value;
	
	public Led () {
		time = null;
		value = null;
	}
	
	public Led ( String time, String value) {
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
	public int compareTo(Led o) {
		return getTime().compareTo(o.getTime());
	}
}
