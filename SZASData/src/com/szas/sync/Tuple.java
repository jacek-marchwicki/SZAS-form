package com.szas.sync;

import java.io.Serializable;
import java.util.Random;

public abstract class Tuple implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Random random = new Random();
	protected int id;
	
	public Tuple() {
		id = random.nextInt();
	}

	public int getId() {
		return id;
	}
}
