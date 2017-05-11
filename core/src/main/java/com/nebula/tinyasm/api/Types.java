package com.nebula.tinyasm.api;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Type;

import com.nebula.tinyasm.Variable;
import com.nebula.tinyasm.util.Field;

public interface Types {
	default boolean is(int access, int modified) {
		return (access & modified) > 0;
	}

	default <T> T firstOf(T[] values) {
		return values[0];
	}

	public static int[] computerVariableLocals(List<Variable> fields) {
		int[] locals = new int[fields.size()];
		int cntLocal = 0;
		for (int i = 0; i < fields.size(); i++) {
			locals[i] = cntLocal;
			cntLocal += fields.get(i).type.getSize();
		}
		return locals;
	}

	public static int[] computerLocalsVariable(List<Variable> fields) {
		int max = 0;
		for (int i = 0; i < fields.size(); i++) {
			max += fields.get(i).type.getSize();
		}

		int[] localsVar = new int[max];
		int cntLocal = 0;
		for (int i = 0; i < fields.size(); i++) {
			localsVar[cntLocal] = i;
			cntLocal += fields.get(i).type.getSize();
		}
		return localsVar;
	}

	default <T> T firstOf(List<T> values) {
		return values.get(0);
	}

	default <T> List<T> restOf(T[] values) {
		List<T> newvalues = new ArrayList<>();
		for (int i = 1; i < values.length; i++) {
			newvalues.add(values[i]);
		}
		return newvalues;
	}

	default <T> List<T> restOf(List<T> values) {
		List<T> newvalues = new ArrayList<>();
		for (int i = 1; i < values.size(); i++) {
			newvalues.add(values.get(i));
		}
		return newvalues;
	}

	default String signatureOf(Type type, Class<?>... signatureClasses) {
		String signature = null;
		if (signatureClasses != null && signatureClasses.length > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append("L");
			sb.append(type.getInternalName());
			sb.append("<");
			for (Class<?> signatureClass : signatureClasses) {
				sb.append(Type.getDescriptor(signatureClass));
			}
			sb.append(">;");
			signature = sb.toString();
		}
		return signature;
	}

	default String signatureOf(Type type, Type... signatureTypes) {
		String signature = null;
		if (signatureTypes != null && signatureTypes.length > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append("L");
			sb.append(type.getInternalName());
			sb.append("<");
			for (Type signatureType : signatureTypes) {
				sb.append(signatureType.getDescriptor());
			}
			sb.append(">;");
			signature = sb.toString();
		}
		return signature;
	}

	default String toPropertyGetName(String fieldName, Type FieldType) {
		return "get" + toPropertyName(fieldName);
	}

	default String toPropertyName(String fieldName) {
		return Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
	}

	default String toPropertySetName(String fieldName, Type FieldType) {
		return "set" + toPropertyName(fieldName);
	}

	default String toSimpleName(String name) {
		int index = name.lastIndexOf('.');
		if (index < 0) index = name.lastIndexOf('/');

		return name.substring(index + 1);
	}

	default Type typeOf(Class<?> clz) {
		return Type.getType(clz);
	}

	default Type[] typesOf(Class<?>... classes) {
		Type[] types = new Type[classes.length];
		for (int i = 0; i < classes.length; i++) {
			types[i] = Type.getType(classes[i]);
		}
		return types;
	}

	default Type[] typesOf(Field... fields) {
		Type[] types = new Type[fields.length];
		for (int i = 0; i < fields.length; i++) {
			types[i] = fields[i].type;
		}
		return types;
	};

	default Type[] typesOf(List<Field> fields) {
		Type[] types = new Type[fields.size()];
		for (int i = 0; i < fields.size(); i++) {
			types[i] = fields.get(i).type;
		}
		return types;
	};

	/**
	 * The stack size variation corresponding to each JVM instruction. This
	 * stack variation is equal to the size of the values produced by an
	 * instruction, minus the size of the values consumed by this instruction.
	 */
	static final int[] SIZE = buildOpcodeSize();

