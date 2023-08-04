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

package org.springdoc.core.configuration;

import java.util.Optional;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springdoc.core.converters.CollectionModelContentConverter;
import org.springdoc.core.converters.RepresentationModelLinksOASMixin;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springdoc.core.customizers.OpenApiHateoasLinksCustomizer;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.providers.HateoasHalProvider;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springdoc.core.utils.Constants;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.hateoas.HateoasProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.LinkRelationProvider;

/**
 * The type Spring doc hateoas configuration.
 * @author bnasslahsen
 */
@Lazy(false)
@Configuration(proxyBeanMethods = false)
@ConditionalOnExpression("${springdoc.api-docs.enabled:true} and ${springdoc.enable-hateoas:true}")
@ConditionalOnClass(LinkRelationProvider.class)
@ConditionalOnWebApplication
@ConditionalOnBean(SpringDocConfiguration.class)
public class SpringDocHateoasConfiguration {

	/**
	 * Hateoas hal provider hateoas hal provider.
	 *
	 * @param hateoasPropertiesOptional the hateoas properties optional
	 * @param objectMapperProvider the object mapper provider
	 * @return the hateoas hal provider
	 */
	@Bean
	@ConditionalOnMissingBean
	@Lazy(false)
	HateoasHalProvider hateoasHalProvider(Optional<HateoasProperties> hateoasPropertiesOptional, ObjectMapperProvider objectMapperProvider) {
		return new HateoasHalProvider(hateoasPropertiesOptional, objectMapperProvider);
	}

	/**
	 * Collection model content converter collection model content converter.
	 *
	 * @param halProvider the hal provider
	 * @param linkRelationProvider the link relation provider
	 * @return the collection model content converter
	 */
	@Bean
	@ConditionalOnMissingBean
	@Lazy(false)
	CollectionModelContentConverter collectionModelContentConverter(HateoasHalProvider halProvider, LinkRelationProvider linkRelationProvider) {
		return halProvider.isHalEnabled() ? new CollectionModelContentConverter(linkRelationProvider) : null;
	}

	/**
	 * Registers an OpenApiCustomizer and a jackson mixin to ensure the definition of `Links` matches the serialized
	 * output. This is done because the customer serializer converts the data to a map before serializing it.
	 *
	 * @param halProvider the hal provider
	 * @param springDocConfigProperties the spring doc config properties
	 * @param objectMapperProvider the object mapper provider
	 * @return the open api customiser
	 * @see org.springframework.hateoas.mediatype.hal.Jackson2HalModule.HalLinkListSerializer#serialize(Links, JsonGenerator, SerializerProvider) org.springframework.hateoas.mediatype.hal.Jackson2HalModule.HalLinkListSerializer#serialize(Links, JsonGenerator, SerializerProvider)org.springframework.hateoas.mediatype.hal.Jackson2HalModule.HalLinkListSerializer#serialize(Links, JsonGenerator, SerializerProvider)org.springframework.hateoas.mediatype.hal.Jackson2HalModule.HalLinkListSerializer#serialize(Links, JsonGenerator, SerializerProvider)org.springframework.hateoas.mediatype.hal.Jackson2HalModule.HalLinkListSerializer#serialize(Links, JsonGenerator, SerializerProvider)org.springframework.hateoas.mediatype.hal.Jackson2HalModule.HalLinkListSerializer#serialize(Links, JsonGenerator, SerializerProvider)
	 */
	@Bean(Constants.LINKS_SCHEMA_CUSTOMISER)
	@ConditionalOnMissingBean
	@Lazy(false)
	GlobalOpenApiCustomizer linksSchemaCustomizer(HateoasHalProvider halProvider, SpringDocConfigProperties springDocConfigProperties,
			ObjectMapperProvider objectMapperProvider) {
		if (!halProvider.isHalEnabled()) {
			return openApi -> {
			};
		}
		objectMapperProvider.jsonMapper().addMixIn(RepresentationModel.class, RepresentationModelLinksOASMixin.class);
		return new OpenApiHateoasLinksCustomizer(springDocConfigProperties);
	}
}