package org.acme.hibernate.reactive;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.Where;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "known_fruits")
@NamedQuery(
    name = "Fruits.findAll",
    query = "SELECT DISTINCT f " +
        "FROM Fruit f " +
        // "LEFT JOIN FETCH f.translations t " +
        "ORDER BY f.name")
public class Fruit {

    public static final String CLASS_NAME = "org.acme.hibernate.reactive.Fruit";

    @Id
    @SequenceGenerator(name = "fruitsSequence", sequenceName = "known_fruits_id_seq", allocationSize = 1, initialValue = 10)
    @GeneratedValue(generator = "fruitsSequence")
    private Integer id;

    @Column(length = 40, unique = true)
    private String name;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "class_pk", updatable = false, insertable = false)
    @Where(clause = "class_name = '" + CLASS_NAME + "'")
    @JsonIgnore
    private Set<Translation> translations = new HashSet<>();

    public Fruit() {
    }

    public Fruit(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setName(String name, String language) {
        addTranslation(language, "name", name);
    }

    public Set<Translation> getTranslations() {
        return translations;
    }

    public void setTranslations(Set<Translation> translations) {
        this.translations = translations;
    }

    public void addTranslation(String language, String key, String value) {
        TranslationKey translationKey = new TranslationKey(language, CLASS_NAME, getId(), key);
        Translation translation = new Translation();
        translation.setPk(translationKey);
        translation.setValue(value);
        translations.add(translation);
    }

    public String getName(String language) {
        return translations
            .stream()
            .filter(e -> e.getPk().equals(new TranslationKey(language, CLASS_NAME, getId(), "name")))
            .findFirst()
            .map(Translation::getValue)
            .orElse(null);
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

    @Override
    public String toString() {
        return "Fruit{" + id + "," + name + '}';
    }
}
