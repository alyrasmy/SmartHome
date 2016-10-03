package com.smarthome.model;

public class Room {
	private String id;
	private String RoomName;
	
	public Room () {
		id = null;
		RoomName = null;
	}
	
	public Room ( String id, String RoomName) {
		this.id = id;
		this.RoomName = RoomName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRoomName() {
		return RoomName;
	}

	public void setRoomName(String roomName) {
		RoomName = roomName;
	}
	
}
