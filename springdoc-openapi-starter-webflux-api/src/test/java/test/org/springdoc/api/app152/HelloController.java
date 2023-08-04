/*
 *
 *  * Copyright 2019-2021 the original author or authors.
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

package test.org.springdoc.api.app152;

import org.reactivestreams.Publisher;
import org.springdoc.core.models.GroupedOpenApi;
import reactor.core.publisher.Mono;

import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

	@GetMapping("/persons")
	public Publisher<String> persons() {
		return Mono.just("OK");
	}

	@Bean
	public GroupedOpenApi userOpenApi() {
		return GroupedOpenApi.builder()
				.group("users")
				.packagesToScan("test.org.springdoc.api.app152")
				.build();
	}
}