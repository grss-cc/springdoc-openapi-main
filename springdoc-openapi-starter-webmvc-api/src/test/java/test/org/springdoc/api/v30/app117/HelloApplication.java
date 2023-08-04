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

package test.org.springdoc.api.v30.app117;

import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.servlet.FilterChain;
import jakarta.servlet.GenericFilter;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.web.servlet.function.RouterFunctions.route;
import static org.springframework.web.servlet.function.ServerResponse.ok;

@Configuration
public class HelloApplication {

	@Bean
	@RouterOperations({ @RouterOperation(path = "/people", method = RequestMethod.GET, beanClass = PersonService.class, beanMethod = "all"),
			@RouterOperation(path = "/people/{id}", beanClass = PersonService.class, beanMethod = "byId"),
			@RouterOperation(path = "/people", method = RequestMethod.POST, beanClass = PersonService.class, beanMethod = "save") })
	RouterFunction<ServerResponse> routes(PersonHandler ph) {
		String root = "";
		return route()
				.GET(root + "/people", ph::handleGetAllPeople)
				.GET(root + "/people/{id}", ph::handleGetPersonById)
				.POST(root + "/people", ph::handlePostPerson)
				.filter((serverRequest, handlerFunction) -> {
					return handlerFunction.handle(serverRequest);
				})
				.build();
	}
}

@Component
class SimpleFilter extends GenericFilter {

	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain filterChain) throws IOException, ServletException {
		filterChain.doFilter(req, res);
	}
}

@Component
class PersonHandler {

	private final PersonService personService;

	PersonHandler(PersonService personService) {
		this.personService = personService;
	}

	ServerResponse handleGetAllPeople(ServerRequest serverRequest) {
		return ok().body(personService.all());
	}

	ServerResponse handlePostPerson(ServerRequest r) throws ServletException, IOException {
		Person result = personService.save(new Person(null, r.body(Person.class).getName()));
		URI uri = URI.create("/people/" + result.getId());
		return ServerResponse.created(uri).body(result);
	}

	ServerResponse handleGetPersonById(ServerRequest r) {
		return ok().body(personService.byId(Long.parseLong(r.pathVariable("id"))));
	}
}

@RestController
class GreetingsRestController {

	@GetMapping("/greet/{name}")
	String greet(@PathVariable String name) {
		return "hello " + name + "!";
	}
}

@Service
class PersonService {

	private final AtomicLong counter = new AtomicLong();

	private final Set<Person> people = Stream.of(
					new Person(counter.incrementAndGet(), "Jane"),
					new Person(counter.incrementAndGet(), "Josh"),
					new Person(counter.incrementAndGet(), "Gordon"))
			.collect(Collectors.toCollection(HashSet::new));


	Person save(Person p) {
		Person person = new Person(counter.incrementAndGet(), p.getName());
		this.people.add(person);
		return person;
	}

	Set<Person> all() {
		return this.people;
	}

	Person byId(@Parameter(in = ParameterIn.PATH) Long id) {
		return this.people.stream()
				.filter(p -> p.getId().equals(id))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("no " + Person.class.getName() + " with that ID found!"));
	}

}

class Person {

	private Long id;

	private String name;

	public Person(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
