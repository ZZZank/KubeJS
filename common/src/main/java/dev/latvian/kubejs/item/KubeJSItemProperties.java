package dev.latvian.kubejs.item;

import dev.latvian.kubejs.registry.builder.item.ItemBuilder;
import net.minecraft.world.item.Item;

public class KubeJSItemProperties extends Item.Properties {
	public final ItemBuilder itemBuilder;

	public KubeJSItemProperties(ItemBuilder itemBuilder) {
		this.itemBuilder = itemBuilder;
	}
}
