package dev.latvian.kubejs.registry;

import com.mojang.serialization.Codec;
import dev.latvian.kubejs.CommonProperties;
import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.item.ItemRegistryEventJS;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.util.ConsoleJS;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.util.wrap.TypeWrapperFactory;
import me.shedaniel.architectury.registry.Registries;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.StatType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.feature.blockplacers.BlockPlacerType;
import net.minecraft.world.level.levelgen.feature.featuresize.FeatureSizeType;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElementType;
import net.minecraft.world.level.levelgen.feature.structures.StructureTemplatePool;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraft.world.level.levelgen.structure.templatesystem.PosRuleTestType;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author ZZZank
 */
public final class RegistryInfo<T> implements Iterable<BuilderBase<? extends T>>, TypeWrapperFactory<T> {
	public static final Map<ResourceKey<? extends Registry<?>>, RegistryInfo<?>> MAP = Collections.synchronizedMap(new LinkedHashMap<>());
	public static final Map<ResourceKey<? extends Registry<?>>, RegistryInfo<?>> WITH_TYPE = Collections.synchronizedMap(new LinkedHashMap<>());
	public static final List<BuilderBase<?>> ALL_BUILDERS = new LinkedList<>();

//	@Info("Platform-agnostic wrapper of minecraft registries, can be used to register content or get objects from the registry")
	private static final Registries REGISTRIES = Registries.get(KubeJS.MOD_ID);

	public static <T> RegistryInfo<T> of(ResourceKey<? extends Registry<?>> key, Class<T> type) {
		var r = MAP.get(key);

		if (r == null) {
			var reg = new RegistryInfo<>(UtilsJS.cast(key), type);
			MAP.put(key, reg);
			return reg;
		}

		return (RegistryInfo<T>) r;
	}

	private static <T> RegistryInfo<T> of(Registry<?> registry, Class<T> type) {
		return of(registry.key(), type);
	}

	public static RegistryInfo<?> of(ResourceKey<? extends Registry<?>> key) {
		return of(key, Object.class);
	}

