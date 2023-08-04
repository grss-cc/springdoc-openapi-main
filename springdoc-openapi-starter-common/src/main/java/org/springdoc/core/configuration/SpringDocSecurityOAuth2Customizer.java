package org.springdoc.core.configuration;

import java.lang.reflect.Field;

import io.swagger.v3.core.util.AnnotationsUtils;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MapSchema;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.configuration.oauth2.SpringDocOAuth2AuthorizationServerMetadata;
import org.springdoc.core.configuration.oauth2.SpringDocOAuth2Token;
import org.springdoc.core.configuration.oauth2.SpringDocOAuth2TokenIntrospection;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springdoc.core.utils.SpringDocAnnotationsUtils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.web.NimbusJwkSetEndpointFilter;
import org.springframework.security.oauth2.server.authorization.web.OAuth2AuthorizationEndpointFilter;
import org.springframework.security.oauth2.server.authorization.web.OAuth2AuthorizationServerMetadataEndpointFilter;
import org.springframework.security.oauth2.server.authorization.web.OAuth2TokenEndpointFilter;
import org.springframework.security.oauth2.server.authorization.web.OAuth2TokenIntrospectionEndpointFilter;
import org.springframework.security.oauth2.server.authorization.web.OAuth2TokenRevocationEndpointFilter;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;

/**
 * The type Spring doc security o auth 2 customizer.
 *
 * @author bnasslahsen
 */
public class SpringDocSecurityOAuth2Customizer implements GlobalOpenApiCustomizer, ApplicationContextAware {

	/**
	 * The constant LOGGER.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(SpringDocSecurityOAuth2Customizer.class);

	/**
	 * The constant OAUTH2_ENDPOINT_TAG.
	 */
	private static final String OAUTH2_ENDPOINT_TAG = "authorization-server-endpoints";

	/**
	 * The Context.
	 */
	private ApplicationContext applicationContext;

	@Override
	public void customise(OpenAPI openAPI) {
		FilterChainProxy filterChainProxy = applicationContext.getBean(AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME, FilterChainProxy.class);
		for (SecurityFilterChain filterChain : filterChainProxy.getFilterChains()) {
			getNimbusJwkSetEndpoint(openAPI, filterChain);
			getOAuth2AuthorizationServerMetadataEndpoint(openAPI, filterChain);
			getOAuth2TokenEndpoint(openAPI, filterChain);
			getOAuth2AuthorizationEndpoint(openAPI, filterChain);
			getOAuth2TokenIntrospectionEndpointFilter(openAPI, filterChain);
			getOAuth2TokenRevocationEndpointFilter(openAPI, filterChain);
		}
	}

	/**
	 * Gets o auth 2 token revocation endpoint filter.
	 *
	 * @param openAPI the open api
	 * @param securityFilterChain the security filter chain
	 */
	private void getOAuth2TokenRevocationEndpointFilter(OpenAPI openAPI, SecurityFilterChain securityFilterChain) {
		Object oAuth2EndpointFilter =
				new SpringDocSecurityOAuth2EndpointUtils(OAuth2TokenRevocationEndpointFilter.class).findEndpoint(securityFilterChain);
		if (oAuth2EndpointFilter != null) {
			ApiResponses apiResponses = new ApiResponses();
			apiResponses.addApiResponse(String.valueOf(HttpStatus.OK.value()), new ApiResponse().description(HttpStatus.OK.getReasonPhrase()));
			buildApiResponsesOnInternalServerError(apiResponses);
			buildApiResponsesOnBadRequest(apiResponses, openAPI);

			Operation operation = buildOperation(apiResponses);
			Schema<?> schema = new ObjectSchema()
					.addProperty("token", new StringSchema())
					.addProperty(OAuth2ParameterNames.TOKEN_TYPE_HINT, new StringSchema());

			String mediaType = org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
			RequestBody requestBody = new RequestBody().content(new Content().addMediaType(mediaType, new MediaType().schema(schema)));
			operation.setRequestBody(requestBody);
			buildPath(oAuth2EndpointFilter, "tokenRevocationEndpointMatcher", openAPI, operation, HttpMethod.POST);
		}
	}

