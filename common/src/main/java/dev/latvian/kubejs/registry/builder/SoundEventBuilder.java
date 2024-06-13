package dev.latvian.kubejs.registry.builder;

import dev.latvian.kubejs.registry.RegistryInfo;
import dev.latvian.kubejs.registry.BuilderBase;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class SoundEventBuilder extends BuilderBase<SoundEvent> {
	public SoundEventBuilder(ResourceLocation i) {
		super(i);
	}

	@Override
	public final RegistryInfo getRegistryType() {
		return RegistryInfo.SOUND_EVENT;
	}

	@Override
	public SoundEvent createObject() {
		return new SoundEvent(id);
	}
}
