package com.nebula.dropwizard.core;

public class Todo {
	boolean completed;
	String title;
	int age;

	public Todo() {
		super();
	}

	public Todo(boolean completed, String title, int age) {
		super();
		this.completed = completed;
		this.title = title;
		this.age = age;
	}
	
	public void changeTitle(String title){
		this.title = title;
	}

	public void changeCompleted(boolean completed) {
		this.completed = completed;
		this.age = 1;
	}
	
	public void delete(){
		
	}
}
