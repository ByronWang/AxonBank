package org.axonframework.samples.bank.config;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;

import com.nebula.cqrs.core.CqrsEntity;

public abstract class AggregateClassUtils {

	// private static final String Aggregate_CLASS_ATTRIBUTE =
	// Conventions.getQualifiedAttributeName(AggregateClassPostProcessor.class,
	// "AggregateClass");
	//
	// private static final String ORDER_ATTRIBUTE =
	// Conventions.getQualifiedAttributeName(AggregateClassPostProcessor.class,
	// "order");

	private static final Log logger = LogFactory.getLog(AggregateClassUtils.class);

	private static final Set<String> candidateIndicators = new HashSet<>(4);

	static {
		// candidateIndicators.add(Component.class.getName());
		// candidateIndicators.add(ComponentScan.class.getName());
		// candidateIndicators.add(Import.class.getName());
		// candidateIndicators.add(ImportResource.class.getName());
	}

	/**
	 * Check whether the given bean definition is a candidate for a Aggregate
	 * class (or a nested component class declared within a Aggregate/component
	 * class, to be auto-registered as well), and mark it accordingly.
	 * 
	 * @param beanDef
	 *            the bean definition to check
	 * @param metadataReaderFactory
	 *            the current factory in use by the caller
	 * @return whether the candidate qualifies as (any kind of) Aggregate class
	 */
	public static boolean checkAggregateClassCandidate(BeanDefinition beanDef) {
		String className = beanDef.getBeanClassName();
		if (className == null) {
			return false;
		}

		AnnotationMetadata metadata;
		if (beanDef instanceof AnnotatedBeanDefinition && className.equals(((AnnotatedBeanDefinition) beanDef).getMetadata().getClassName())) {
			// Can reuse the pre-parsed metadata from the given
			// BeanDefinition...
			metadata = ((AnnotatedBeanDefinition) beanDef).getMetadata();
		} else if (beanDef instanceof AbstractBeanDefinition && ((AbstractBeanDefinition) beanDef).hasBeanClass()) {
			// Check already loaded Class if present...
			// since we possibly can't even load the class file for this Class.
			Class<?> beanClass = ((AbstractBeanDefinition) beanDef).getBeanClass();
			metadata = new StandardAnnotationMetadata(beanClass, true);
		} else {
			return false;
		}

		if (isFullAggregateCandidate(metadata)) {
			// beanDef.setAttribute(Aggregate_CLASS_ATTRIBUTE,
			// Aggregate_CLASS_FULL);
		} else if (isLiteAggregateCandidate(metadata)) {
			// beanDef.setAttribute(Aggregate_CLASS_ATTRIBUTE,
			// Aggregate_CLASS_LITE);
		} else {
			return false;
		}

		// // It's a full or lite Aggregate candidate... Let's determine the
		// order value, if any.
		// Integer order = getOrder(metadata);
		// if (order != null) {
		// beanDef.setAttribute(ORDER_ATTRIBUTE, order);
		// }

		return true;
	}

	/**
	 * Check the given metadata for a Aggregate class candidate (or nested
	 * component class declared within a Aggregate/component class).
	 * 
	 * @param metadata
	 *            the metadata of the annotated class
	 * @return {@code true} if the given class is to be registered as a
	 *         reflection-detected bean definition; {@code false} otherwise
	 */
	public static boolean isAggregateCandidate(AnnotationMetadata metadata) {
		return (isFullAggregateCandidate(metadata) || isLiteAggregateCandidate(metadata));
	}

	/**
	 * Check the given metadata for a full Aggregate class candidate (i.e. a
	 * class annotated with {@code @Aggregate}).
	 * 
	 * @param metadata
	 *            the metadata of the annotated class
	 * @return {@code true} if the given class is to be processed as a full
	 *         Aggregate class, including cross-method call interception
	 */
	public static boolean isFullAggregateCandidate(AnnotationMetadata metadata) {
		return metadata.isAnnotated(CqrsEntity.class.getName());
	}

	/**
	 * Check the given metadata for a lite Aggregate class candidate (e.g. a
	 * class annotated with {@code @Component} or just having {@code @Import}
	 * declarations or {@code @Bean methods}).
	 * 
	 * @param metadata
	 *            the metadata of the annotated class
	 * @return {@code true} if the given class is to be processed as a lite
	 *         Aggregate class, just registering it and scanning it for
	 *         {@code @Bean} methods
	 */
	public static boolean isLiteAggregateCandidate(AnnotationMetadata metadata) {
		// Do not consider an interface or an annotation...
		if (metadata.isInterface()) {
			return false;
		}

		// Any of the typical annotations found?
		for (String indicator : candidateIndicators) {
			if (metadata.isAnnotated(indicator)) {
				return true;
			}
		}
		return false;
//
//		// Finally, let's look for @Bean methods...
//		try {
//			return metadata.hasAnnotatedMethods(Bean.class.getName());
//		} catch (Throwable ex) {
//			if (logger.isDebugEnabled()) {
//				logger.debug("Failed to introspect @Bean methods on class [" + metadata.getClassName() + "]: " + ex);
//			}
//			return false;
//		}
	}
}