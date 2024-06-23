package dev.latvian.kubejs.event.forge;

import dev.latvian.kubejs.event.PlatformEventHandler;
import dev.latvian.kubejs.forge.KubeJSForgeEventHandlerWrapper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PlatformEventHandlerImpl extends PlatformEventHandler {
	private static final PlatformEventHandlerImpl INSTANCE = new PlatformEventHandlerImpl();
	private final List<KubeJSForgeEventHandlerWrapper> listeners = new ArrayList<>();

	@Override
	public void unregister() {
		for (var listener : this.listeners) {
			MinecraftForge.EVENT_BUS.unregister(listener);
		}
		this.listeners.clear();
	}

	/**
	 * @see PlatformEventHandler#instance()
	 */
	public static PlatformEventHandler instance() {
		return INSTANCE;
	}

	@Override
	public @Nullable Object call(Object[] params) {
		if (params.length < 2) {
			throw new RuntimeException("Invalid syntax! onForgeEvent(string | Class, function) required event class and handler");
		}

		try {
            var clazz = params[0] instanceof Class c ? c : Class.forName(params[0].toString());
            if (!Event.class.isAssignableFrom(clazz)) {
                throw new RuntimeException("The first parameter of onForgeEvent() must represent a class that is a subclass of `net.minecraftforge.eventbus.api.Event`");
            }
            var handler = secured((KubeJSForgeEventHandlerWrapper) params[1],clazz);
			MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, clazz, handler);
			listeners.add(handler);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

		return null;
	}

	/**
	 * wrap provided event handler with a try-catch that will catch Exception and log them using {@link PlatformEventHandler#logException(Exception, String)}
	 * @param handler event handler
	 * @param eventTarget event type class
	 * @return wrapped handler
	 */
	static KubeJSForgeEventHandlerWrapper secured(KubeJSForgeEventHandlerWrapper handler, Class eventTarget) {
		return event -> {
			try {
				handler.accept(event);
			} catch (Exception ex) {
				logException(ex, "Error when calling 'onForgeEvent' for " + eventTarget);
			}
		};
	}
}
