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

package org.axonframework.samples.bank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import com.nebula.cqrs.axon.CQRSBuilder;
import com.nebula.cqrs.axonweb.CQRSWebSpringApplicationListener;

//@SpringBootApplication
@Configuration
@EnableAutoConfiguration
@ComponentScan(excludeFilters = { @Filter(RestController.class), @Filter(Controller.class) })
public class AxonBankApplication {

	public static void main(String[] args) {
		CQRSBuilder cqrsBuilder = new CQRSBuilder();
		SpringApplication application = new SpringApplication(AxonBankApplication.class);
		application.addListeners(new CQRSWebSpringApplicationListener(cqrsBuilder));
		application.run(args);
	}
}
