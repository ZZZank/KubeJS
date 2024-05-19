package dev.latvian.kubejs.event;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.latvian.mods.rhino.util.DynamicFunction;
import org.jetbrains.annotations.Contract;

public abstract class PlatformEventHandler implements DynamicFunction.Callback {

	@ExpectPlatform
	@Contract(value = " -> _")
	public static PlatformEventHandler instance() {
		throw new AssertionError("Not implemented");
	}

	@ExpectPlatform
	@Contract(value = " -> _")
	public void unregister() {
		throw new AssertionError("Not implemented");
	}
}
