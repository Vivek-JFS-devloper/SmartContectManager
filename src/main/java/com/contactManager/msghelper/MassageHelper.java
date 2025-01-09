package com.contactManager.msghelper;

public class MassageHelper {
	public String content;
	public String type;
	
	
	
	public MassageHelper(String content, String type) {
		this.content = content;
		this.type = type;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
}
