package com.nebula.cqrs.axon.asm;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.Type;

import com.nebula.cqrs.axon.CQRSBuilder;
import com.nebula.cqrs.axonweb.asm.query.CQRSWebEntryBuilder;
import com.nebula.cqrs.core.asm.Field;

public class CQRSWebEntryBuilderTest {

	CQRSBuilder cqrs;

	@Before
	public void setUp() throws Exception {
		cqrs = new CQRSBuilder();
	}

	@Test
	public void testDump() throws Exception {

		Type objectType = Type.getObjectType("test/test/MyAccount");
		List<Field> objectFields = new ArrayList<>();

		CQRSWebEntryBuilder.dump(objectType, objectFields.toArray(new Field[0]));
	}

	@Test
	public void testDefine() throws Exception {

		Type objectType = Type.getObjectType("test/test/MyAccount");
		List<Field> objectFields = new ArrayList<>();
		objectFields.add(new Field("axonBankAccountId", Type.getType(String.class)));
		objectFields.add(new Field("overdraftLimit", Type.getType(long.class)));
		objectFields.add(new Field("i", Type.getType(int.class)));
		objectFields.add(new Field("balanceInCents", Type.getType(long.class)));
		objectFields.add(new Field("balanceInCentsxx", Type.getType(long.class)));

		byte[] code = CQRSWebEntryBuilder.dump(objectType, objectFields.toArray(new Field[0]));

		Class<?> clz = cqrs.defineClass(objectType.getClassName(), code);
		clz.newInstance();
	}
}
