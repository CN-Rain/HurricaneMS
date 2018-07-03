/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * This file is part of the "Renoria" Game.
 * Copyright (C) 2008
 * IDGames.
 */

package net.sf.odinms.server;

/**
 *
 * @author David
 */
public class RandomEvent {
	private RandomEventType type;
	private int id;
	private String message;
	
	public RandomEvent(RandomEventType type, int id, String message) {
		this.type = type;
		this.id = id;
		this.message = message;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public RandomEventType getType() {
		return type;
	}

	public void setType(RandomEventType type) {
		this.type = type;
	}
}