	/**
	 * Computes the stack size variation corresponding to each JVM instruction.
	 */
	static int[] buildOpcodeSize() {
		int i;
		int[] b = new int[202];
		String s = "EFFFFFFFFGGFFFGGFFFEEFGFGFEEEEEEEEEEEEEEEEEEEEDEDEDDDDD" + "CDCDEEEEEEEEEEEEEEEEEEEEBABABBBBDCFFFGGGEDCDCDCDCDCDCDCDCD"
		        + "CDCEEEEDDDDDDDCDCDCEFEFDDEEFFDEDEEEBDDBBDDDDDDCCCCCCCCEFED" + "DDCDCDEEEEEEEEEEFEEEEEEDDEEDDEE";
		for (i = 0; i < b.length; ++i) {
			b[i] = s.charAt(i) - 'E';
		}
		return b;

		// code to generate the above string
		//
		// int NA = 0; // not applicable (unused opcode or variable size opcode)
		//
		// b = new int[] {
		// 0, //NOP, // visitInsn
		// 1, //ACONST_NULL, // -
		// 1, //ICONST_M1, // -
		// 1, //ICONST_0, // -
		// 1, //ICONST_1, // -
		// 1, //ICONST_2, // -
		// 1, //ICONST_3, // -
		// 1, //ICONST_4, // -
		// 1, //ICONST_5, // -
		// 2, //LCONST_0, // -
		// 2, //LCONST_1, // -
		// 1, //FCONST_0, // -
		// 1, //FCONST_1, // -
		// 1, //FCONST_2, // -
		// 2, //DCONST_0, // -
		// 2, //DCONST_1, // -
		// 1, //BIPUSH, // visitIntInsn
		// 1, //SIPUSH, // -
		// 1, //LDC, // visitLdcInsn
		// NA, //LDC_W, // -
		// NA, //LDC2_W, // -
		// 1, //ILOAD, // visitVarInsn
		// 2, //LLOAD, // -
		// 1, //FLOAD, // -
		// 2, //DLOAD, // -
		// 1, //ALOAD, // -
		// NA, //ILOAD_0, // -
		// NA, //ILOAD_1, // -
		// NA, //ILOAD_2, // -
		// NA, //ILOAD_3, // -
		// NA, //LLOAD_0, // -
		// NA, //LLOAD_1, // -
		// NA, //LLOAD_2, // -
		// NA, //LLOAD_3, // -
		// NA, //FLOAD_0, // -
		// NA, //FLOAD_1, // -
		// NA, //FLOAD_2, // -
		// NA, //FLOAD_3, // -
		// NA, //DLOAD_0, // -
		// NA, //DLOAD_1, // -
		// NA, //DLOAD_2, // -
		// NA, //DLOAD_3, // -
		// NA, //ALOAD_0, // -
		// NA, //ALOAD_1, // -
		// NA, //ALOAD_2, // -
		// NA, //ALOAD_3, // -
		// -1, //IALOAD, // visitInsn
		// 0, //LALOAD, // -
		// -1, //FALOAD, // -
		// 0, //DALOAD, // -
		// -1, //AALOAD, // -
		// -1, //BALOAD, // -
		// -1, //CALOAD, // -
		// -1, //SALOAD, // -
		// -1, //ISTORE, // visitVarInsn
		// -2, //LSTORE, // -
		// -1, //FSTORE, // -
		// -2, //DSTORE, // -
		// -1, //ASTORE, // -
		// NA, //ISTORE_0, // -
		// NA, //ISTORE_1, // -
		// NA, //ISTORE_2, // -
		// NA, //ISTORE_3, // -
		// NA, //LSTORE_0, // -
		// NA, //LSTORE_1, // -
		// NA, //LSTORE_2, // -
		// NA, //LSTORE_3, // -
		// NA, //FSTORE_0, // -
		// NA, //FSTORE_1, // -
		// NA, //FSTORE_2, // -
		// NA, //FSTORE_3, // -
		// NA, //DSTORE_0, // -
		// NA, //DSTORE_1, // -
		// NA, //DSTORE_2, // -
		// NA, //DSTORE_3, // -
		// NA, //ASTORE_0, // -
		// NA, //ASTORE_1, // -
		// NA, //ASTORE_2, // -
		// NA, //ASTORE_3, // -
		// -3, //IASTORE, // visitInsn
		// -4, //LASTORE, // -
		// -3, //FASTORE, // -
		// -4, //DASTORE, // -
		// -3, //AASTORE, // -
		// -3, //BASTORE, // -
		// -3, //CASTORE, // -
		// -3, //SASTORE, // -
		// -1, //POP, // -
		// -2, //POP2, // -
		// 1, //DUP, // -
		// 1, //DUP_X1, // -
		// 1, //DUP_X2, // -
		// 2, //DUP2, // -
		// 2, //DUP2_X1, // -
		// 2, //DUP2_X2, // -
		// 0, //SWAP, // -
		// -1, //IADD, // -
		// -2, //LADD, // -
		// -1, //FADD, // -
		// -2, //DADD, // -
		// -1, //ISUB, // -
		// -2, //LSUB, // -
		// -1, //FSUB, // -
		// -2, //DSUB, // -
		// -1, //IMUL, // -
		// -2, //LMUL, // -
		// -1, //FMUL, // -
		// -2, //DMUL, // -
		// -1, //IDIV, // -
		// -2, //LDIV, // -
		// -1, //FDIV, // -
		// -2, //DDIV, // -
		// -1, //IREM, // -
		// -2, //LREM, // -
		// -1, //FREM, // -
		// -2, //DREM, // -
		// 0, //INEG, // -
		// 0, //LNEG, // -
		// 0, //FNEG, // -
		// 0, //DNEG, // -
		// -1, //ISHL, // -
		// -1, //LSHL, // -
		// -1, //ISHR, // -
		// -1, //LSHR, // -
		// -1, //IUSHR, // -
		// -1, //LUSHR, // -
		// -1, //IAND, // -
		// -2, //LAND, // -
		// -1, //IOR, // -
		// -2, //LOR, // -
		// -1, //IXOR, // -
		// -2, //LXOR, // -
		// 0, //IINC, // visitIincInsn
		// 1, //I2L, // visitInsn
		// 0, //I2F, // -
		// 1, //I2D, // -
		// -1, //L2I, // -
		// -1, //L2F, // -
		// 0, //L2D, // -
		// 0, //F2I, // -
		// 1, //F2L, // -
		// 1, //F2D, // -
		// -1, //D2I, // -
		// 0, //D2L, // -
		// -1, //D2F, // -
		// 0, //I2B, // -
		// 0, //I2C, // -
		// 0, //I2S, // -
		// -3, //LCMP, // -
		// -1, //FCMPL, // -
		// -1, //FCMPG, // -
		// -3, //DCMPL, // -
		// -3, //DCMPG, // -
		// -1, //IFEQ, // visitJumpInsn
		// -1, //IFNE, // -
		// -1, //IFLT, // -
		// -1, //IFGE, // -
		// -1, //IFGT, // -
		// -1, //IFLE, // -
		// -2, //IF_ICMPEQ, // -
		// -2, //IF_ICMPNE, // -
		// -2, //IF_ICMPLT, // -
		// -2, //IF_ICMPGE, // -
		// -2, //IF_ICMPGT, // -
		// -2, //IF_ICMPLE, // -
		// -2, //IF_ACMPEQ, // -
		// -2, //IF_ACMPNE, // -
		// 0, //GOTO, // -
		// 1, //JSR, // -
		// 0, //RET, // visitVarInsn
		// -1, //TABLESWITCH, // visiTableSwitchInsn
		// -1, //LOOKUPSWITCH, // visitLookupSwitch
		// -1, //IRETURN, // visitInsn
		// -2, //LRETURN, // -
		// -1, //FRETURN, // -
		// -2, //DRETURN, // -
		// -1, //ARETURN, // -
		// 0, //RETURN, // -
		// NA, //GETSTATIC, // visitFieldInsn
		// NA, //PUTSTATIC, // -
		// NA, //GETFIELD, // -
		// NA, //PUTFIELD, // -
		// NA, //INVOKEVIRTUAL, // visitMethodInsn
		// NA, //INVOKESPECIAL, // -
		// NA, //INVOKESTATIC, // -
		// NA, //INVOKEINTERFACE, // -
		// NA, //INVOKEDYNAMIC, // visitInvokeDynamicInsn
		// 1, //NEW, // visitTypeInsn
		// 0, //NEWARRAY, // visitIntInsn
		// 0, //ANEWARRAY, // visitTypeInsn
		// 0, //ARRAYLENGTH, // visitInsn
		// NA, //ATHROW, // -
		// 0, //CHECKCAST, // visitTypeInsn
		// 0, //INSTANCEOF, // -
		// -1, //MONITORENTER, // visitInsn
		// -1, //MONITOREXIT, // -
		// NA, //WIDE, // NOT VISITED
		// NA, //MULTIANEWARRAY, // visitMultiANewArrayInsn
		// -1, //IFNULL, // visitJumpInsn
		// -1, //IFNONNULL, // -
		// NA, //GOTO_W, // -
		// NA, //JSR_W, // -
		// };
		// for (i = 0; i < b.length; ++i) {
		// System.err.print((char)('E' + b[i]));
		// }
		// System.err.println();
	}

