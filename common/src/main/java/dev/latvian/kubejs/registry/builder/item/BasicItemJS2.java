package dev.latvian.kubejs.registry.builder.item;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import me.shedaniel.architectury.registry.fuel.FuelRegistry;
import dev.latvian.kubejs.registry.RegistryInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class BasicItemJS2 extends Item {
	public static class Builder extends ItemBuilder {
		public Builder(ResourceLocation i) {
			super(i);
		}

		@Override
		public Item createObject() {
			return new BasicItemJS2(this);
		}
	}

	private final ItemBuilder itemBuilder;
	private final Multimap<Attribute, AttributeModifier> attributes;
	private boolean modified = false;

	public BasicItemJS2(ItemBuilder p) {
		super(p.createItemProperties());
		this.itemBuilder = p;

		if (p.burnTime > 0) {
			FuelRegistry.register(p.burnTime, this);
		}

		attributes = ArrayListMultimap.create();
	}

	public ItemBuilder kjs$getItemBuilder() {
		return itemBuilder;
	}

	@Override
	public Component getName(ItemStack itemStack) {
		if (itemBuilder.displayName != null && itemBuilder.formattedDisplayName) {
			return itemBuilder.displayName;
		}

		return super.getName(itemStack);
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
		if (!modified) {
			itemBuilder.attributes.forEach((r, m) -> attributes.put(RegistryInfo.ATTRIBUTE.getValue(r), m));
			modified = true;
		}

		return slot == EquipmentSlot.MAINHAND ? attributes : super.getDefaultAttributeModifiers(slot);
	}
}