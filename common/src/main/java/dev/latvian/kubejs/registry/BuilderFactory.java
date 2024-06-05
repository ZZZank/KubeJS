package dev.latvian.kubejs.registry;

import dev.latvian.kubejs.util.BuilderBase;
import net.minecraft.resources.ResourceLocation;

public interface BuilderFactory {
	BuilderBase createBuilder(ResourceLocation id);
}