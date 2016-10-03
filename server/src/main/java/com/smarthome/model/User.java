package com.smarthome.model;

import java.util.ArrayList;
import java.util.List;

public class User {
	private String id;
	private String name;
	private String username;
	private String password;
	private String email;
	private boolean isAdmin;
	private Room house;
	private List<Room> rooms;
	
	public User () {
		id = null;
		name = null;
		username = null;
		password = null;
		email = null;
		isAdmin = false;
		house = null;
		rooms = new ArrayList<Room>();
	}
	
	public User ( String id, String name, String username, String password, String email, boolean isAdmin, Room house, List<Room> rooms) {
		this.id = id;
		this.name = name;
		this.username = username;
		this.password = password;
		this.email = email;
		this.isAdmin = isAdmin;
		this.house = house;
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

	public Room getHouse() {
		return house;
	}

	public void setHouse(Room house) {
		this.house = house;
	}

	public List<Room> getRooms() {
		return rooms;
	}

	public void setRooms(List<Room> rooms) {
		this.rooms = rooms;
	}
	
}
