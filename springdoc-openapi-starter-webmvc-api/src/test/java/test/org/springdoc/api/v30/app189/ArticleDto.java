package test.org.springdoc.api.v30.app189;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public class ArticleDto {
	@Schema(description = "title")
	private String title;

	@Schema(description = "content")
	private String content;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
