package dev.latvian.kubejs.registry.types;

import dev.latvian.kubejs.registry.BuilderBase;
import dev.latvian.kubejs.registry.RegistryInfo;
import net.minecraft.resources.ResourceLocation;

public class CustomStatBuilder extends BuilderBase<ResourceLocation> {
	public CustomStatBuilder(ResourceLocation i) {
		super(i);
	}

	@Override
	public final RegistryInfo<ResourceLocation> getRegistryType() {
		return RegistryInfo.CUSTOM_STAT;
	}

	@Override
	public ResourceLocation createObject() {
		return id;
	}
}