	/**
	 * Gets o auth 2 token introspection endpoint filter.
	 *
	 * @param openAPI the open api
	 * @param securityFilterChain the security filter chain
	 */
	private void getOAuth2TokenIntrospectionEndpointFilter(OpenAPI openAPI, SecurityFilterChain securityFilterChain) {
		Object oAuth2EndpointFilter =
				new SpringDocSecurityOAuth2EndpointUtils(OAuth2TokenIntrospectionEndpointFilter.class).findEndpoint(securityFilterChain);
		if (oAuth2EndpointFilter != null) {
			ApiResponses apiResponses = new ApiResponses();
			buildApiResponsesOnSuccess(apiResponses, AnnotationsUtils.resolveSchemaFromType(SpringDocOAuth2TokenIntrospection.class, openAPI.getComponents(), null));
			buildApiResponsesOnInternalServerError(apiResponses);
			buildApiResponsesOnBadRequest(apiResponses, openAPI);

			Operation operation = buildOperation(apiResponses);
			Schema<?> requestSchema = new ObjectSchema()
					.addProperty("token", new StringSchema())
					.addProperty(OAuth2ParameterNames.TOKEN_TYPE_HINT, new StringSchema())
					.addProperty("additionalParameters", new ObjectSchema().additionalProperties(new StringSchema()));

			String mediaType = org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
			RequestBody requestBody = new RequestBody().content(new Content().addMediaType(mediaType, new MediaType().schema(requestSchema)));
			operation.setRequestBody(requestBody);
			buildPath(oAuth2EndpointFilter, "tokenIntrospectionEndpointMatcher", openAPI, operation, HttpMethod.POST);
		}
	}

	/**
	 * Gets o auth 2 authorization server metadata endpoint.
	 *
	 * @param openAPI the open api
	 * @param securityFilterChain the security filter chain
	 */
	private void getOAuth2AuthorizationServerMetadataEndpoint(OpenAPI openAPI, SecurityFilterChain securityFilterChain) {
		Object oAuth2EndpointFilter =
				new SpringDocSecurityOAuth2EndpointUtils(OAuth2AuthorizationServerMetadataEndpointFilter.class).findEndpoint(securityFilterChain);
		if (oAuth2EndpointFilter != null) {
			ApiResponses apiResponses = new ApiResponses();
			buildApiResponsesOnSuccess(apiResponses, AnnotationsUtils.resolveSchemaFromType(SpringDocOAuth2AuthorizationServerMetadata.class, openAPI.getComponents(), null));
			buildApiResponsesOnInternalServerError(apiResponses);
			Operation operation = buildOperation(apiResponses);
			buildPath(oAuth2EndpointFilter, "requestMatcher", openAPI, operation, HttpMethod.GET);
		}
	}

	/**
	 * Gets nimbus jwk set endpoint.
	 *
	 * @param openAPI the open api
	 * @param securityFilterChain the security filter chain
	 */
	private void getNimbusJwkSetEndpoint(OpenAPI openAPI, SecurityFilterChain securityFilterChain) {
		Object oAuth2EndpointFilter =
				new SpringDocSecurityOAuth2EndpointUtils(NimbusJwkSetEndpointFilter.class).findEndpoint(securityFilterChain);
		if (oAuth2EndpointFilter != null) {
			ApiResponses apiResponses = new ApiResponses();
			Schema<?> schema = new MapSchema();
			schema.addProperty("keys", new ArraySchema().items(new ObjectSchema().additionalProperties(true)));

			ApiResponse response = new ApiResponse().description(HttpStatus.OK.getReasonPhrase()).content(new Content().addMediaType(
					APPLICATION_JSON_VALUE,
					new MediaType().schema(schema)));
			apiResponses.addApiResponse(String.valueOf(HttpStatus.OK.value()), response);
			buildApiResponsesOnInternalServerError(apiResponses);
			buildApiResponsesOnBadRequest(apiResponses, openAPI);

			Operation operation = buildOperation(apiResponses);
			operation.responses(apiResponses);
			buildPath(oAuth2EndpointFilter, "requestMatcher", openAPI, operation, HttpMethod.GET);
		}
	}

