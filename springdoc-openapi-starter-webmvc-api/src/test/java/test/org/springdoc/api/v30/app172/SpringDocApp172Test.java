/*
 *
 *  *
 *  *  *
 *  *  *  *
 *  *  *  *  * Copyright 2019-2022 the original author or authors.
 *  *  *  *  *
 *  *  *  *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  *  *  * you may not use this file except in compliance with the License.
 *  *  *  *  * You may obtain a copy of the License at
 *  *  *  *  *
 *  *  *  *  *      https://www.apache.org/licenses/LICENSE-2.0
 *  *  *  *  *
 *  *  *  *  * Unless required by applicable law or agreed to in writing, software
 *  *  *  *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  *  *  *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  *  *  * See the License for the specific language governing permissions and
 *  *  *  *  * limitations under the License.
 *  *  *  *
 *  *  *
 *  *
 *
 */

package test.org.springdoc.api.v30.app172;

import org.junit.jupiter.api.Test;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.utils.Constants;
import test.org.springdoc.api.v30.AbstractSpringDocV30Test;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestPropertySource;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource(properties = { "springdoc.show-actuator=true", "management.endpoints.enabled-by-default=true",
		"management.endpoints.web.exposure.include = tenant" })
public class SpringDocApp172Test extends AbstractSpringDocV30Test {

	@Test
	public void testApp() throws Exception {
		mockMvc.perform(get(Constants.DEFAULT_API_DOCS_URL + "/sample-group"))
				.andExpect(jsonPath("$.openapi", is("3.0.1")))
				.andExpect(status().isOk())
				.andExpect(content().json(getContent("results/3.0.1/app172.json"), true));
	}

	@SpringBootApplication
	static class SpringDocTestApp {
		@Bean
		public GroupedOpenApi actuatorApi(OpenApiCustomizer actuatorOpenApiCustomizer, OperationCustomizer actuatorCustomizer) {
			return GroupedOpenApi.builder()
					.group("sample-group")
					.packagesToScan("test.org.springdoc.api.v30.app172")
					.addOpenApiCustomizer(actuatorOpenApiCustomizer)
					.addOperationCustomizer(actuatorCustomizer)
					.pathsToExclude("/health/*")
					.build();
		}
	}

}