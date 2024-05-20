package dev.latvian.kubejs.forge;

import dev.latvian.kubejs.event.PlatformEventHandler;
import net.minecraftforge.eventbus.api.Event;

import java.util.function.Consumer;

@FunctionalInterface
public interface KubeJSForgeEventHandlerWrapper extends Consumer<Event> {

	default KubeJSForgeEventHandlerWrapper secured(Class eventTarget) {
		return event -> {
			try {
				this.accept(event);
			} catch (Exception ex) {
				PlatformEventHandler.logException(ex, "Error when using 'onForgeEvent' for " + eventTarget);
			}
		};
	}
}
