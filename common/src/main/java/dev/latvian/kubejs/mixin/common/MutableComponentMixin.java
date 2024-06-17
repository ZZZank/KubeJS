package dev.latvian.kubejs.mixin.common;

import dev.latvian.kubejs.core.MutableComponentKJS;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.annotations.RemapPrefixForJS;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Mixin(MutableComponent.class)
@RemapPrefixForJS("kjs$")
public abstract class MutableComponentMixin implements MutableComponentKJS {

	// hidden to avoid ambiguity, the type wrapper should wrap strings to TextComponent anyways
	@HideFromJS
	@Shadow
	public abstract MutableComponent append(String string);

	@Override
	public Iterator<Component> iterator() {
		if (!kjs$hasSiblings()) {
			return UtilsJS.cast(List.of(kjs$self()).iterator());
		}

		List<Component> list = new LinkedList<>();
		list.add(kjs$self());

		for (var child : getSiblings()) {
			if (child instanceof MutableComponentKJS wrapped) {
				wrapped.forEach(list::add);
			} else {
				list.add(child);
			}
		}

		return list.iterator();
	}
}
