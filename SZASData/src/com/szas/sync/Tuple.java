package com.szas.sync;

import java.io.Serializable;
import java.util.Random;

/**
 * class inherited from Tuple can be stored in DAO's
 * @author Jacek Marchwicki
 *
 */
public abstract class Tuple implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Random random = new Random();
	
	protected long id;
	
	public Tuple() {
		id = random.nextLong();
	}

	/**
	 * Return random generated id
	 * @return random generated id of Tuple
	 */
	public long getId() {
		return id;
	}
}
