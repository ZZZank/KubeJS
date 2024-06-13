package dev.latvian.kubejs.script;

import com.mojang.serialization.Codec;
import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSRegistries;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.util.wrap.TypeWrapperFactory;
import dev.latvian.mods.rhino.util.wrap.TypeWrappers;
import me.shedaniel.architectury.registry.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class RegistryTypeWrapperFactory<T> implements TypeWrapperFactory<T> {
	private static List<RegistryTypeWrapperFactory<?>> all;

    public static void register(TypeWrappers wrappers) {
        for (var wrapperFactory : getAll()) {
            try {
                wrappers.register(wrapperFactory.type, UtilsJS.cast(wrapperFactory));
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

	@SuppressWarnings({"rawtypes", "unchecked"})
	public static List<RegistryTypeWrapperFactory<?>> getAll() {
        if (all != null) {
            return all;
        }
        all = new ArrayList<>();
        try {
            for(var registry : KubeJSRegistries.registries()) {
                var key = registry.key();
                var type = (ParameterizedType) registry.getClass().getGenericSuperclass(); // Registry<T> in `? extends Registry<T>`
                var T = type.getActualTypeArguments()[0]; //T in `Registry<T>`
                Class raw = UtilsJS.getRawType(T);
                if (raw == Item.class || raw == ResourceLocation.class || raw == ResourceKey.class || raw == Codec.class) {
                    continue;
                }
                all.add(new RegistryTypeWrapperFactory(raw, KubeJSRegistries.genericRegistry(UtilsJS.cast(key)), key.location().toString()));
            }
        } catch (Exception ex) {
            KubeJS.LOGGER.error("Failed to register TypeWrappers for registries!");
            ex.printStackTrace();
        }

        return all;
	}

	public final Class<T> type;
	public final Registry<T> registry;
	public final String name;

	private RegistryTypeWrapperFactory(Class<T> type, Registry<T> registry, String name) {
		this.type = type;
		this.registry = registry;
		this.name = name;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T wrap(Object o) {
		if (o == null) {
			return null;
		} else if (type.isAssignableFrom(o.getClass())) {
			return (T) o;
		}

		return registry.get(UtilsJS.getMCID(o));
	}

	@Override
	public String toString() {
		return "RegistryTypeWrapperFactory{type=" + type.getName() + ", registry=" + name + '}';
	}
}
