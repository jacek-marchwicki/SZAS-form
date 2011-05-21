package com.szas.data;

public interface FieldDataTuple {
	public long getId();
	public String getName();
	public void setName(String name);
	public FieldTuple getTuple();
	public void setOnList(boolean onList);
	public boolean isOnList();
}
