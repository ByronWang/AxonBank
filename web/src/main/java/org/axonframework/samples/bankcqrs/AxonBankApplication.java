/*
 * Copyright (c) 2016. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.axonframework.samples.bankcqrs;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import com.nebula.cqrs.axon.CQRSBuilder;
import com.nebula.cqrs.axonweb.CQRSWebSpringApplicationListener;
import com.nebula.cqrs.core.CqrsEntity;

//@SpringBootApplication
@Configuration
@EnableAutoConfiguration
@ComponentScan // (excludeFilters = { @Filter(RestController.class),
               // @Filter(Controller.class) })
public class AxonBankApplication {

    private static final String CLASS_RESOURCE_PATTERN = "/**/*.class";

    public static void main(String[] args) {
        prepare();
        SpringApplication application = new SpringApplication(AxonBankApplication.class);
        // application.addListeners(applicationListener);
        application.run(args);
    }

    @SuppressWarnings("unused")
    private static void prepare() {

        try {
            CQRSBuilder cqrsBuilder = new CQRSBuilder();
            CQRSWebSpringApplicationListener applicationListener = new CQRSWebSpringApplicationListener(cqrsBuilder);
            cqrsBuilder.add(applicationListener);

            // String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
            // +
            // ClassUtils.convertClassNameToResourcePath(pkg) +
            // CLASS_RESOURCE_PATTERN;
            PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = pathMatchingResourcePatternResolver.getResources(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                    + AxonBankApplication.class.getPackage().getName().replace('.', '/') + CLASS_RESOURCE_PATTERN);
            SimpleMetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory();
            AnnotationTypeFilter annotationTypeFilter = new AnnotationTypeFilter(CqrsEntity.class);
            for (Resource resource : resources) {
                if (resource.isReadable()) {
                    MetadataReader reader = metadataReaderFactory.getMetadataReader(resource);
                    String className = reader.getClassMetadata().getClassName();
                    if (annotationTypeFilter.match(reader, metadataReaderFactory)) {
                        // System.out.println("domain " + className);
                        cqrsBuilder.makeDomainCQRSHelper(className);
                    }
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
