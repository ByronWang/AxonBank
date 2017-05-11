package com.nebula.tinyasm.ana;

import org.objectweb.asm.Label;

class Block {
	enum BlockType {
		ELSEBLOCK, IFBLOCK, METHODBLOCK, VITUALBLOCK
	}

	Block.BlockType blockType = BlockType.METHODBLOCK;

	Label elseLabel;

	Label label;

	String name;
	final int startStackIndex;

	public Block(String name, final int startStackIndex) {
		super();
		this.name = name;
		this.blockType = BlockType.METHODBLOCK;
		this.startStackIndex = startStackIndex;
	}

	public Block(String name, Label label, Block.BlockType blockType, final int startStackIndex) {
		super();
		this.name = name;
		this.label = label;
		this.blockType = blockType;
		this.startStackIndex = startStackIndex;
	}

	public Block(String name, Label label, final int startStackIndex) {
		super();
		this.name = name;
		this.label = label;
		this.blockType = BlockType.IFBLOCK;
		this.startStackIndex = startStackIndex;
	}
}