	/**
	 * Gets o auth 2 token endpoint.
	 *
	 * @param openAPI the open api
	 * @param securityFilterChain the security filter chain
	 */
	private void getOAuth2TokenEndpoint(OpenAPI openAPI, SecurityFilterChain securityFilterChain) {
		Object oAuth2EndpointFilter =
				new SpringDocSecurityOAuth2EndpointUtils(OAuth2TokenEndpointFilter.class).findEndpoint(securityFilterChain);

		if (oAuth2EndpointFilter != null) {
			ApiResponses apiResponses = new ApiResponses();
			buildApiResponsesOnSuccess(apiResponses, AnnotationsUtils.resolveSchemaFromType(SpringDocOAuth2Token.class, openAPI.getComponents(), null));
			buildApiResponsesOnInternalServerError(apiResponses);
			buildApiResponsesOnBadRequest(apiResponses, openAPI);
			buildOAuth2Error(openAPI, apiResponses, HttpStatus.UNAUTHORIZED);
			Operation operation = buildOperation(apiResponses);

			Schema<?> requestSchema = new ObjectSchema()
					.addProperty(OAuth2ParameterNames.GRANT_TYPE,
							new StringSchema()
									.addEnumItem(AuthorizationGrantType.AUTHORIZATION_CODE.getValue())
									.addEnumItem(AuthorizationGrantType.REFRESH_TOKEN.getValue())
									.addEnumItem(AuthorizationGrantType.CLIENT_CREDENTIALS.getValue()))
					.addProperty(OAuth2ParameterNames.CODE, new StringSchema())
					.addProperty(OAuth2ParameterNames.REDIRECT_URI, new StringSchema())
					.addProperty(OAuth2ParameterNames.REFRESH_TOKEN, new StringSchema())
					.addProperty(OAuth2ParameterNames.SCOPE, new StringSchema())
					.addProperty(OAuth2ParameterNames.CLIENT_ID, new StringSchema())
					.addProperty(OAuth2ParameterNames.CLIENT_SECRET, new StringSchema())
					.addProperty(OAuth2ParameterNames.CLIENT_ASSERTION_TYPE, new StringSchema())
					.addProperty(OAuth2ParameterNames.CLIENT_ASSERTION, new StringSchema())
					.addProperty("additionalParameters", new ObjectSchema().additionalProperties(new StringSchema()));

			String mediaType = org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
			RequestBody requestBody = new RequestBody().content(new Content().addMediaType(mediaType, new MediaType().schema(requestSchema)));
			operation.setRequestBody(requestBody);
			operation.addParametersItem(new HeaderParameter().name("Authorization"));

			buildPath(oAuth2EndpointFilter, "tokenEndpointMatcher", openAPI, operation, HttpMethod.POST);
		}
	}

	/**
	 * Gets o auth 2 authorization endpoint.
	 *
	 * @param openAPI the open api
	 * @param securityFilterChain the security filter chain
	 */
	private void getOAuth2AuthorizationEndpoint(OpenAPI openAPI, SecurityFilterChain securityFilterChain) {
		Object oAuth2EndpointFilter =
				new SpringDocSecurityOAuth2EndpointUtils(OAuth2AuthorizationEndpointFilter.class).findEndpoint(securityFilterChain);
		if (oAuth2EndpointFilter != null) {
			ApiResponses apiResponses = new ApiResponses();

			ApiResponse response = new ApiResponse().description(HttpStatus.OK.getReasonPhrase()).content(new Content().addMediaType(
					TEXT_HTML_VALUE,
					new MediaType()));
			apiResponses.addApiResponse(String.valueOf(HttpStatus.OK.value()), response);
			buildApiResponsesOnInternalServerError(apiResponses);
			buildApiResponsesOnBadRequest(apiResponses, openAPI);
			apiResponses.addApiResponse(String.valueOf(HttpStatus.MOVED_TEMPORARILY.value()),
					new ApiResponse().description(HttpStatus.MOVED_TEMPORARILY.getReasonPhrase())
							.addHeaderObject("Location", new Header().schema(new StringSchema())));
			Operation operation = buildOperation(apiResponses);
			Schema<?> schema = new ObjectSchema().additionalProperties(new StringSchema());
			operation.addParametersItem(new Parameter().name("parameters").in(ParameterIn.QUERY.toString()).schema(schema));
			buildPath(oAuth2EndpointFilter, "authorizationEndpointMatcher", openAPI, operation, HttpMethod.POST);
		}
	}

	/**
	 * Build operation operation.
	 *
	 * @param apiResponses the api responses
	 * @return the operation
	 */
	private Operation buildOperation(ApiResponses apiResponses) {
		Operation operation = new Operation();
		operation.addTagsItem(OAUTH2_ENDPOINT_TAG);
		operation.responses(apiResponses);
		return operation;
	}

