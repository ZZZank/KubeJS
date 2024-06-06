package dev.latvian.kubejs.client;

import com.google.gson.JsonElement;
import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.generator.AssetJsonGenerator;
import dev.latvian.kubejs.registry.RegistryInfo;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.script.data.KubeJSResourcePack;
import dev.latvian.kubejs.util.KubeJSPlugins;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KubeJSClientResourcePack extends KubeJSResourcePack {
	public static List<PackResources> inject(List<PackResources> packs) {
		if (KubeJS.instance == null) {
			return packs;
		}
		List<PackResources> injected = new ArrayList<>(packs);
		//KubeJS injected resources should have lower priority then user resources packs, which means file pack
		int pos = injected.size();
		for (int i = 0; i < injected.size(); i++) {
			if (injected.get(i) instanceof FilePackResources) {
				pos = i;
				break;
			}
		}
		injected.add(pos, new KubeJSClientResourcePack());
		return injected;
	}

	public KubeJSClientResourcePack() {
		super(PackType.CLIENT_RESOURCES);
	}

	@Override
	public void generateJsonFiles(Map<ResourceLocation, JsonElement> map) {
		AssetJsonGenerator generator = new AssetJsonGenerator(map);
		KubeJSPlugins.forEachPlugin(p -> p.generateAssetJsons(generator));

		for (var builder : RegistryInfo.ALL_BUILDERS) {
			builder.generateAssetJsons(generator);
		}

		generateLang(generator);
	}

	private void generateLang(AssetJsonGenerator generator) {
		var langEvent = new LangEventJS();
		Map<String, String> langMap = new HashMap<>();

		for (var builder : RegistryInfo.ALL_BUILDERS) {
			builder.generateLang(langEvent);
		}

		KubeJSPlugins.forEachPlugin(p -> p.generateLang(langMap));
		langEvent.post(ScriptType.CLIENT, KubeJSEvents.CLIENT_LANG);

		//using a special namespace to keep backward equivalence
		langEvent.get("kubejs_generated", "en_us").addAll(langMap);

		for (var lang2entries : langEvent.namespace2lang2entries.values()) {
			for (var entries : lang2entries.values()) {
				generator.json(entries.path(), entries.asJson());
			}
		}
	}
}
