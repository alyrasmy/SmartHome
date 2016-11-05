package com.smarthome.model;

public class Temperature implements Comparable<Temperature> {
	private String time;
	private String value;
	
	public Temperature () {
		time = null;
		value = null;
	}
	
	public Temperature ( String time, String value) {
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
	public int compareTo(Temperature o) {
		return getTime().compareTo(o.getTime());
	}
}