	public static final RegistryInfo<SoundEvent> SOUND_EVENT = of(Registry.SOUND_EVENT, SoundEvent.class);
	public static final RegistryInfo<Fluid> FLUID = of(Registry.FLUID, Fluid.class);
	public static final RegistryInfo<MobEffect> MOB_EFFECT = of(Registry.MOB_EFFECT, MobEffect.class).languageKeyPrefix("effect");
	public static final RegistryInfo<Block> BLOCK = of(Registry.BLOCK, Block.class);
	public static final RegistryInfo<Enchantment> ENCHANTMENT = of(Registry.ENCHANTMENT, Enchantment.class);
	public static final RegistryInfo<EntityType> ENTITY_TYPE = of(Registry.ENTITY_TYPE, EntityType.class);
	public static final RegistryInfo<Item> ITEM = of(Registry.ITEM, Item.class).noAutoWrap().customRegistryEvent(ItemRegistryEventJS::new);
	public static final RegistryInfo<Potion> POTION = of(Registry.POTION, Potion.class);
	public static final RegistryInfo<ParticleType> PARTICLE_TYPE = of(Registry.PARTICLE_TYPE, ParticleType.class);
	public static final RegistryInfo<BlockEntityType> BLOCK_ENTITY_TYPE = of(Registry.BLOCK_ENTITY_TYPE, BlockEntityType.class);
	public static final RegistryInfo<ResourceLocation> CUSTOM_STAT = of(Registry.CUSTOM_STAT, ResourceLocation.class);
	public static final RegistryInfo<ChunkStatus> CHUNK_STATUS = of(Registry.CHUNK_STATUS, ChunkStatus.class);
	public static final RegistryInfo<RuleTestType> RULE_TEST = of(Registry.RULE_TEST, RuleTestType.class);
	public static final RegistryInfo<PosRuleTestType> POS_RULE_TEST = of(Registry.POS_RULE_TEST, PosRuleTestType.class);
	public static final RegistryInfo<MenuType> MENU = of(Registry.MENU, MenuType.class);
	public static final RegistryInfo<RecipeType> RECIPE_TYPE = of(Registry.RECIPE_TYPE, RecipeType.class);
	public static final RegistryInfo<RecipeSerializer> RECIPE_SERIALIZER = of(Registry.RECIPE_SERIALIZER, RecipeSerializer.class);
	public static final RegistryInfo<Attribute> ATTRIBUTE = of(Registry.ATTRIBUTE, Attribute.class);
	public static final RegistryInfo<StatType> STAT_TYPE = of(Registry.STAT_TYPE, StatType.class);
	public static final RegistryInfo<VillagerType> VILLAGER_TYPE = of(Registry.VILLAGER_TYPE, VillagerType.class);
	public static final RegistryInfo<VillagerProfession> VILLAGER_PROFESSION = of(Registry.VILLAGER_PROFESSION, VillagerProfession.class);
	public static final RegistryInfo<PoiType> POINT_OF_INTEREST_TYPE = of(Registry.POINT_OF_INTEREST_TYPE, PoiType.class);
	public static final RegistryInfo<MemoryModuleType> MEMORY_MODULE_TYPE = of(Registry.MEMORY_MODULE_TYPE, MemoryModuleType.class);
	public static final RegistryInfo<SensorType> SENSOR_TYPE = of(Registry.SENSOR_TYPE, SensorType.class);
	public static final RegistryInfo<Schedule> SCHEDULE = of(Registry.SCHEDULE, Schedule.class);
	public static final RegistryInfo<Activity> ACTIVITY = of(Registry.ACTIVITY, Activity.class);
	public static final RegistryInfo<LootPoolEntryType> LOOT_ENTRY = of(Registry.LOOT_POOL_ENTRY_TYPE, LootPoolEntryType.class);
	public static final RegistryInfo<LootItemFunctionType> LOOT_FUNCTION = of(Registry.LOOT_FUNCTION_TYPE, LootItemFunctionType.class);
	public static final RegistryInfo<LootItemConditionType> LOOT_ITEM = of(Registry.LOOT_CONDITION_TYPE, LootItemConditionType.class);
	public static final RegistryInfo<DimensionType> DIMENSION_TYPE = of(Registry.DIMENSION_TYPE_REGISTRY, DimensionType.class);
	public static final RegistryInfo<Level> DIMENSION = of(Registry.DIMENSION_REGISTRY, Level.class);
	public static final RegistryInfo<LevelStem> LEVEL_STEM = of(Registry.LEVEL_STEM_REGISTRY, LevelStem.class);
	public static final RegistryInfo<NoiseGeneratorSettings> NOISE_GENERATOR_SETTINGS = of(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, NoiseGeneratorSettings.class);
	public static final RegistryInfo<ConfiguredWorldCarver> CONFIGURED_CARVER = of(Registry.CONFIGURED_CARVER_REGISTRY, ConfiguredWorldCarver.class);
	public static final RegistryInfo<ConfiguredFeature> CONFIGURED_FEATURE = of(Registry.CONFIGURED_FEATURE_REGISTRY, ConfiguredFeature.class);
	public static final RegistryInfo<StructureProcessorList> PROCESSOR_LIST = of(Registry.PROCESSOR_LIST_REGISTRY, StructureProcessorList.class);
	public static final RegistryInfo<StructureTemplatePool> TEMPLATE_POOL = of(Registry.TEMPLATE_POOL_REGISTRY, StructureTemplatePool.class);
	public static final RegistryInfo<Biome> BIOME = of(Registry.BIOME_REGISTRY, Biome.class);
	public static final RegistryInfo<WorldCarver> CARVER = of(Registry.CARVER, WorldCarver.class);
	public static final RegistryInfo<Feature> FEATURE = of(Registry.FEATURE, Feature.class);
	public static final RegistryInfo<StructurePieceType> STRUCTURE_PIECE = of(Registry.STRUCTURE_PIECE, StructurePieceType.class);
	public static final RegistryInfo<BlockStateProviderType> BLOCK_STATE_PROVIDER_TYPE = of(Registry.BLOCK_STATE_PROVIDER_TYPE_REGISTRY, BlockStateProviderType.class);
	public static final RegistryInfo<FoliagePlacerType> FOLIAGE_PLACER_TYPE = of(Registry.FOLIAGE_PLACER_TYPES, FoliagePlacerType.class);
	public static final RegistryInfo<TrunkPlacerType> TRUNK_PLACER_TYPE = of(Registry.TRUNK_PLACER_TYPES, TrunkPlacerType.class);
	public static final RegistryInfo<TreeDecoratorType> TREE_DECORATOR_TYPE = of(Registry.TREE_DECORATOR_TYPES, TreeDecoratorType.class);
	public static final RegistryInfo<BlockPlacerType> BLOCK_PLACER_TYPE = of(Registry.BLOCK_PLACER_TYPE_REGISTRY, BlockPlacerType.class);
	public static final RegistryInfo<FeatureSizeType> FEATURE_SIZE_TYPE = of(Registry.FEATURE_SIZE_TYPES, FeatureSizeType.class);
	public static final RegistryInfo<Codec> BIOME_SOURCE = of(Registry.BIOME_SOURCE, Codec.class);
	public static final RegistryInfo<Codec> CHUNK_GENERATOR = of(Registry.CHUNK_GENERATOR, Codec.class);
	public static final RegistryInfo<StructureProcessorType> STRUCTURE_PROCESSOR = of(Registry.STRUCTURE_PROCESSOR, StructureProcessorType.class);
	public static final RegistryInfo<StructurePoolElementType> STRUCTURE_POOL_ELEMENT = of(Registry.STRUCTURE_POOL_ELEMENT, StructurePoolElementType.class);
	//TODO: creative tab registry
//	public static final RegistryInfo<BannerPattern> BANNER_PATTERN = of(Registry.BANNER_PATTERN, BannerPattern.class);
//	public static final RegistryInfo<CreativeModeTab> CREATIVE_MODE_TAB = of(Registry.CREATIVE_MODE_TAB, CreativeModeTab.class);
	public static final RegistryInfo<LootItemConditionType> LOOT_CONDITION_TYPE = of(Registry.LOOT_CONDITION_TYPE, LootItemConditionType.class);
	public static final RegistryInfo<LootItemFunctionType> LOOT_FUNCTION_TYPE = of(Registry.LOOT_FUNCTION_TYPE, LootItemFunctionType.class);
	public static final RegistryInfo<LootPoolEntryType> LOOT_POOL_ENTRY_TYPE = of(Registry.LOOT_POOL_ENTRY_TYPE, LootPoolEntryType.class);
	public static final RegistryInfo<NoiseGeneratorSettings> NOISE_SETTINGS = of(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, NoiseGeneratorSettings.class);

