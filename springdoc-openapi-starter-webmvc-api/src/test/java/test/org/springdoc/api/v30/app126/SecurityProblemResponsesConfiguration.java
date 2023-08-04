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

package test.org.springdoc.api.v30.app126;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Map;

import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;

import org.springframework.beans.factory.parsing.Problem;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;

/**
 * Configuration class defining standard OpenAPI Specification for operations
 */
@Configuration
public class SecurityProblemResponsesConfiguration {

	private static final String HTTP_401_NO_TOKEN = "http401NoToken";

	public static final String UNAUTHORIZED_401_NO_TOKEN_RESPONSE_REF = "#/components/responses/" + HTTP_401_NO_TOKEN;

	private static final String HTTP_401_BAD_TOKEN = "http401BadToken";

	public static final String UNAUTHORIZED_401_BAD_TOKEN_RESPONSE_REF = "#/components/responses/" + HTTP_401_BAD_TOKEN;

	private static final String HTTP_403 = "http403";

	public static final String FORBIDDEN_403_RESPONSE_REF = "#/components/responses/" + HTTP_403;

	@Bean
	public Map.Entry<String, ApiResponse> http401NoTokenResponse() throws IOException {
		return simpleResponse(HTTP_401_NO_TOKEN, "Invalid authentication.");
	}

	@Bean
	public Map.Entry<String, ApiResponse> http401BadTokenResponse() throws IOException {
		return simpleResponse(HTTP_401_BAD_TOKEN, "Invalid authentication.");
	}

	@Bean
	public Map.Entry<String, ApiResponse> http403Example() throws IOException {
		return simpleResponse(HTTP_403, "Missing authorities.");
	}

	private Map.Entry<String, ApiResponse> simpleResponse(String code, String description) throws IOException {
		ApiResponse response = new ApiResponse().description(description).content(new Content().addMediaType(
				APPLICATION_PROBLEM_JSON_VALUE,
				new MediaType()
						.schema(new Schema<Problem>().$ref("#/components/schemas/Problem"))));
		return new AbstractMap.SimpleEntry<>(code, response);
	}

}
