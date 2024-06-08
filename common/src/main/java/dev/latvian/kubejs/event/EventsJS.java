package dev.latvian.kubejs.event;

import dev.latvian.kubejs.script.ScriptManager;
import dev.latvian.mods.rhino.RhinoException;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class EventsJS {

	public final ScriptManager scriptManager;
	private final Map<String, List<IEventHandler>> map;

	public EventsJS(ScriptManager t) {
		scriptManager = t;
		map = new Object2ObjectOpenHashMap<>();
	}

	public void listen(String id, IEventHandler handler) {
		id = id.replace("yeet", "remove");

		var list = map.get(id);
		if (list == null) {
			list = new ObjectArrayList<>();
			map.put(id, list);
		}

		list.add(handler);
	}

	public List<IEventHandler> handlers(String id) {
		List<IEventHandler> list = map.get(id);
		return list == null ? Collections.emptyList() : list;
	}

	/**
	 * @return true if there's one handler tried to cancel the event, and the event is cancellable
	 */
	public boolean postToHandlers(String id, List<IEventHandler> list, EventJS event) {
		if (list.isEmpty()) {
			return false;
		}

		boolean c = event.canCancel();

		for (var handler : list) {
			try {
				handler.onEvent(event);

				if (c && event.isCancelled()) {
					return true;
				}
			} catch (RhinoException ex) {
				scriptManager.type.console.error("Error occurred while handling event '" + id + "': " + ex.getMessage());
			} catch (Throwable ex) {
				ex.printStackTrace();
			}
		}

		//ScriptManager.instance.currentFile = null;
		return false;
	}

	public void clear() {
		map.clear();
	}
}