	//
	// static int[] getStackCompact() {
	// int[] stackCompack = new int[200];
//		// @formatter:off
//		// https://en.wikipedia.org/wiki/Java_bytecode_instruction_listings
//		stackCompack[AALOAD] = (1+1 )-(1); // [arrayref, index → value]    load onto the stack a reference from an array
//		stackCompack[AASTORE] = (1+1+1 )-(0); // [arrayref, index, value →]    store into a reference in an array
//		stackCompack[ACONST_NULL] = (0)-(1); // [→ null]    push a null reference onto the stack
//		stackCompack[ALOAD] = (0)-(1); // [→ objectref]    load a reference onto the stack from a local variable #index
//		//locals[ALOAD_0] = (0)-(1); // [→ objectref]    load a reference onto the stack from local variable 0
//		//locals[ALOAD_1] = (0)-(1); // [→ objectref]    load a reference onto the stack from local variable 1
//		//locals[ALOAD_2] = (0)-(1); // [→ objectref]    load a reference onto the stack from local variable 2
//		//locals[ALOAD_3] = (0)-(1); // [→ objectref]    load a reference onto the stack from local variable 3
//		stackCompack[ANEWARRAY] = (1 )-(1); // [count → arrayref]    create a new array of references of length count and component type identified by the class referenceindex (indexbyte1 << 8 + indexbyte2) in the constant pool
//		stackCompack[ARETURN] = (1 )-(0); // [objectref → [empty]]    return a reference from a method
//		stackCompack[ARRAYLENGTH] = (1 )-(1); // [arrayref → length]    get the length of an array
//		stackCompack[ASTORE] = (1 )-(0); // [objectref →]    store a reference into a local variable #index
//		//locals[ASTORE_0] = (1 )-(0); // [objectref →]    store a reference into local variable 0
//		//locals[ASTORE_1] = (1 )-(0); // [objectref →]    store a reference into local variable 1
//		//locals[ASTORE_2] = (1 )-(0); // [objectref →]    store a reference into local variable 2
//		//locals[ASTORE_3] = (1 )-(0); // [objectref →]    store a reference into local variable 3
//		stackCompack[ATHROW] = 1000; // [objectref → [empty], objectref]    throws an error or exception (notice that the rest of the stack is cleared, leaving only a reference to the Throwable)
//		stackCompack[BALOAD] = (1+1 )-(1); // [arrayref, index → value]    load a byte or Boolean value from an array
//		stackCompack[BASTORE] = (1+1+1 )-(0); // [arrayref, index, value →]    store a byte or Boolean value into an array
//		stackCompack[BIPUSH] = (0)-(1); // [→ value]    push a byte onto the stack as an integer value
//		//locals[BREAKPOINT] = 0; // []    reserved for breakpoints in Java debuggers; should not appear in any class file
//		stackCompack[CALOAD] = (1+1 )-(1); // [arrayref, index → value]    load a char from an array
//		stackCompack[CASTORE] = (1+1+1 )-(0); // [arrayref, index, value →]    store a char into an array
//		stackCompack[CHECKCAST] = (1 )-(1); // [objectref → objectref]    checks whether an objectref is of a certain type, the class reference of which is in the constant pool at index (indexbyte1 << 8 + indexbyte2)
//		stackCompack[D2F] = (1 -1); // [value → result]    convert a double to a float
//		stackCompack[D2I] = (1 -1); // [value → result]    convert a double to an int
//		stackCompack[D2L] = (1 -1); // [value → result]    convert a double to a long
//		stackCompack[DADD] = (1+1 -1); // [value1, value2 → result]    add two doubles
//		stackCompack[DALOAD] = (1+1 )-(1); // [arrayref, index → value]    load a double from an array
//		stackCompack[DASTORE] = (1+1+1 )-(0); // [arrayref, index, value →]    store a double into an array
//		stackCompack[DCMPG] = (1+1 -1); // [value1, value2 → result]    compare two doubles
//		stackCompack[DCMPL] = (1+1 -1); // [value1, value2 → result]    compare two doubles
//		stackCompack[DCONST_0] = (0)-(1); // [→ 0.0]    push the constant 0.0 onto the stack
//		stackCompack[DCONST_1] = (0)-(1); // [→ 1.0]    push the constant 1.0 onto the stack
//		stackCompack[DDIV] = (1+1 -1); // [value1, value2 → result]    divide two doubles
//		stackCompack[DLOAD] = (0)-(1); // [→ value]    load a double value from a local variable #index
//		//locals[DLOAD_0] = (0)-(1); // [→ value]    load a double from local variable 0
//		//locals[DLOAD_1] = (0)-(1); // [→ value]    load a double from local variable 1
//		//locals[DLOAD_2] = (0)-(1); // [→ value]    load a double from local variable 2
//		//locals[DLOAD_3] = (0)-(1); // [→ value]    load a double from local variable 3
//		stackCompack[DMUL] = (1+1 -1); // [value1, value2 → result]    multiply two doubles
//		stackCompack[DNEG] = (1 -1); // [value → result]    negate a double
//		stackCompack[DREM] = (1+1 -1); // [value1, value2 → result]    get the remainder from a division between two doubles
//		stackCompack[DRETURN] = (1 )-(0); // [value → [empty]]    return a double from a method
//		stackCompack[DSTORE] = (1 )-(0); // [value →]    store a double value into a local variable #index
//		//locals[DSTORE_0] = (1 )-(0); // [value →]    store a double into local variable 0
//		//locals[DSTORE_1] = (1 )-(0); // [value →]    store a double into local variable 1
//		//locals[DSTORE_2] = (1 )-(0); // [value →]    store a double into local variable 2
//		//locals[DSTORE_3] = (1 )-(0); // [value →]    store a double into local variable 3
//		stackCompack[DSUB] = (1+1 -1); // [value1, value2 → result]    subtract a double from another
//		stackCompack[DUP] = (1 )-(1+1); // [value → value, value]    duplicate the value on top of the stack
//		stackCompack[DUP_X1] = (1+1 )-(1+1+1); // [value2, value1 → value1, value2, value1]    insert a copy of the top value into the stack two values from the top. value1 and value2 must not be of the type double or long.
//		stackCompack[DUP_X2] = (1+1+1 )-(1+1+1+1); // [value3, value2, value1 → value1, value3, value2, value1]    insert a copy of the top value into the stack two (if value2 is double or long it takes up the entry of value3, too) or three values (if value2 is neither double nor long) from the top
//		stackCompack[DUP2] = ((1+1) )-((1+1)+(1+1)); // [{value2, value1} → {value2, value1}, {value2, value1}]    duplicate top two stack words (two values, if value1 is not double nor long; a single value, if value1 is double or long)
//		stackCompack[DUP2_X1] = (1+(1+1) )-((1+1)+1+(1+1)); // [value3, {value2, value1} → {value2, value1}, value3, {value2, value1}]    duplicate two words and insert beneath third word (see explanation above)
//		stackCompack[DUP2_X2] = ((1+1)+(1+1) )-((1+1)+(1+1)+(1+1)); // [{value4, value3}, {value2, value1} → {value2, value1}, {value4, value3}, {value2, value1}]    duplicate two words and insert beneath fourth word
//		stackCompack[F2D] = (1 -1); // [value → result]    convert a float to a double
//		stackCompack[F2I] = (1 -1); // [value → result]    convert a float to an int
//		stackCompack[F2L] = (1 -1); // [value → result]    convert a float to a long
//		stackCompack[FADD] = (1+1 -1); // [value1, value2 → result]    add two floats
//		stackCompack[FALOAD] = (1+1 )-(1); // [arrayref, index → value]    load a float from an array
//		stackCompack[FASTORE] = (1+1+1 )-(0); // [arrayref, index, value →]    store a float in an array
//		stackCompack[FCMPG] = (1+1 -1); // [value1, value2 → result]    compare two floats
//		stackCompack[FCMPL] = (1+1 -1); // [value1, value2 → result]    compare two floats
//		stackCompack[FCONST_0] = (0)-(1); // [→ 0.0f]    push 0.0f on the stack
//		stackCompack[FCONST_1] = (0)-(1); // [→ 1.0f]    push 1.0f on the stack
//		stackCompack[FCONST_2] = (0)-(1); // [→ 2.0f]    push 2.0f on the stack
//		stackCompack[FDIV] = (1+1 -1); // [value1, value2 → result]    divide two floats
//		stackCompack[FLOAD] = (0)-(1); // [→ value]    load a float value from a local variable #index
//		//locals[FLOAD_0] = (0)-(1); // [→ value]    load a float value from local variable 0
//		//locals[FLOAD_1] = (0)-(1); // [→ value]    load a float value from local variable 1
//		//locals[FLOAD_2] = (0)-(1); // [→ value]    load a float value from local variable 2
//		//locals[FLOAD_3] = (0)-(1); // [→ value]    load a float value from local variable 3
//		stackCompack[FMUL] = (1+1 -1); // [value1, value2 → result]    multiply two floats
//		stackCompack[FNEG] = (1 -1); // [value → result]    negate a float
//		stackCompack[FREM] = (1+1 -1); // [value1, value2 → result]    get the remainder from a division between two floats
//		stackCompack[FRETURN] = (1 )-(0); // [value → [empty]]    return a float
//		stackCompack[FSTORE] = (1 )-(0); // [value →]    store a float value into a local variable #index
//		//locals[FSTORE_0] = (1 )-(0); // [value →]    store a float value into local variable 0
//		//locals[FSTORE_1] = (1 )-(0); // [value →]    store a float value into local variable 1
//		//locals[FSTORE_2] = (1 )-(0); // [value →]    store a float value into local variable 2
//		//locals[FSTORE_3] = (1 )-(0); // [value →]    store a float value into local variable 3
//		stackCompack[FSUB] = (1+1 -1); // [value1, value2 → result]    subtract two floats
//		stackCompack[GETFIELD] = (1 )-(1); // [objectref → value]    get a field value of an object objectref, where the field is identified by field reference in the constant pool index (index1 << 8 + index2)
//		stackCompack[GETSTATIC] = (0)-(1); // [→ value]    get a static field value of a class, where the field is identified by field reference in the constant pool index (index1 << 8 + index2)
//		stackCompack[GOTO] = 0; // [[no change]]    goes to another instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 + branchbyte2)
//		//locals[GOTO_W] = 0; // [[no change]]    goes to another instruction at branchoffset (signed int constructed from unsigned bytes branchbyte1 << 24 + branchbyte2 << 16 + branchbyte3 << 8 + branchbyte4)
//		stackCompack[I2B] = (1 -1); // [value → result]    convert an int into a byte
//		stackCompack[I2C] = (1 -1); // [value → result]    convert an int into a character
//		stackCompack[I2D] = (1 -1); // [value → result]    convert an int into a double
//		stackCompack[I2F] = (1 -1); // [value → result]    convert an int into a float
//		stackCompack[I2L] = (1 -1); // [value → result]    convert an int into a long
//		stackCompack[I2S] = (1 -1); // [value → result]    convert an int into a short
//		stackCompack[IADD] = (1+1 -1); // [value1, value2 → result]    add two ints
//		stackCompack[IALOAD] = (1+1 )-(1); // [arrayref, index → value]    load an int from an array
//		stackCompack[IAND] = (1+1 -1); // [value1, value2 → result]    perform a bitwise and on two integers
//		stackCompack[IASTORE] = (1+1+1 )-(0); // [arrayref, index, value →]    store an int into an array
//		stackCompack[ICONST_M1] = (0)-(-1); // [→ -1]    load the int value -1 onto the stack
//		stackCompack[ICONST_0] = (0)-(0); // [→ 0]    load the int value 0 onto the stack
//		stackCompack[ICONST_1] = (0)-(1); // [→ 1]    load the int value 1 onto the stack
//		stackCompack[ICONST_2] = (0)-(2); // [→ 2]    load the int value 2 onto the stack
//		stackCompack[ICONST_3] = (0)-(3); // [→ 3]    load the int value 3 onto the stack
//		stackCompack[ICONST_4] = (0)-(4); // [→ 4]    load the int value 4 onto the stack
//		stackCompack[ICONST_5] = (0)-(5); // [→ 5]    load the int value 5 onto the stack
//		stackCompack[IDIV] = (1+1 -1); // [value1, value2 → result]    divide two integers
//		stackCompack[IF_ACMPEQ] = (1+1 )-(0); // [value1, value2 →]    if references are equal, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 + branchbyte2)
//		stackCompack[IF_ACMPNE] = (1+1 )-(0); // [value1, value2 →]    if references are not equal, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 + branchbyte2)
//		stackCompack[IF_ICMPEQ] = (1+1 )-(0); // [value1, value2 →]    if ints are equal, branch to instruction at branchoffset (signed short constructed from unsigned bytesbranchbyte1 << 8 + branchbyte2)
//		stackCompack[IF_ICMPGE] = (1+1 )-(0); // [value1, value2 →]    if value1 is greater than or equal to value2, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 + branchbyte2)
//		stackCompack[IF_ICMPGT] = (1+1 )-(0); // [value1, value2 →]    if value1 is greater than value2, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 + branchbyte2)
//		stackCompack[IF_ICMPLE] = (1+1 )-(0); // [value1, value2 →]    if value1 is less than or equal to value2, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 + branchbyte2)
//		stackCompack[IF_ICMPLT] = (1+1 )-(0); // [value1, value2 →]    if value1 is less than value2, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 + branchbyte2)
//		stackCompack[IF_ICMPNE] = (1+1 )-(0); // [value1, value2 →]    if ints are not equal, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 + branchbyte2)
//		stackCompack[IFEQ] = (1 )-(0); // [value →]    if value is 0, branch to instruction at branchoffset (signed short constructed from unsigned bytesbranchbyte1 << 8 + branchbyte2)
//		stackCompack[IFGE] = (1 )-(0); // [value →]    if value is greater than or equal to 0, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 + branchbyte2)
//		stackCompack[IFGT] = (1 )-(0); // [value →]    if value is greater than 0, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 + branchbyte2)
//		stackCompack[IFLE] = (1 )-(0); // [value →]    if value is less than or equal to 0, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 + branchbyte2)
//		stackCompack[IFLT] = (1 )-(0); // [value →]    if value is less than 0, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 + branchbyte2)
//		stackCompack[IFNE] = (1 )-(0); // [value →]    if value is not 0, branch to instruction at branchoffset (signed short constructed from unsigned bytesbranchbyte1 << 8 + branchbyte2)
//		stackCompack[IFNONNULL] = (1 )-(0); // [value →]    if value is not null, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 + branchbyte2)
//		stackCompack[IFNULL] = (1 )-(0); // [value →]    if value is null, branch to instruction at branchoffset (signed short constructed from unsigned bytesbranchbyte1 << 8 + branchbyte2)
//		stackCompack[IINC] = 0; // [[No change]]    increment local variable #index by signed byte const
//		stackCompack[ILOAD] = (0)-(1); // [→ value]    load an int value from a local variable #index
//		//locals[ILOAD_0] = (0)-(1); // [→ value]    load an int value from local variable 0
//		//locals[ILOAD_1] = (0)-(1); // [→ value]    load an int value from local variable 1
//		//locals[ILOAD_2] = (0)-(1); // [→ value]    load an int value from local variable 2
//		//locals[ILOAD_3] = (0)-(1); // [→ value]    load an int value from local variable 3
//		//locals[IMPDEP1] = 0; // []    reserved for implementation-dependent operations within debuggers; should not appear in any class file
//		//locals[IMPDEP2] = 0; // []    reserved for implementation-dependent operations within debuggers; should not appear in any class file
//		stackCompack[IMUL] = (1+1 -1); // [value1, value2 → result]    multiply two integers
//		stackCompack[INEG] = (1 -1); // [value → result]    negate int
//		stackCompack[INSTANCEOF] = (1 -1); // [objectref → result]    determines if an object objectref is of a given type, identified by class reference index in constant pool (indexbyte1 << 8 + indexbyte2)
//		stackCompack[INVOKEDYNAMIC] = 1000; // [[arg1, [arg2 ...]] →]    invokes a dynamic method identified by method reference index in constant pool (indexbyte1 << 8 + indexbyte2)
//		stackCompack[INVOKEINTERFACE] = 1000; // [objectref, [arg1, arg2, ...] →]    invokes an interface method on object objectref, where the interface method is identified by method reference index in constant pool (indexbyte1 << 8 + indexbyte2)
//		stackCompack[INVOKESPECIAL] = 1000; // [objectref, [arg1, arg2, ...] →]    invoke instance method on object objectref, where the method is identified by method reference indexin constant pool (indexbyte1 << 8 + indexbyte2)
//		stackCompack[INVOKESTATIC] = 1000; // [[arg1, arg2, ...] →]    invoke a static method, where the method is identified by method reference index in constant pool (indexbyte1 << 8 + indexbyte2)
//		stackCompack[INVOKEVIRTUAL] = 1000; // [objectref, [arg1, arg2, ...] →]    invoke virtual method on object objectref, where the method is identified by method reference index in constant pool (indexbyte1 << 8 + indexbyte2)
//		stackCompack[IOR] = (1+1 -1); // [value1, value2 → result]    bitwise int or
//		stackCompack[IREM] = (1+1 -1); // [value1, value2 → result]    logical int remainder
//		stackCompack[IRETURN] = (1 )-(0); // [value → [empty]]    return an integer from a method
//		stackCompack[ISHL] = (1+1 -1); // [value1, value2 → result]    int shift left
//		stackCompack[ISHR] = (1+1 -1); // [value1, value2 → result]    int arithmetic shift right
//		stackCompack[ISTORE] = (1 )-(0); // [value →]    store int value into variable #index
//		//locals[ISTORE_0] = (1 )-(0); // [value →]    store int value into variable 0
//		//locals[ISTORE_1] = (1 )-(0); // [value →]    store int value into variable 1
//		//locals[ISTORE_2] = (1 )-(0); // [value →]    store int value into variable 2
//		//locals[ISTORE_3] = (1 )-(0); // [value →]    store int value into variable 3
//		stackCompack[ISUB] = (1+1 -1); // [value1, value2 → result]    int subtract
//		stackCompack[IUSHR] = (1+1 -1); // [value1, value2 → result]    int logical shift right
//		stackCompack[IXOR] = (1+1 -1); // [value1, value2 → result]    int xor
//		stackCompack[JSR] = (-1); // [→ address]    jump to subroutine at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 + branchbyte2) and place the return address on the stack
//	//	locals[JSR_W] = (-1); // [→ address]    jump to subroutine at branchoffset (signed int constructed from unsigned bytes branchbyte1 << 24 + branchbyte2 << 16 + branchbyte3 << 8 + branchbyte4) and place the return address on the stack
//		stackCompack[L2D] = (1 -1); // [value → result]    convert a long to a double
//		stackCompack[L2F] = (1 -1); // [value → result]    convert a long to a float
//		stackCompack[L2I] = (1 -1); // [value → result]    convert a long to a int
//		stackCompack[LADD] = (1+1 -1); // [value1, value2 → result]    add two longs
//		stackCompack[LALOAD] = (1+1 )-(1); // [arrayref, index → value]    load a long from an array
//		stackCompack[LAND] = (1+1 -1); // [value1, value2 → result]    bitwise and of two longs
//		stackCompack[LASTORE] = (1+1+1 )-(0); // [arrayref, index, value →]    store a long to an array
//		stackCompack[LCMP] = (1+1 -1); // [value1, value2 → result]    compare two longs values
//		stackCompack[LCONST_0] = (0)-(0); // [→ 0L]    push the long 0 onto the stack
//		stackCompack[LCONST_1] = (0)-(1); // [→ 1L]    push the long 1 onto the stack
//		stackCompack[LDC] = (0)-(1); // [→ value]    push a constant #index from a constant pool (String, int or float) onto the stack
//		//locals[LDC_W] = (0)-(1); // [→ value]    push a constant #index from a constant pool (String, int or float) onto the stack (wide index is constructed as indexbyte1 << 8 + indexbyte2)
//		//locals[LDC2_W] = (0)-(1); // [→ value]    push a constant #index from a constant pool (double or long) onto the stack (wide index is constructed as indexbyte1 << 8 + indexbyte2)
//		stackCompack[LDIV] = (1+1 -1); // [value1, value2 → result]    divide two longs
//		stackCompack[LLOAD] = (0)-(1); // [→ value]    load a long value from a local variable #index
//		//locals[LLOAD_0] = (0)-(1); // [→ value]    load a long value from a local variable 0
//		//locals[LLOAD_1] = (0)-(1); // [→ value]    load a long value from a local variable 1
//		//locals[LLOAD_2] = (0)-(1); // [→ value]    load a long value from a local variable 2
//		//locals[LLOAD_3] = (0)-(1); // [→ value]    load a long value from a local variable 3
//		stackCompack[LMUL] = (1+1 -1); // [value1, value2 → result]    multiply two longs
//		stackCompack[LNEG] = (1 -1); // [value → result]    negate a long
//		stackCompack[LOOKUPSWITCH] = 1000; // [key →]    a target address is looked up from a table using a key and execution continues from the instruction at that address
//		stackCompack[LOR] = (1+1 -1); // [value1, value2 → result]    bitwise or of two longs
//		stackCompack[LREM] = (1+1 -1); // [value1, value2 → result]    remainder of division of two longs
//		stackCompack[LRETURN] = (1 )-(0); // [value → [empty]]    return a long value
//		stackCompack[LSHL] = (1+1 -1); // [value1, value2 → result]    bitwise shift left of a long value1 by value2 positions
//		stackCompack[LSHR] = (1+1 -1); // [value1, value2 → result]    bitwise shift right of a long value1 by value2 positions
//		stackCompack[LSTORE] = (1 )-(0); // [value →]    store a long value in a local variable #index
//		//locals[LSTORE_0] = (1 )-(0); // [value →]    store a long value in a local variable 0
//		//locals[LSTORE_1] = (1 )-(0); // [value →]    store a long value in a local variable 1
//		//locals[LSTORE_2] = (1 )-(0); // [value →]    store a long value in a local variable 2
//		//locals[LSTORE_3] = (1 )-(0); // [value →]    store a long value in a local variable 3
//		stackCompack[LSUB] = (1+1 -1); // [value1, value2 → result]    subtract two longs
//		stackCompack[LUSHR] = (1+1 -1); // [value1, value2 → result]    bitwise shift right of a long value1 by value2 positions, unsigned
//		stackCompack[LXOR] = (1+1 -1); // [value1, value2 → result]    bitwise exclusive or of two longs
//		stackCompack[MONITORENTER] = (1 )-(0); // [objectref →]    enter monitor for object ("grab the lock" - start of synchronized() section)
//		stackCompack[MONITOREXIT] = (1 )-(0); // [objectref →]    exit monitor for object ("release the lock" - end of synchronized() section)
//		stackCompack[MULTIANEWARRAY] = 1000; // [count1, [count2,...] → arrayref]    create a new array of dimensions dimensions with elements of type identified by class reference in constant pool index (indexbyte1 << 8 + indexbyte2); the sizes of each dimension is identified bycount1, [count2, etc.]
//		stackCompack[NEW] = (0)-(1); // [→ objectref]    create new object of type identified by class reference in constant pool index (indexbyte1 << 8 + indexbyte2)
//		stackCompack[NEWARRAY] = 1000; // [count → arrayref]    create new array with count elements of primitive type identified by atype
//		stackCompack[NOP] = (0); // [[No change]]    perform no operation
//		stackCompack[POP] = (1 )-(0); // [value →]    discard the top value on the stack
//		stackCompack[POP2] = ((1+1) )-(0); // [{value2, value1} →]    discard the top two values on the stack (or one value, if it is a double or long)
//		stackCompack[PUTFIELD] = (1+1 )-(0); // [objectref, value →]    set field to value in an object objectref, where the field is identified by a field reference index in constant pool (indexbyte1 << 8 + indexbyte2)
//		stackCompack[PUTSTATIC] = (1 )-(0); // [value →]    set static field to value in a class, where the field is identified by a field reference index in constant pool (indexbyte1 << 8 + indexbyte2)
//		stackCompack[RET] = (0); // [[No change]]    continue execution from address taken from a local variable #index (the asymmetry with jsr is intentional)
//		stackCompack[RETURN] = (0)-(0); // [→ [empty]]    return void from method
//		stackCompack[SALOAD] = (1+1 )-(1); // [arrayref, index → value]    load short from array
//		stackCompack[SASTORE] = (1+1+1 )-(0); // [arrayref, index, value →]    store short to array
//		stackCompack[SIPUSH] = (0)-(1); // [→ value]    push a short onto the stack
//		stackCompack[SWAP] = (1+1 )-(1+1); // [value2, value1 → value1, value2]    swaps two top words on the stack (note that value1 and value2 must not be double or long)
//		stackCompack[TABLESWITCH] = (1 )-(0); // [index →]    continue execution from an address in the table at offset index
//		//locals[WIDE] = 1000; // [[same as for corresponding instructions]]    execute opcode, where opcode is either iload, fload, aload, lload, dload, istore, fstore, astore, lstore, dstore, or ret, but assume the index is 16 bit; or execute iinc, where the index is 16 bits and the constant to increment by is a signed 16 bit short
//		// @formatter:on
	// return stackCompack;
	// }
}
