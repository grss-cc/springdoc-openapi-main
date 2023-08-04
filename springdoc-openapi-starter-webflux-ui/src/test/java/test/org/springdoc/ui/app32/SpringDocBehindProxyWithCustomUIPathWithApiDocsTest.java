/*
 *
 *  * Copyright 2019-2020 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      https://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package test.org.springdoc.ui.app32;

import org.junit.jupiter.api.Test;
import test.org.springdoc.ui.AbstractSpringDocTest;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@TestPropertySource(properties = {
		"server.forward-headers-strategy=framework",
		"springdoc.swagger-ui.path=/foo/documentation/swagger.html",
		"springdoc.api-docs.path=/bar/openapi/v3"
})
@Import(SpringDocConfig.class)
public class SpringDocBehindProxyWithCustomUIPathWithApiDocsTest extends AbstractSpringDocTest {

	private static final String X_FORWARD_PREFIX = "/path/prefix";

	@Test
	public void shouldRedirectSwaggerUIFromCustomPath() {
		webTestClient
				.get().uri("/foo/documentation/swagger.html")
				.header("X-Forwarded-Prefix", X_FORWARD_PREFIX)
				.exchange()
				.expectStatus().isFound()
				.expectHeader().location("/path/prefix/foo/documentation/webjars/swagger-ui/index.html");
	}

	@Test
	public void shouldReturnCorrectInitializerJS() {
		webTestClient
				.get().uri("/foo/documentation/webjars/swagger-ui/swagger-initializer.js")
				.header("X-Forwarded-Prefix", X_FORWARD_PREFIX)
				.exchange()
				.expectStatus().isOk()
				.expectBody(String.class)
				.consumeWith(response ->
						assertThat(response.getResponseBody())
								.contains("\"configUrl\" : \"/path/prefix/bar/openapi/v3/swagger-config\",")
				);
	}

	@Test
	public void shouldCalculateUrlsBehindProxy() {
		webTestClient
				.get().uri("/bar/openapi/v3/swagger-config")
				.header("X-Forwarded-Prefix", X_FORWARD_PREFIX)
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.url")
				.isEqualTo("/path/prefix/bar/openapi/v3")
				.jsonPath("$.configUrl")
				.isEqualTo("/path/prefix/bar/openapi/v3/swagger-config");
	}

	@SpringBootApplication
	static class SpringDocTestApp {}
}