	/**
	 * Add your registry to these to make sure it comes after vanilla registries, if it depends on them.
	 * Only works on Fabric, since Forge already has ordered registries.
	 */
	public static final LinkedList<RegistryInfo<?>> AFTER_VANILLA = new LinkedList<>();

	public final ResourceKey<? extends Registry<T>> key;
	public final Class<?> objectBaseClass;
	public final Map<String, BuilderType<T>> types;
	public final Map<ResourceLocation, BuilderBase<? extends T>> objects;
	public boolean hasDefaultTags = false;
	private BuilderType<T> defaultType;
	public boolean bypassServerOnly;
	public boolean autoWrap;
	private me.shedaniel.architectury.registry.Registry<T> archRegistry;
	public String languageKeyPrefix;
	public Supplier<RegistryEventJS<T>> customRegEvent;

	private RegistryInfo(ResourceKey<? extends Registry<T>> key, Class<T> objectBaseClass) {
		this.key = key;
		this.objectBaseClass = objectBaseClass;
		this.types = new LinkedHashMap<>();
		this.objects = new LinkedHashMap<>();
		this.bypassServerOnly = false;
		this.autoWrap = objectBaseClass != Codec.class && objectBaseClass != ResourceLocation.class && objectBaseClass != String.class;
		this.languageKeyPrefix = key.location().getPath().replace('/', '.');
		this.customRegEvent = null;
	}

	public RegistryInfo<T> bypassServerOnly() {
		this.bypassServerOnly = true;
		return this;
	}

	public RegistryInfo<T> customRegistryEvent(Supplier<RegistryEventJS<T>> supplier) {
		this.customRegEvent = supplier;
		return this;
	}

	public RegistryInfo<T> noAutoWrap() {
		this.autoWrap = false;
		return this;
	}

	public RegistryInfo<T> languageKeyPrefix(String prefix) {
		this.languageKeyPrefix = prefix;
		return this;
	}

