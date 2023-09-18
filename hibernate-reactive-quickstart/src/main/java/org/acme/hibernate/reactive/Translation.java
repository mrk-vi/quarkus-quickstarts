package org.acme.hibernate.reactive;

import org.hibernate.Hibernate;
import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.NaturalId;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Objects;

@Entity
@Table(
	indexes = {
		@Index(
			name = "idx_translation_pk",
			columnList = "language, class_name, class_pk, key",
			unique = true
		),
		@Index(
			name = "idx_translation_entities",
			columnList = "class_name, class_pk"
		)
	}
)
public class Translation implements BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Embedded
	@NaturalId
	private TranslationKey pk;
	@Nationalized
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
		return Objects.hash(id, pk);
	}

	@Override
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
}
