package com.nebula.dropwizard.core;

public class Todo2 {
	boolean completed;
	String title;
	int age;

	public int getint() {
		return age;
	}

	public void setint(int age) {
		this.age = age;
	}

	public Todo2(boolean completed, String title, int age) {
		super();
		this.completed = completed;
		this.title = title;
		this.age = age;
	}

	public void changeCompleted(boolean completed) {
		this.setCompleted(completed);
		this.setint(this.getint()+ 1 );
	}

	public void changeString(String title) {
		this.setString(title);
	}

	public void delete() {

	}

	public boolean getCompleted() {
		return completed;
	}

	public String getString() {
		return title;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public void setString(String title) {
		this.title = title;
	}
}
