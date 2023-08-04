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

package test.org.springdoc.api.app2.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * The type Tag.
 */
public class Tag {

	/**
	 * The Id.
	 */
	@Schema(description = "")
	private Long id = null;

	/**
	 * The Name.
	 */
	@Schema(description = "")
	private String name = null;

	/**
	 * Convert the given object to string with each line indented by 4 spaces
	 * (except the first line).
	 * @param o the o
	 * @return the string
	 */
	private static String toIndentedString(Object o) {
		if (o == null) {
			return "null";
		}
		return o.toString().replace("\n", "\n    ");
	}

	/**
	 * Get id
	 *
	 * @return id id
	 */
	@JsonProperty("id")
	public Long getId() {
		return id;
	}

	/**
	 * Sets id.
	 *
	 * @param id the id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Id tag.
	 *
	 * @param id the id
	 * @return the tag
	 */
	public Tag id(Long id) {
		this.id = id;
		return this;
	}

	/**
	 * Get name
	 *
	 * @return name name
	 */
	@JsonProperty("name")
	public String getName() {
		return name;
	}

	/**
	 * Sets name.
	 *
	 * @param name the name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Name tag.
	 *
	 * @param name the name
	 * @return the tag
	 */
	public Tag name(String name) {
		this.name = name;
		return this;
	}

	/**
	 * To string string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class Tag {\n");

		sb.append("    id: ").append(toIndentedString(id)).append("\n");
		sb.append("    name: ").append(toIndentedString(name)).append("\n");
		sb.append("}");
		return sb.toString();
	}
}
