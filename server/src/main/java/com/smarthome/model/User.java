package com.smarthome.model;

import java.util.ArrayList;
import java.util.List;

public class User {
	private String id;
	private String name;
	private String username;
	private String password;
	private String email;
	private String temp_threshold;
	private String humid_threshold;
	private String led_threshold;
	private String city;
	private String address;
	private String camera;
	private boolean isAdmin;
	private Room mainRoom;
	private List<Room> rooms;
	
	public User () {
		id = null;
		name = null;
		username = null;
		password = null;
		email = null;
		isAdmin = false;
		mainRoom = null;
		temp_threshold = null;
		humid_threshold = null;
		led_threshold = null;
		city = null;
		address = null;
		camera = null;
		rooms = new ArrayList<Room>();
	}
	
	public User ( String id, String name, String username, String password, String email, boolean isAdmin, Room mainRoom, List<Room> rooms) {
		this.id = id;
		this.name = name;
		this.username = username;
		this.password = password;
		this.email = email;
		this.isAdmin = isAdmin;
		this.mainRoom = mainRoom;
		this.rooms = rooms;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	public Room getMainRoom() {
		return mainRoom;
	}

	public void setmainRoom(Room mainRoom) {
		this.mainRoom = mainRoom;
	}

	public List<Room> getRooms() {
		return rooms;
	}

	public void setRooms(List<Room> rooms) {
		this.rooms = rooms;
	}

	public String getTemp_threshold() {
		return temp_threshold;
	}

	public void setTemp_threshold(String temp_threshold) {
		this.temp_threshold = temp_threshold;
	}

	public String getHumid_threshold() {
		return humid_threshold;
	}

	public void setHumid_threshold(String humid_threshold) {
		this.humid_threshold = humid_threshold;
	}

	public String getLed_threshold() {
		return led_threshold;
	}

	public void setLed_threshold(String led_threshold) {
		this.led_threshold = led_threshold;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCamera() {
		return camera;
	}

	public void setCamera(String camera) {
		this.camera = camera;
	}
	
}
