package org.acme.hibernate.reactive;

import java.util.Map;

public class LocalizedFruit extends Fruit implements LocalizedEntity<Fruit> {

	private final Fruit wrappee;
	private final Map<TranslationKey, String> translationMap;

	public LocalizedFruit(Fruit wrappee, Map<TranslationKey, String> translationMap) {
		this.wrappee = wrappee;
		this.translationMap = translationMap;
	}

	@Override
	public Integer getId() {
		return wrappee.getId();
	}

	@Override
	public void setId(Integer id) {
		wrappee.setId(id);
	}

	@Override
	public String getName() {
		return wrappee.getName();
	}

	@Override
	public void setName(String name) {
		wrappee.setName(name);
	}

	@Override
	public Class<Fruit> getWrappeeClass() {
		return Fruit.class;
	}

	public Map<TranslationKey, String> getTranslationMap() {
		return translationMap;
	}

}
