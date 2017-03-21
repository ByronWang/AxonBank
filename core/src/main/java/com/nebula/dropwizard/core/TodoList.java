package com.nebula.dropwizard.core;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TodoList {
	List<Todo> todos;

	public TodoList() {
		todos = new ArrayList<>();
	}

	public List<Todo> getAll() {
		return todos;
	}

	public List<Todo> getCompleted(boolean completed) {
		return todos.stream().filter(todo -> todo.completed == completed).collect(Collectors.toList());
	}

	public void newTodo(String title) {
		Todo todo = new Todo(true, title, 0);
		todos.add(todo);
	}

	public void changeTodoString(int index, String title) {
		todos.get(index).changeTitle(title);
	}

	public Todo getTodo(int index) {
		return todos.get(index);
	}

	public void deleteTodo(int index) {
		todos.get(index).delete();
	}

	public void changeStatus(int index, boolean completed) {
		todos.get(index).changeCompleted(completed);
	}
}
