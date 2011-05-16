/**
 * 
 */
package com.szas.android.SZASApplication;

/**
 * @author pszafer
 * Class for CustomArrayAdapter
 *
 */
public class QuestionnaireTypeRow {
	private String name;
	private long id;
	/**
	 * Type:
	 * 0 - background black, not filled
	 * 1 - background green, filled
	 */
	private int type;
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}
	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}
	
	/**
	 * 
	 */
	public QuestionnaireTypeRow(String name, int type, long id) {
		this.name = name;
		this.type = type;
		this.id = id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}
	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}
}
