package com.nebula.tinyasm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Type;

import com.nebula.tinyasm.api.ClassThisInstance;
import com.nebula.tinyasm.api.ClassUseCaller;
import com.nebula.tinyasm.api.InstanceMethodCode;
import com.nebula.tinyasm.api.MethodCode;
import com.nebula.tinyasm.api.MethodHeader;
import com.nebula.tinyasm.api.MethodUseCaller;
import com.nebula.tinyasm.api.Types;

public abstract class AbstractInstanceMethodVisitor<H, M, C extends MethodCode<M, C>> extends AbstractMethodVistor<H, M, C>
        implements InstanceMethodCode<M, C>, MethodHeader<C>, Types {

	public AbstractInstanceMethodVisitor(ClassVisitor cv, Type thisType, int access, Type returnType, String methodName, Class<?>... exceptionClasses) {
		super(cv, thisType, access, returnType, methodName, exceptionClasses);
	}


	@Override
	protected C doMethodBegin() {
		currentInstance = new MyInstance(mv);
		mv.visitCode();

		labelCurrent = labelWithoutLineNumber();
		// TODO add class sign
		variablesStack.push(new Variable(THIS_NAME, thisObjectType, null, labelCurrent));
		for (ClassField field : thisMethodParams) {
			variablesStack.push(new Variable(field, labelCurrent));
		}
		recomputerLocals();

		return code();
	}

	@Override
	public Type thisType() {
		return thisObjectType;
	}

}
