package org.acme.hibernate.reactive;

import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TranslationKey implements Serializable {

	private String language;
	@Column(name = "class_name")
	private String className;
	@Column(name = "class_pk")
	private Integer classPK;
	private String key;

	public TranslationKey() {
	}

	public TranslationKey(String language, String className, Integer classPK, String key) {
		this.language = language;
		this.className = className;
		this.classPK = classPK;
		this.key = key;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public Integer getClassPK() {
		return classPK;
	}

	public void setClassPK(Integer classPK) {
		this.classPK = classPK;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public String toString() {
		return key + "." + language;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || Hibernate.getClass(this) !=
			Hibernate.getClass(o)) {
			return false;
		}
		TranslationKey that = (TranslationKey) o;
		return Objects.equals(language, that.language)
			&& Objects.equals(className, that.className)
			&& Objects.equals(classPK, that.classPK)
			&& Objects.equals(key, that.key);
	}

	@Override
	public int hashCode() {
		return Objects.hash(language, className, classPK, key);
	}

}
