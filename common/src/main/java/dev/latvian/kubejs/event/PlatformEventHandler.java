package dev.latvian.kubejs.event;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.latvian.mods.rhino.util.DynamicFunction;

public abstract class PlatformEventHandler implements DynamicFunction.Callback {

	@ExpectPlatform
	public static PlatformEventHandler instance() {
		throw new AssertionError("Not implemented");
	}

	@ExpectPlatform
	public void unregister() {
		throw new AssertionError("Not implemented");
	}
}
