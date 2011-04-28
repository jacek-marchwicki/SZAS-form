package com.szas.sync;

public interface DAOObserver {
	public void onChange(boolean whileSync);
}
