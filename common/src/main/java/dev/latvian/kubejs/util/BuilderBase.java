package dev.latvian.kubejs.util;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.client.LangEventJS;
import dev.latvian.kubejs.generator.AssetJsonGenerator;
import dev.latvian.kubejs.generator.DataJsonGenerator;
import dev.latvian.kubejs.registry.RegistryInfo;
import dev.latvian.mods.rhino.annotations.typing.JSInfo;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @deprecated
 * @see dev.latvian.kubejs.registry.BuilderBase
 * @author LatvianModder
 */
public abstract class BuilderBase<T> implements Supplier<T> {
	public final ResourceLocation id;
	public String translationKey;
	public String displayName;
	public Component display;
	protected T object;
	public boolean formattedDisplayName;
	public transient boolean dummyBuilder;
	public transient Set<ResourceLocation> defaultTags;

	public BuilderBase(ResourceLocation i) {
		id = i;
		object = null;
		translationKey = ""; //compute lazily
		display = null;
		formattedDisplayName = false;
		dummyBuilder = false;
		defaultTags = new HashSet<>();
	}

	public BuilderBase(String s) {
		this(UtilsJS.getMCID(KubeJS.appendModId(s)));
//		displayName = Arrays.stream(id.getPath().split("_")).map(UtilsJS::toTitleCase).collect(Collectors.joining(" "));
	}

	public abstract RegistryInfo getRegistryType();

	public abstract T createObject();

	public T transformObject(T obj) {
		return obj;
	}

	@Deprecated
	public String getBuilderType() {
		return "";
	}

	@Override
	public final T get() {
		return object;
	}

	public void createAdditionalObjects() {
	}

	public String getTranslationKeyGroup() {
		return getRegistryType().languageKeyPrefix;
	}

	@Deprecated
	public void add() {
		ConsoleJS.STARTUP.setLineNumber(true);
		ConsoleJS.STARTUP.log("You no longer need to use .add() at end of " + getBuilderType() + " builder!");
		ConsoleJS.STARTUP.setLineNumber(false);
	}

	@JSInfo("""
		Sets the translation key for this object, e.g. `block.minecraft.stone`.
		""")
	public BuilderBase<T> translationKey(String key) {
		translationKey = key;
		return this;
	}

	@JSInfo("Deprecated, use `.display()` instead")
	@Deprecated
	public BuilderBase<T> displayName(String name) {
		return display(new TextComponent(name));
	}

	@JSInfo("""
		Sets the display name for this object, e.g. `Stone`.

		This will be overridden by a lang file if it exists.
		""")
	public BuilderBase<T> display(Component name) {
		display = name;
		displayName = display.getString();
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
		return formattedDisplayName().display(name);
	}

	@JSInfo("""
		Adds a tag to this object, e.g. `minecraft:stone`.
		""")
	public BuilderBase<T> tag(ResourceLocation tag) {
		defaultTags.add(tag);
		getRegistryType().hasDefaultTags = true;
		return this;
	}

	public ResourceLocation newID(String prefix, String post) {
		if (prefix.isEmpty() && post.isEmpty()) {
			return id;
		}
		return new ResourceLocation(id.getNamespace(), prefix + id.getPath() + post);
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

	@JSInfo("dev only, do not use")
	public T createTransformedObject() {
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
