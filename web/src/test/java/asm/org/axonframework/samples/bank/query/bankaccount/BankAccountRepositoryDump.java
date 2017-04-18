package asm.org.axonframework.samples.bank.query.bankaccount;
import java.util.*;
import org.objectweb.asm.*;
public class BankAccountRepositoryDump implements Opcodes {

public static byte[] dump () throws Exception {

ClassWriter cw = new ClassWriter(0);
FieldVisitor fv;
MethodVisitor mv;
AnnotationVisitor av0;

cw.visit(52, ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, "org/axonframework/samples/bank/query/bankaccount/BankAccountRepository", "Ljava/lang/Object;Lorg/springframework/data/repository/CrudRepository<Lorg/axonframework/samples/bank/query/bankaccount/BankAccountEntry;Ljava/lang/String;>;", "java/lang/Object", new String[] { "org/springframework/data/repository/CrudRepository" });

cw.visitSource("BankAccountRepository.java", null);

{
av0 = cw.visitAnnotation("Lorg/springframework/stereotype/Repository;", true);
av0.visitEnd();
}
{
mv = cw.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "findAllByOrderByIdAsc", "()Ljava/lang/Iterable;", "()Ljava/lang/Iterable<Lorg/axonframework/samples/bank/query/bankaccount/BankAccountEntry;>;", null);
mv.visitEnd();
}
{
mv = cw.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "findOneByAxonBankAccountId", "(Ljava/lang/String;)Lorg/axonframework/samples/bank/query/bankaccount/BankAccountEntry;", null, null);
mv.visitEnd();
}
cw.visitEnd();

return cw.toByteArray();
}
}
