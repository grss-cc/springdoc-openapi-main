package test.org.springdoc.api.app18;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping(value = { HelloController.VERSION + "/helloWorld", "latest/helloWorld" }, produces = MediaType.APPLICATION_JSON_VALUE)
public class HelloController {

	public static final String VERSION = "v1";

	@Operation(summary = "Example endpoint")
	@GetMapping("/helloWorld")
	public HelloWorldModel helloWorld(@Valid @ParameterObject HelloWorldModel helloWorldModel, HttpServletRequest request) {
		return new HelloWorldModel();
	}

}
