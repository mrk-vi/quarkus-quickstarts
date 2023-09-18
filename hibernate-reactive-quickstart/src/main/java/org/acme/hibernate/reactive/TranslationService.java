package org.acme.hibernate.reactive;

import io.smallrye.mutiny.Uni;
import org.hibernate.reactive.common.Identifier;
import org.hibernate.reactive.mutiny.Mutiny;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class TranslationService {

	@Inject
	Mutiny.SessionFactory sf;

	public  <T extends BaseEntity, S extends LocalizedEntity<T>>  Uni<S> getLocalizedEntity(
		Class<T> entityClass,
		T entity,
		BiFunction<? super T, Map<TranslationKey, String>, ? extends S> localizedEntityFactory) {

		return this
			.getTranslationMap(
				entityClass,
				entity.getId()
			)
			.map(translationMap -> localizedEntityFactory
				.apply(entity, translationMap)
			);
	}

	public  <T extends BaseEntity, S extends LocalizedEntity<T>>  Uni<List<S>> getLocalizedEntities(
		Class<T> entityClass,
		List<T> entities,
		BiFunction<? super T, Map<TranslationKey, String>, ? extends S> localizedEntityFactory) {

		return this
			.getTranslationMaps(
				entityClass,
				entities
					.stream()
					.map(BaseEntity::getId).collect(Collectors.toList())
			)
			.map(translationMaps -> entities
				.stream()
				.map(entity -> localizedEntityFactory
					.apply(entity, translationMaps.get(entity.getId())))
				.collect(Collectors.toList())
			);
	}

	public <T> Uni<Map<TranslationKey, String>> getTranslationMap(Class<T> entityClass, Integer id) {

		String className = entityClass.getName();

		return sf.withStatelessTransaction(session -> session
			.createQuery(
				"select t " +
				"from Translation t " +
				"where t.pk.className = :className " +
				"and t.pk.classPK = :classPk",
				Translation.class)
			.setParameter("className", className)
			.setParameter("classPk", id)
			.getResultList()
			.map(translations -> translations
				.stream()
				.collect(Collectors
					.toMap(
						Translation::getPk,
						Translation::getValue)
				)
			)
		);
	}

	public <T> Uni<Map<Integer, Map<TranslationKey, String>>> getTranslationMaps(Class<T> entityClass, List<Integer> ids) {

		String className = entityClass.getName();

		return sf.withStatelessTransaction(session -> session
			.createQuery(
				"select t " +
					"from Translation t " +
					"where t.pk.className = :className " +
					"and t.pk.classPK in (:classPks)",
				Translation.class)
			.setParameter("className", className)
			.setParameter("classPks", ids)
			.getResultList()
			.map(translations -> translations
				.stream()
				.collect(Collectors
					.toMap(
						Translation::getClassPK,
						t -> Map.of(t.getPk(), t.getValue()),
						(m1, m2) -> Map.ofEntries(Stream
							.concat(m1.entrySet().stream(), m2.entrySet().stream())
							.<Map.Entry<TranslationKey, String>>toArray(Map.Entry[]::new)
						)
					)
				)
			)
		);
	}

	public <T> Uni<Void> addTranslation(
		Class<T> entityClass, Integer id, String language, String key, String value) {

		TranslationKey pkValue =
			new TranslationKey(language, entityClass.getName(), id, key);

		return sf.withTransaction((session, transaction) -> session
			.find(Translation.class, Identifier.id("pk", pkValue))
			.chain(entity -> {
				if (entity != null) {
					entity.setValue(value);
					return session.persist(entity);
				}
				else {
					Translation translation = new Translation();
					translation.setPk(pkValue);
					translation.setValue(value);
					return session.persist(translation);
				}
			}));
	}

	public <T> Uni<Void> deleteTranslation(
		Class<T> entityClass, Integer id, String language, String key) {

		TranslationKey pkValue =
			new TranslationKey(language, entityClass.getName(), id, key);

		return sf.withTransaction((session, transaction) -> session
			.find(Translation.class, Identifier.id("pk", pkValue))
			.chain(entity -> {
				if (entity != null) {
					return session.remove(entity);
				}
				else {
					return Uni.createFrom().voidItem();
				}
			})
		);
	}

}
