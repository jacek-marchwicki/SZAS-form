package com.szas.server.gwt.client;

import java.util.Random;

public class Tuple {
	private int id;

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
	
	public void setRandomId(Random random) {
		id = random.nextInt();
	}
}
