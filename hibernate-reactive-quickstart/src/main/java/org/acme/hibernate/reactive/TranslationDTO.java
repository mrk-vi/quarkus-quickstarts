package org.acme.hibernate.reactive;

public class TranslationDTO {
	private String language;
	private String key;
	private String value;

	public TranslationDTO() {
	}

	public TranslationDTO(String language, String key, String value) {
		this.language = language;
		this.key = key;
		this.value = value;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
