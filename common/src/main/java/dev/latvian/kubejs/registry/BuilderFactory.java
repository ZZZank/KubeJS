package dev.latvian.kubejs.registry;

import net.minecraft.resources.ResourceLocation;

public interface BuilderFactory {
	BuilderBase createBuilder(ResourceLocation id);
}