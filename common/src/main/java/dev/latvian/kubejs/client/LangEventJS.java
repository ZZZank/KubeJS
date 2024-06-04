package dev.latvian.kubejs.client;

import com.github.bsideup.jabel.Desugar;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.item.ItemStackJS;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class LangEventJS extends EventJS {
	/**
	 * only matches lower case letters and underscore, for LangKey or ResourceLocation, or ...
	 */
	public static final Pattern PATTERN = Pattern.compile("[a-z_]+");

	@Desugar
	public record LangEntries(String namespace, String lang, Map<String,String> entries) {
		public LangEntries(String namespace, String lang) {
			this(namespace, lang, new HashMap<>());
		}

		public void add(String key, String text) {
			ensureValid(key, text);
			entries.put(key, text);
		}

		public void addAll(Map<String, String> map) {
			for (var entry : map.entrySet()) {
				add(entry.getKey(), entry.getValue());
			}
		}

		public void addIfAbsent(String key, String text) {
			ensureValid(key, text);
			entries.putIfAbsent(key, text);
		}

		public JsonObject asJson() {
			JsonObject obj = new JsonObject();
			for (var entry : entries.entrySet()) {
				obj.addProperty(entry.getKey(), entry.getValue());
			}
			return obj;
		}

		public static void ensureValid(String key, String text) {
			if (key == null || text == null || key.trim().isEmpty()) {
				throw new IllegalArgumentException("Invalid key or value: [" + key + ", " + text + "]");
			}
		}
	}

	public final Map<String, Map<String, LangEntries>> namespace2lang2entries;

	public LangEventJS() {
		namespace2lang2entries = new HashMap<>();
	}

	public LangEntries get(String namespace, String lang) {
		if (namespace == null || lang == null || namespace.isEmpty() || lang.isEmpty()) {
			throw new IllegalArgumentException("Invalid namespace or lang: [" + namespace + ", " + lang + "]");
		}
		var namespaced = namespace2lang2entries.get(namespace);
		if (namespaced == null) {
			namespaced = new HashMap<>();
			namespace2lang2entries.put(namespace, namespaced);
		}
		var entries = namespaced.get(lang);
		if (entries == null) {
			entries = new LangEntries(namespace, lang);
			namespaced.put(lang, entries);
		}
		return entries;
	}

	public LangEntries get(String lang) {
		return get(KubeJS.MOD_ID, lang);
	}

	public void renameItem(String lang, ItemStackJS stack, String name) {
		if (stack == null || stack.isEmpty()) {
			return;
		}
		var desc = stack.getItem().getDescriptionId();
		if (desc != null && !desc.isEmpty()) {
			get(stack.getMod(), lang).add(desc, name);
		}
	}

	public void renameBlock(String lang, Block block, String name) {
		if (block == null || block == Blocks.AIR) {
			return;
		}
		var desc = block.getDescriptionId();
		if (desc != null && !desc.isEmpty()) {
			var modid = Registry.BLOCK.getKey(block).getNamespace();
			get(modid, lang).add(desc, name);
		}
	}

	public void renameEntity(String lang, ResourceLocation id, String name) {
		get(id.getNamespace(), lang).add("entity." + id.getNamespace() + "." + id.getPath().replace('/', '.'), name);
	}

	public void renameBiome(String lang, ResourceLocation id, String name) {
		get(id.getNamespace(), lang).add("biome." + id.getNamespace() + "." + id.getPath().replace('/', '.'), name);
	}
}
