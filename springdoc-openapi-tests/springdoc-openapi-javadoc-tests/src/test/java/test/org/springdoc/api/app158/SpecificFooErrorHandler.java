package test.org.springdoc.api.app158;

import org.springframework.web.bind.annotation.ControllerAdvice;

/**
 * The type Specific foo error handler.
 */
@ControllerAdvice(assignableTypes = HelloController.class)
public class SpecificFooErrorHandler extends CommonFooErrorHandler {
}
