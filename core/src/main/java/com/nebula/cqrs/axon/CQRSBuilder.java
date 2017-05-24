package com.nebula.cqrs.axon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nebula.cqrs.axon.asm.AnalyzeEventsClassVisitor;
import com.nebula.cqrs.axon.asm.AnalyzeFieldClassVisitor;
import com.nebula.cqrs.axon.asm.CQRSCommandBuilder;
import com.nebula.cqrs.axon.asm.CQRSCommandHandlerBuilder;
import com.nebula.cqrs.axon.asm.CQRSCommandHandlerCallerBuilder;
import com.nebula.cqrs.axon.asm.CQRSCommandHandlerCtorCallerBuilder;
import com.nebula.cqrs.axon.asm.CQRSEventAliasBuilder;
import com.nebula.cqrs.axon.asm.CQRSEventRealBuilder;
import com.nebula.cqrs.axon.asm.CQRSMakeDomainImplClassVisitor;
import com.nebula.cqrs.axon.asm.RemoveCqrsAnnotationClassVisitor;
import com.nebula.cqrs.axon.pojo.Command;
import com.nebula.cqrs.axon.pojo.DomainDefinition;
import com.nebula.cqrs.axon.pojo.Event;
import com.nebula.tinyasm.util.AnalyzeMethodParamsClassVisitor;
import com.nebula.tinyasm.util.Field;
import com.nebula.tinyasm.util.RenameClassVisitor;

public class CQRSBuilder implements CQRSContext {
    public final static Logger LOGGER = LoggerFactory.getLogger(CQRSBuilder.class);

    final MyClassLoader classLoader = new MyClassLoader();

