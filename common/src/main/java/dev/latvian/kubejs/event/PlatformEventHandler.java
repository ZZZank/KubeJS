package dev.latvian.kubejs.event;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.mods.rhino.util.DynamicFunction;
import org.jetbrains.annotations.Contract;

public abstract class PlatformEventHandler implements DynamicFunction.Callback {

	@ExpectPlatform
	@Contract(value = " -> _")
	public static PlatformEventHandler instance() {
		throw new AssertionError("Not implemented");
	}

	@Contract(value = " -> _")
	public abstract void unregister();

	/**
	 * Helper method to throw and log an exception with the stack trace. Rhino seems to not be able to print the full stacktrace.
	 * The user still only gets the basic message inside startup log. The full stacktrace will be logged at `latest.log`
	 */
	public static void logException(Exception e, String desc) {
		ScriptType.STARTUP.console.error(desc + ": " + e.getLocalizedMessage());
		for (var ste : e.getStackTrace()) {
			KubeJS.LOGGER.error(ste.toString());
		}
	}
}
