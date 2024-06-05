package dev.latvian.kubejs.registry;

import dev.latvian.kubejs.client.LangEventJS;
import dev.latvian.kubejs.generator.AssetJsonGenerator;
import dev.latvian.kubejs.generator.DataJsonGenerator;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.annotations.typing.JSInfo;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public abstract class BuilderBase<T> implements Supplier<T> {
	public final ResourceLocation id;
	public String translationKey;
	public Component display;
	protected T object;
	public boolean formattedDisplayName;
	public transient boolean dummyBuilder;
	public transient Set<ResourceLocation> defaultTags;

	public BuilderBase(ResourceLocation i) {
		id = i;
		object = null;
		translationKey = "";
		display = null;
		formattedDisplayName = false;
		dummyBuilder = false;
		defaultTags = new HashSet<>();
	}

	public abstract RegistryInfo getRegistryType();

	public abstract T createObject();

	public T transformObject(T obj) {
		return obj;
	}

	@Override
	public final T get() {
		try {
			return object;
		} catch (Exception ex) {
			if (dummyBuilder) {
				throw new RuntimeException("Object '" + id + "' of registry '" + getRegistryType().key.location() + "' is from a dummy builder and doesn't have a value!");
			} else {
				throw new RuntimeException("Object '" + id + "' of registry '" + getRegistryType().key.location() + "' hasn't been registered yet!", ex);
			}
		}
	}

	public void createAdditionalObjects() {
	}

	public String getTranslationKeyGroup() {
		return getRegistryType().languageKeyPrefix;
	}

	@JSInfo("""
		Sets the translation key for this object, e.g. `block.minecraft.stone`.
		""")
	public BuilderBase<T> translationKey(String key) {
		translationKey = key;
		return this;
	}

	@JSInfo("""
		Sets the display name for this object, e.g. `Stone`.

		This will be overridden by a lang file if it exists.
		""")
	public BuilderBase<T> displayName(Component name) {
		display = name;
		return this;
	}

	@JSInfo("""
		Makes displayName() override language files.
		""")
	public BuilderBase<T> formattedDisplayName() {
		formattedDisplayName = true;
		return this;
	}

	@JSInfo("""
			Combined method of formattedDisplayName().displayName(name).""")
	public BuilderBase<T> formattedDisplayName(Component name) {
		return formattedDisplayName().displayName(name);
	}

	@JSInfo("""
		Adds a tag to this object, e.g. `minecraft:stone`.
		""")
	public BuilderBase<T> tag(ResourceLocation tag) {
		defaultTags.add(tag);
		getRegistryType().hasDefaultTags = true;
		return this;
	}

	public ResourceLocation newID(String pre, String post) {
		if (pre.isEmpty() && post.isEmpty()) {
			return id;
		}

		return new ResourceLocation(id.getNamespace() + ':' + pre + id.getPath() + post);
	}

	public void generateDataJsons(DataJsonGenerator generator) {
	}

	public void generateAssetJsons(AssetJsonGenerator generator) {
	}

	public String getBuilderTranslationKey() {
		if (translationKey.isEmpty()) {
			return Util.makeDescriptionId(getTranslationKeyGroup(), id);
		}
		return translationKey;
	}

	public void generateLang(LangEventJS event) {
		var name = display == null
				? UtilsJS.convertSnakeCaseToCamelCase(id.getPath())
				: display.getString();
		event.get(id.getNamespace(), "en_us").add(getBuilderTranslationKey(), name);
	}

	protected T createTransformedObject() {
		object = transformObject(createObject());
		return object;
	}

	@Override
	public String toString() {
		var n = getClass().getName();
		int i = n.lastIndexOf('.');

		if (i != -1) {
			n = n.substring(i + 1);
		}

		return n + "[" + id + "]";
	}
}
