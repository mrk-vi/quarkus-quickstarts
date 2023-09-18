package org.acme.hibernate.reactive;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Map;

public interface LocalizedEntity<T extends BaseEntity> {

	@JsonIgnore
	Class<T> getWrappeeClass();

	Map<TranslationKey, String> getTranslationMap();
}
