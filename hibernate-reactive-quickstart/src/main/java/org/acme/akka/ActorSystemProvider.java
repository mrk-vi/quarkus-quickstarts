package org.acme.akka;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.Behaviors;
import io.quarkus.cache.Cache;
import io.quarkus.cache.CacheName;
import io.quarkus.runtime.Startup;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@Startup
@ApplicationScoped
public class ActorSystemProvider {

	private ActorSystem<?> actorSystem;

	@PostConstruct
	public void init() {
		this.actorSystem = ActorSystem.create(
			Behaviors.setup(ctx -> {
				ctx.spawnAnonymous(QuarkusCacheHandler.create(List.of(fruitsCache)));
				return Behaviors.empty();
			}),
			"actor-system-cache");
	}

	@PreDestroy
	public void destroy() {
		this.actorSystem.terminate();
		this.actorSystem = null;
	}

	public ActorSystem<?> get() {
		return this.actorSystem;
	}

	@Inject
	@CacheName("fruits-cache")
	Cache fruitsCache;

}
