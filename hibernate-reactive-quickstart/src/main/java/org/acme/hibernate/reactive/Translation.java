package org.acme.hibernate.reactive;

import org.hibernate.Hibernate;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.Objects;

@Entity
public class Translation {

	@EmbeddedId
	private TranslationKey pk;
	private String value;

	public TranslationKey getPk() {
		return pk;
	}

	public void setPk(TranslationKey pk) {
		this.pk = pk;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Transient
	public String getLanguage() {
		return pk.getLanguage();
	}

	@Transient
	public String getClassName() {
		return pk.getClassName();
	}

	@Transient
	public void setClassName(String className) {
		pk.setClassName(className);
	}

	@Transient
	public Integer getClassPK() {
		return pk.getClassPK();
	}

	@Transient
	public void setClassPK(Integer classPK) {
		pk.setClassPK(classPK);
	}

	@Transient
	public String getKey() {
		return pk.getKey();
	}

	@Transient
	public void setKey(String key) {
		pk.setKey(key);
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
		Translation that = (Translation) o;
		return Objects.equals(pk, that.pk);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(pk);
	}
}
