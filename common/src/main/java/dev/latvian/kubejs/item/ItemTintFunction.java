package dev.latvian.kubejs.item;

import com.github.bsideup.jabel.Desugar;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Undefined;
import dev.latvian.mods.rhino.mod.util.color.Color;
import dev.latvian.mods.rhino.mod.util.color.SimpleColor;
import dev.latvian.mods.rhino.mod.wrapper.ColorWrapper;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.alchemy.PotionUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@FunctionalInterface
public interface ItemTintFunction {
	Color getColor(ItemStack stack, int index);

	@Desugar
	record Fixed(Color color) implements ItemTintFunction {
		@Override
		public Color getColor(ItemStack stack, int index) {
			return color;
		}
	}

	class Mapped implements ItemTintFunction {
		public final Int2ObjectMap<ItemTintFunction> map = new Int2ObjectArrayMap<>(1);

		@Override
		public Color getColor(ItemStack stack, int index) {
			var f = map.get(index);
			return f == null ? null : f.getColor(stack, index);
		}
	}

	//TODO: this requires a block builder
//	ItemTintFunction BLOCK = (stack, index) -> {
//		if (stack.getItem() instanceof BlockItem block) {
//			var s = block.getBlock().defaultBlockState();
//			var internal = ((BlockKJS) s.getBlock()).kjs$getBlockBuilder();
//
//			if (internal != null && internal.tint != null) {
//				return internal.tint.getColor(s, null, null, index);
//			}
//		}
//
//		return null;
//	};

	ItemTintFunction POTION = (stack, index) -> new SimpleColor(PotionUtils.getColor(stack));
	ItemTintFunction MAP = (stack, index) -> new SimpleColor(MapItem.getColor(stack));
	ItemTintFunction DISPLAY_COLOR_NBT = (stack, index) -> {
		var tag = stack.getTagElement("display");

		if (tag != null && tag.contains("color", 99)) {
			return new SimpleColor(tag.getInt("color"));
		}

		return null;
	};

	@Nullable
	static ItemTintFunction of(Context cx, Object o) {
		if (o == null || Undefined.isUndefined(o)) {
			return null;
		} else if (o instanceof ItemTintFunction f) {
			return f;
		} else if (o instanceof List<?> list) {
			var map = new Mapped();

			for (int i = 0; i < list.size(); i++) {
				var f = of(cx, list.get(i));

				if (f != null) {
					map.map.put(i, f);
				}
			}

			return map;
		} else if (o instanceof CharSequence) {
			var f = switch (o.toString()) {
//				case "block" -> BLOCK; //TODO
				case "potion" -> POTION;
				case "map" -> MAP;
				case "display_color_nbt" -> DISPLAY_COLOR_NBT;
				default -> null;
			};

			if (f != null) {
				return f;
			}
			//TODO: rhizo problem, need to expose more api
//		} else if (o instanceof BaseFunction function) {
//			return (ItemTintFunction) NativeJavaObject.createInterfaceAdapter(cx, ItemTintFunction.class, function);
		}

		return new Fixed(ColorWrapper.of(o));
	}
}