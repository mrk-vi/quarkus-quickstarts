package org.acme.hibernate.reactive;

public class TranslationKeyDTO {
	private String language;
	private String key;

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
}