	public void addType(String type, Class<? extends BuilderBase<? extends T>> builderType, BuilderFactory factory, boolean isDefault) {
		var b = new BuilderType<>(type, builderType, factory);
		types.put(type, b);

		if (isDefault) {
			if (defaultType != null) {
				ConsoleJS.STARTUP.warn("Previous default type '" + defaultType.type() + "' for registry '" + key.location() + "' replaced with '" + type + "'!");
			}

			defaultType = b;
		}
		WITH_TYPE.put(key, this);
	}

	public void addType(String type, Class<? extends BuilderBase<? extends T>> builderType, BuilderFactory factory) {
		addType(type, builderType, factory, type.equals("basic"));
	}

	public void addBuilder(BuilderBase<? extends T> builder) {
		if (builder == null) {
			throw new IllegalArgumentException("Can't add null builder in registry '" + key.location() + "'!");
		}
		if (CommonProperties.get().debugInfo) {
			ConsoleJS.STARTUP.info("~ " + key.location() + " | " + builder.id);
		}
		if (objects.containsKey(builder.id)) {
			throw new IllegalArgumentException("Duplicate key '" + builder.id + "' in registry '" + key.location() + "'!");
		}

		objects.put(builder.id, builder);
		ALL_BUILDERS.add(builder);
	}

	@Nullable
	public BuilderType getDefaultType() {
		if (types.isEmpty()) {
			return null;
		} else if (defaultType == null) {
			defaultType = types.values().iterator().next();
		}

		return defaultType;
	}

	@Override
	public int hashCode() {
		return key.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this || obj instanceof RegistryInfo ri && key.equals(ri.key);
	}

	@Override
	public String toString() {
		return key.location().toString();
	}

	public int registerArch() {
		return registerObjects(this.getArchRegistry()::registerSupplied);
	}

	public int registerObjects(RegistryCallback<T> function) {
		if (CommonProperties.get().debugInfo) {
			if (objects.isEmpty()) {
				KubeJS.LOGGER.info("Skipping {} registry", this);
			} else {
				KubeJS.LOGGER.info("Building {} objects of {} registry", objects.size(), this);
			}
		}

		if (objects.isEmpty()) {
			return 0;
		}

		int added = 0;

		for (var builder : this) {
			if (builder.dummyBuilder || (!builder.getRegistryType().bypassServerOnly && CommonProperties.get().serverOnly)) {
				continue;
			}
			function.accept(builder.id, builder::createTransformedObject);

			if (CommonProperties.get().debugInfo) {
				ConsoleJS.STARTUP.info("+ " + this + " | " + builder.id);
			}

			added++;
		}

		if (!objects.isEmpty() && CommonProperties.get().debugInfo) {
			KubeJS.LOGGER.info("Registered {}/{} objects of {}", added, objects.size(), this);
		}

		return added;
	}

	@NotNull
	@Override
	public Iterator<BuilderBase<? extends T>> iterator() {
		return objects.values().iterator();
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public me.shedaniel.architectury.registry.Registry<T> getArchRegistry() {
		if (archRegistry == null) {
			archRegistry = REGISTRIES.get((ResourceKey) key);
		}
		return archRegistry;
	}

	public Registry<T> getVanillaRegistry() {
		return Registry.REGISTRY.get((ResourceKey) key);
	}

	public Set<Map.Entry<ResourceKey<T>, T>> entrySet() {
		return getArchRegistry().entrySet();
	}

	public ResourceLocation getId(T value) {
		return getArchRegistry().getId(value);
	}

	public T getValue(ResourceLocation id) {
		return getArchRegistry().get(id);
	}

	public boolean hasValue(ResourceLocation id) {
		return getArchRegistry().contains(id);
	}

	@Override
	public T wrap(Object o) {
		if (o == null) {
			return null;
		} else if (objectBaseClass.isInstance(o)) {
			return (T) o;
		}

		var id = UtilsJS.getMCID(o);
		var value = getValue(id);

		if (value == null) {
			var npe = new NullPointerException(String.format("No such element with id %s in registry %s!",id, this));
			ConsoleJS.STARTUP.error("Error while wrapping registry element type!", npe);
			throw npe;
		}

		return value;
	}

	public void fireRegistryEvent() {
		var event = customRegEvent == null
				?new RegistryEventJS<>(this)
				:customRegEvent.get();
		event.post(ScriptType.STARTUP, key.location().getPath() + KubeJSEvents.REGISTRY_SUFFIX);
		event.created.forEach(BuilderBase::createAdditionalObjects);
	}
}

