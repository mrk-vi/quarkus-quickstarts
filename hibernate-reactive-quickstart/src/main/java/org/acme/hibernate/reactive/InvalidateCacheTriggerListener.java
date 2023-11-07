package org.acme.hibernate.reactive;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.AskPattern;
import akka.actor.typed.receptionist.Receptionist;
import org.acme.akka.ActorSystemProvider;
import org.acme.akka.QuarkusCacheHandler;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import java.time.Duration;

@ApplicationScoped
public class InvalidateCacheTriggerListener {

	@Inject
	ActorSystemProvider actorSystemProvider;

	@PostRemove
	@PostPersist
	@PostUpdate
	void handle(Object ignore) {
		if (log.isDebugEnabled()) {
			log.debug("trigger cache invalidation");
		}

		invalidateLocalCache(actorSystemProvider.get());
	}

	private static void invalidateLocalCache(ActorSystem<?> actorSystem) {
		Receptionist receptionist = Receptionist.get(actorSystem);

		AskPattern.ask(
			receptionist.ref(),
			(ActorRef<Receptionist.Listing> replyTo) ->
				Receptionist.find(QuarkusCacheHandler.SERVICE_KEY, replyTo),
			Duration.ofSeconds(10),
			actorSystem.scheduler()
		).whenComplete(
			(listing, throwable) -> {
				if (throwable == null) {
					listing
						.getServiceInstances(QuarkusCacheHandler.SERVICE_KEY)
						.stream()
						.filter(ref -> ref.path().address().port().isEmpty())
						.forEach(ref -> ref.tell(QuarkusCacheHandler.InvalidateAllLocal.INSTANCE));
				}
				else {
					log.warn("cannot trigger cache invalidation", throwable);
				}
			}
		);
	}


	private final static Logger log = Logger.getLogger(InvalidateCacheTriggerListener.class);
}
