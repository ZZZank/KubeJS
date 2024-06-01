package dev.latvian.kubejs.client;

import com.github.bsideup.jabel.Desugar;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.item.ItemStackJS;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Map;
import java.util.regex.Pattern;

public class LangEventJS extends EventJS {
	/**
	 * only matches lower case letters and underscore, for LangKey or ResourceLocation, or ...
	 */
	public static final Pattern PATTERN = Pattern.compile("[a-z_]+");

	@Desugar
	public record Key(String namespace, String lang, String key) {
	}

	public final String lang;
	public final Map<Key, String> map;

	public LangEventJS(String lang, Map<Key, String> map) {
		this.lang = lang;
		this.map = map;
	}

	public void add(String namespace, String key, String value) {
		if (namespace == null || key == null || value == null || namespace.isEmpty() || key.isEmpty() || value.isEmpty()) {
			throw new IllegalArgumentException("Invalid namespace, key or value: [" + namespace + ", " + key + ", " + value + "]");
		}

		map.put(new Key(namespace, lang, key), value);
	}

	public void addAll(String namespace, Map<String, String> map) {
		for (var e : map.entrySet()) {
			add(namespace, e.getKey(), e.getValue());
		}
	}

	public void add(String key, String value) {
		add("minecraft", key, value);
	}

	public void addAll(Map<String, String> map) {
		addAll("minecraft", map);
	}

	public void renameItem(ItemStackJS stack, String name) {
		if (stack == null || stack.isEmpty()) {
			return;
		}
		var desc = stack.getItem().getDescriptionId();

		if (desc != null && !desc.isEmpty()) {
			add(stack.getMod(), desc, name);
		}
	}

	public void renameBlock(Block block, String name) {
		if (block == null || block == Blocks.AIR) {
			return;
		}
		var d = block.getDescriptionId();

		if (d != null && !d.isEmpty()) {
			var modid = Registry.BLOCK.getKey(block).getNamespace();
			add(modid, d, name);
		}
	}

	public void renameEntity(ResourceLocation id, String name) {
		add(id.getNamespace(), "entity." + id.getNamespace() + "." + id.getPath().replace('/', '.'), name);
	}

	public void renameBiome(ResourceLocation id, String name) {
		add(id.getNamespace(), "biome." + id.getNamespace() + "." + id.getPath().replace('/', '.'), name);
	}
}
