package dev.latvian.kubejs.item;

import net.minecraft.world.item.Item;

public class KubeJSItemProperties extends Item.Properties {
	public final dev.latvian.kubejs.registry.builder.ItemBuilder itemBuilder;

	public KubeJSItemProperties(dev.latvian.kubejs.registry.builder.ItemBuilder itemBuilder) {
		this.itemBuilder = itemBuilder;
	}
}
