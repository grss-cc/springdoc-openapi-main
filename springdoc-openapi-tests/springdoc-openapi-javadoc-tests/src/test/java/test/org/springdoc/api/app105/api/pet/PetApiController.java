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

package test.org.springdoc.api.app105.api.pet;

import java.util.Optional;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The type Pet api controller.
 */
@jakarta.annotation.Generated(value = "org.springdoc.demo.app2.codegen.languages.SpringCodegen", date = "2019-07-11T00:09:29.839+02:00[Europe/Paris]")

@RestController
@RequestMapping("${openapi.openAPIPetstore.base-path:/}")
public class PetApiController implements PetApi {

	/**
	 * The Delegate.
	 */
	private final PetApiDelegate delegate;

	/**
	 * Instantiates a new Pet api controller.
	 *
	 * @param delegate the delegate
	 */
	public PetApiController(@org.springframework.beans.factory.annotation.Autowired(required = false) PetApiDelegate delegate) {
		this.delegate = Optional.ofNullable(delegate).orElse(new PetApiDelegate() {
		});
	}

	/**
	 * Gets delegate.
	 *
	 * @return the delegate
	 */
	@Override
	public PetApiDelegate getDelegate() {
		return delegate;
	}

}
