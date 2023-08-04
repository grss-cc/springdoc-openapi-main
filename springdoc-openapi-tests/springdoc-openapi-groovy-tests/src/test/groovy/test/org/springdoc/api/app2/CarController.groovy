/*
 *
 *  *
 *  *  * Copyright 2019-2020 the original author or authors.
 *  *  *
 *  *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  * you may not use this file except in compliance with the License.
 *  *  * You may obtain a copy of the License at
 *  *  *
 *  *  *      https://www.apache.org/licenses/LICENSE-2.0
 *  *  *
 *  *  * Unless required by applicable law or agreed to in writing, software
 *  *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  * See the License for the specific language governing permissions and
 *  *  * limitations under the License.
 *  *
 *
 */

package test.org.springdoc.api.app2


import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class CarController {

	test.org.springdoc.api.app2.CarService carService

	CarController(test.org.springdoc.api.app2.CarService carService) {
		this.carService = carService
	}

	@GetMapping(path = '/cars')
	List<test.org.springdoc.api.app2.Car> getCars() {
		return carService.getCars()
	}

	@GetMapping(path = 'cars/{carId}')
	test.org.springdoc.api.app2.Car getCar(@PathVariable(value = 'carId') Long carId) {
		return carService.getCar(carId)
	}
}
