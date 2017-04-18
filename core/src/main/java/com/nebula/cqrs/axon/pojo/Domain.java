package com.nebula.cqrs.axon.pojo;

import java.util.ArrayList;
import java.util.List;

public class Domain {
	public String name;

	public Domain(String name) {
		super();
		this.name = name;
	}

	public List<Field> fields = new ArrayList<>();
}