    public CQRSBuilder() {
        super();
        // Thread.currentThread().setContextClassLoader(classLoader);
        listeners.add(new CommandHandlerListener());
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    final List<DomainListener> listeners = new ArrayList<>();

    public void add(DomainListener domainListener) {
        this.listeners.add(domainListener);
    }

    static class CommandHandlerListener implements DomainListener {
        @Override
        public void define(CQRSContext ctx, DomainDefinition domainDefinition) {
            try {
                Type typeHandler = domainDefinition.typeOf("CommandHandler");

                for (Command command : domainDefinition.commands.values()) {
                    if (command.ctorMethod) {
                        Type callerType = Type.getObjectType(typeHandler.getInternalName() + "$Inner" + command.commandName);
                        byte[] callerCode = CQRSCommandHandlerCtorCallerBuilder.dump(typeHandler, callerType, domainDefinition.implDomainType, command.type,
                                command, domainDefinition);
                        LOGGER.debug("Create command inner class [{}]", callerType.getClassName());
                        ctx.defineClass(callerType.getClassName(), callerCode);
                    } else {
                        Type callerType = Type.getObjectType(typeHandler.getInternalName() + "$Inner" + command.commandName);
                        byte[] callerCode = CQRSCommandHandlerCallerBuilder.dump(typeHandler, callerType, domainDefinition.implDomainType, command.type,
                                command, domainDefinition);
                        LOGGER.debug("Create command inner class [{}]", callerType.getClassName());
                        ctx.defineClass(callerType.getClassName(), callerCode);
                    }
                }

                byte[] codeHandler = CQRSCommandHandlerBuilder.dump(typeHandler, domainDefinition.implDomainType, domainDefinition);
                LOGGER.debug("Create command handler [{}]", typeHandler.getClassName());
                ctx.defineClass(typeHandler.getClassName(), codeHandler);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return;
        }
    }

    public DomainDefinition makeDomainCQRSHelper(String srcDomainClassName) {
        LOGGER.debug("Start make domain [{}]", srcDomainClassName);

        try {
            final DomainDefinition domainDefinition;
            Type srcDomainType;
            String domainName = srcDomainClassName.substring(srcDomainClassName.lastIndexOf('.') + 1);
            {
                srcDomainType = Type.getObjectType(srcDomainClassName.replace('.', '/'));
            }

            domainDefinition = analyzeDomain(domainName, srcDomainType);

            makeDomainImpl(domainDefinition, srcDomainType, domainDefinition.implDomainType);

            listeners.forEach(l -> l.define(CQRSBuilder.this, domainDefinition));
            
            return domainDefinition;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void makeDomainImpl(final DomainDefinition domainDefinition, Type srcDomainType, Type implDomainType) throws IOException {

        ClassReader cr = new ClassReader(srcDomainType.getClassName());
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);

        CQRSMakeDomainImplClassVisitor cvMakeDomainImpl = new CQRSMakeDomainImplClassVisitor(cw, domainDefinition);
        RenameClassVisitor cvRename = new RenameClassVisitor(cvMakeDomainImpl, srcDomainType.getInternalName(), implDomainType.getInternalName());

        RemoveCqrsAnnotationClassVisitor removeCqrsAnnotation = new RemoveCqrsAnnotationClassVisitor(cvRename);
        cr.accept(removeCqrsAnnotation, 0);

        LOGGER.debug("Rename domain class from {} to impl class [{}]", srcDomainType.getClassName(), implDomainType.getClassName());
        LOGGER.debug("Made cqrs domain class [{}]", implDomainType.getClassName());

        byte[] code = cw.toByteArray();

        classLoader.load(implDomainType.getClassName(), code);

        for (Event event : domainDefinition.realEvents.values()) {
            byte[] eventCode = CQRSEventRealBuilder.dump(event);
            LOGGER.debug("Create event [{}]", event.type.getClassName());
            classLoader.load(event.type.getClassName(), eventCode);
        }
        for (Event event : domainDefinition.virtualEvents.values()) {
            byte[] eventCode = CQRSEventAliasBuilder.dump(event);
            LOGGER.debug("Create command handler [{}]", event.type.getClassName());
            classLoader.load(event.type.getClassName(), eventCode);
        }
        for (Command command : domainDefinition.commands.values()) {
            if (command.ctorMethod) {
                byte[] codeCommand = CQRSCommandBuilder.dump(command);
                LOGGER.debug("Create command handler [{}]", command.type.getClassName());
                classLoader.load(command.type.getClassName(), codeCommand);
            } else {
                byte[] codeCommand = CQRSCommandBuilder.dump(command);
                LOGGER.debug("Create command handler [{}]", command.type.getClassName());
                classLoader.load(command.type.getClassName(), codeCommand);
            }
        }
    }

    private DomainDefinition analyzeDomain(String srcDomainName, Type srcDomainType) throws IOException {
        final DomainDefinition domainDefinition;
        domainDefinition = new DomainDefinition(srcDomainName, srcDomainType);
        ClassReader cr = new ClassReader(srcDomainType.getClassName());

        {
            AnalyzeMethodParamsClassVisitor analyzeMethodParamsClassVisitor = new AnalyzeMethodParamsClassVisitor();
            AnalyzeFieldClassVisitor analyzeFieldClassVisitor = new AnalyzeFieldClassVisitor(analyzeMethodParamsClassVisitor);
            cr.accept(analyzeFieldClassVisitor, 0);
            domainDefinition.methods = analyzeMethodParamsClassVisitor.getMethods();
            domainDefinition.fields = analyzeFieldClassVisitor.finished().toArray(new Field[0]);
            for (Field field : domainDefinition.fields) {
                if (field.identifier) {
                    domainDefinition.identifierField = field;
                }
            }
        }

        {
            AnalyzeEventsClassVisitor analyzeEventsClassVisitor = new AnalyzeEventsClassVisitor(domainDefinition);
            cr.accept(analyzeEventsClassVisitor, 0);
            domainDefinition.realEvents = analyzeEventsClassVisitor.finished();
        }

        return domainDefinition;
    }

    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return this.classLoader.loadClass(name);
    }

    @SuppressWarnings("unchecked")
    public <T> void defineClass(String name, byte[] b) {
        this.classLoader.load(name, b);
    }

}
