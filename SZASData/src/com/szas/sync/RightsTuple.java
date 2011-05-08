package com.szas.sync;

import java.util.ArrayList;

public abstract class RightsTuple extends Tuple {
	private static final long serialVersionUID = 1L;

	public static enum RightType {
		ADMIN,
		EDITOR,
		VIEVER
	}
	public static class Right implements Cloneable {
		public Right(String email, RightType rightType) {
			this.email = email;
			this.rightType = rightType;
		}
		String email;
		RightType rightType;
	}
	
	private ArrayList<Right> rights =
		new ArrayList<Right>();
	
	public void setDefaultRights(String email) {
		Right defaultRight = new Right(email, RightType.ADMIN);
		getRights().clear();
		getRights().add(defaultRight);
	}

	public void setRights(ArrayList<Right> rights) {
		this.rights = rights;
	}

	public ArrayList<Right> getRights() {
		return rights;
	}
}
