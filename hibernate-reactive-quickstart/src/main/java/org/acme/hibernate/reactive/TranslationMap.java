package org.acme.hibernate.reactive;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Embeddable
public class TranslationMap {


	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumns({
		@JoinColumn(name = "class_pk", referencedColumnName = "id", updatable = false, insertable = false),
		@JoinColumn(name = "class_name", referencedColumnName = "class_name", updatable = false, insertable = false)
	})
	@JsonIgnore
	private Set<Translation> translations = new HashSet<>();

	public void addTranslation(String language, String className, Integer classPk, String key, String value) {
		TranslationKey translationKey = new TranslationKey(language, className, classPk, key);
		Translation translation = new Translation();
		translation.setPk(translationKey);
		translation.setValue(value);
		translations.add(translation);
	}

	@Transient
	@JsonProperty
	public Map<TranslationKey, String> getTranslationMap() {
		return this.translations
			.stream()
			.collect(Collectors.toMap(Translation::getPk, Translation::getValue));
	}

	@JsonIgnore
	public void setTranslationMap(Map<TranslationKey, String> translationMap) {
	}


}
