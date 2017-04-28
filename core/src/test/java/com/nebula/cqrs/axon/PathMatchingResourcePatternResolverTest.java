package com.nebula.cqrs.axon;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public class PathMatchingResourcePatternResolverTest {

	@Test
	public void testGetResources() throws IOException {
		CQRSBuilder cqrsBuilder = new CQRSBuilder();
		Thread.currentThread().setContextClassLoader(cqrsBuilder.getClassLoader());
		cqrsBuilder.makeDomainCQRSHelper("org.axonframework.samples.bank.cqrs.MyBankAccount");
		PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver = new PathMatchingResourcePatternResolver();
		Resource[] resources = pathMatchingResourcePatternResolver.getResources("classpath*:org/axonframework/samples/**/**.class");

		Arrays.stream(resources).forEach(r -> System.out.println(r.getDescription()));
	}
}