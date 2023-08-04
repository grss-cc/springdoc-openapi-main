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

package test.org.springdoc.api.app10;

import java.util.Locale;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RestController;

/**
 * The type Hello controller.
 */
@RestController
public class HelloController {

	/**
	 * Test.
	 *
	 * @param header the header 
	 * @param request the request 
	 * @param response the response 
	 * @param locale the locale 
	 * @param hello the hello
	 */
	@GetMapping("/test")
	public void test(HttpSession header, HttpServletRequest request, HttpServletResponse response, Locale locale,
			String hello) {
	}

	/**
	 * Test request attribute.
	 *
	 * @param sample the sample 
	 * @param s the s
	 */
	@GetMapping("/testreq")
	public void testRequestAttribute(@RequestAttribute String sample, String s) {

	}
}