	/**
	 * Build api responses api responses on success.
	 *
	 * @param apiResponses the api responses
	 * @param schema the schema
	 * @return the api responses
	 */
	private ApiResponses buildApiResponsesOnSuccess(ApiResponses apiResponses, Schema schema) {
		ApiResponse response = new ApiResponse().description(HttpStatus.OK.getReasonPhrase()).content(new Content().addMediaType(
				APPLICATION_JSON_VALUE,
				new MediaType().schema(schema)));
		apiResponses.addApiResponse(String.valueOf(HttpStatus.OK.value()), response);
		return apiResponses;
	}

	/**
	 * Build api responses api responses on internal server error.
	 *
	 * @param apiResponses the api responses
	 * @return the api responses
	 */
	private ApiResponses buildApiResponsesOnInternalServerError(ApiResponses apiResponses) {
		apiResponses.addApiResponse(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), new ApiResponse().description(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()));
		return apiResponses;
	}

	/**
	 * Build api responses on bad request.
	 *
	 * @param apiResponses the api responses
	 * @param openAPI the open api
	 * @return the api responses
	 */
	private ApiResponses buildApiResponsesOnBadRequest(ApiResponses apiResponses, OpenAPI openAPI) {
		buildOAuth2Error(openAPI, apiResponses, HttpStatus.BAD_REQUEST);
		return apiResponses;
	}

	/**
	 * Build o auth 2 error.
	 *
	 * @param openAPI the open api
	 * @param apiResponses the api responses
	 * @param httpStatus the http status
	 */
	private static void buildOAuth2Error(OpenAPI openAPI, ApiResponses apiResponses, HttpStatus httpStatus) {
		Schema oAuth2ErrorSchema = AnnotationsUtils.resolveSchemaFromType(OAuth2Error.class, openAPI.getComponents(), null);
		apiResponses.addApiResponse(String.valueOf(httpStatus.value()), new ApiResponse().description(httpStatus.getReasonPhrase()).content(new Content().addMediaType(
				APPLICATION_JSON_VALUE,
				new MediaType().schema(oAuth2ErrorSchema))));
	}

	/**
	 * Build path.
	 *
	 * @param oAuth2EndpointFilter the o auth 2 endpoint filter
	 * @param authorizationEndpointMatcher the authorization endpoint matcher
	 * @param openAPI the open api
	 * @param operation the operation
	 * @param requestMethod the request method
	 */
	private void buildPath(Object oAuth2EndpointFilter, String authorizationEndpointMatcher, OpenAPI openAPI, Operation operation, HttpMethod requestMethod) {
		try {
			Field tokenEndpointMatcherField = FieldUtils.getDeclaredField(oAuth2EndpointFilter.getClass(), authorizationEndpointMatcher, true);
			RequestMatcher endpointMatcher = (RequestMatcher) tokenEndpointMatcherField.get(oAuth2EndpointFilter);
			String path = null;
			if (endpointMatcher instanceof AntPathRequestMatcher)
				path = ((AntPathRequestMatcher) endpointMatcher).getPattern();
			else if (endpointMatcher instanceof OrRequestMatcher) {
				OrRequestMatcher endpointMatchers = (OrRequestMatcher) endpointMatcher;
				Field requestMatchersField = FieldUtils.getDeclaredField(OrRequestMatcher.class, "requestMatchers", true);
				Iterable<RequestMatcher> requestMatchers = (Iterable<RequestMatcher>) requestMatchersField.get(endpointMatchers);
				for (RequestMatcher requestMatcher : requestMatchers) {
					if (requestMatcher instanceof OrRequestMatcher) {
						OrRequestMatcher orRequestMatcher = (OrRequestMatcher) requestMatcher;
						requestMatchersField = FieldUtils.getDeclaredField(OrRequestMatcher.class, "requestMatchers", true);
						requestMatchers = (Iterable<RequestMatcher>) requestMatchersField.get(orRequestMatcher);
						for (RequestMatcher matcher : requestMatchers) {
							if (matcher instanceof AntPathRequestMatcher)
								path = ((AntPathRequestMatcher) matcher).getPattern();
						}
					}
				}
			}

			PathItem pathItem = new PathItem();
			if (HttpMethod.POST.equals(requestMethod)) {
				pathItem.post(operation);
			}
			else if (HttpMethod.GET.equals(requestMethod)) {
				pathItem.get(operation);
			}
			openAPI.getPaths().addPathItem(path, pathItem);
		}
		catch (IllegalAccessException | ClassCastException ignored) {
			LOGGER.trace(ignored.getMessage());
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
