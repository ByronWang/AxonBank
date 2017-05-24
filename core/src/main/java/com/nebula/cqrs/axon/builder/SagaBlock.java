package com.nebula.cqrs.axon.builder;

import java.util.List;

import org.objectweb.asm.Label;
import org.objectweb.asm.Type;

import com.nebula.tinyasm.api.ClassMethodCode;
import com.nebula.tinyasm.util.Field;

class SagaBlock {
	enum BlockType {
		ELSEBLOCK, IFBLOCK, METHODBLOCK, VITUALBLOCK
	}

	SagaBlock.BlockType blockType = BlockType.METHODBLOCK;

	Label elseLabel;

	Label labelClose;

	String name;
	final int startStackIndex;

	public SagaBlock(String name, final int startStackIndex) {
		super();
		this.name = name;
		this.blockType = BlockType.METHODBLOCK;
		this.startStackIndex = startStackIndex;
	}

	public SagaBlock(String name, Label label, SagaBlock.BlockType blockType, final int startStackIndex) {
		super();
		this.name = name;
		this.labelClose = label;
		this.blockType = blockType;
		this.startStackIndex = startStackIndex;
	}

	public SagaBlock(String name, Label labelClose, final int startStackIndex) {
		super();
		this.name = name;
		this.labelClose = labelClose;
		this.blockType = BlockType.IFBLOCK;
		this.startStackIndex = startStackIndex;
	}
	
	ClassMethodCode code;

	public String commandName;

	public Type eventType;

	public List<Field> eventFields;
}