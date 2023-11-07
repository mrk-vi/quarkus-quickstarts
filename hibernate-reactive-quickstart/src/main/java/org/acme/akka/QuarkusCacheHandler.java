package org.acme.akka;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.receptionist.Receptionist;
import akka.actor.typed.receptionist.ServiceKey;
import io.quarkus.cache.Cache;
import org.acme.cache.CacheUtils;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class QuarkusCacheHandler extends AbstractBehavior<QuarkusCacheHandler.Command> {

	public static final ServiceKey<Command> SERVICE_KEY =
		ServiceKey.create(Command.class, "quarkus-cache-handler");

	public static Behavior<Command> create(List<Cache> cacheList) {
		return Behaviors.setup(ctx -> new QuarkusCacheHandler(ctx, cacheList));
	}

	public sealed interface Command {}
	private enum Start implements Command {INSTANCE}
	public enum InvalidateAllLocal implements Command {INSTANCE}
	public enum InvalidateAllRemotes implements Command, CborSerializable {INSTANCE}
	private record ListingAdapter(Receptionist.Listing response) implements Command {}

	private final ActorSystem<Void> system;
	private final List<Cache> cacheList;
	private Set<ActorRef<Command>> instances;

	public QuarkusCacheHandler(ActorContext<Command> context, List<Cache> cacheList) {
		super(context);
		this.system = context.getSystem();
		this.cacheList = cacheList;
		getContext().getSelf().tell(Start.INSTANCE);
	}

	@Override
	public Receive<Command> createReceive() {
		return newReceiveBuilder()
			.onMessageEquals(Start.INSTANCE, this::onStart)
			.onMessage(ListingAdapter.class, this::onListing)
			.onMessageEquals(InvalidateAllLocal.INSTANCE, this::onInvalidateAllLocal)
			.onMessageEquals(InvalidateAllRemotes.INSTANCE, this::onInvalidateAllOthers)
			.build();
	}

	private Behavior<Command> onStart() {
		receptionist().tell(Receptionist.register(SERVICE_KEY, getContext().getSelf()));

		ActorRef<Receptionist.Listing> messagedAdapter =
			getContext().messageAdapter(Receptionist.Listing.class, ListingAdapter::new);

		receptionist().tell(Receptionist.subscribe(SERVICE_KEY, messagedAdapter));

		return Behaviors.same();
	}

	private Behavior<Command> onListing(ListingAdapter listingAdapter) {
		Receptionist.Listing listing = listingAdapter.response();

		this.instances = listing
			.getServiceInstances(SERVICE_KEY)
			.stream()
			.filter(ref -> !ref.equals(this.getContext().getSelf()))
			.collect(Collectors.toSet());

		return Behaviors.same();
	}

	private Behavior<Command> onInvalidateAllLocal() {

		invalidateLocalCache();

		for (ActorRef<Command> instance : this.instances) {
			if (log.isDebugEnabled()) {
				log.debug("trigger other nodes");
			}
			instance.tell(InvalidateAllRemotes.INSTANCE);
		}

		return Behaviors.same();
	}

	private Behavior<Command> onInvalidateAllOthers() {
		invalidateLocalCache();
		return Behaviors.same();
	}

	private void invalidateLocalCache() {
		for (Cache cache : this.cacheList) {
			if (log.isDebugEnabled()) {
				log.debug("invalidate local cache");
			}
			CacheUtils.invalidateAllAsync(cache);
		}
	}

	private ActorRef<Receptionist.Command> receptionist() {
		return this.system.receptionist();
	}

	private final static Logger log = Logger.getLogger(QuarkusCacheHandler.class);
}
