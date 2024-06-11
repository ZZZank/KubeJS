package dev.latvian.kubejs.registry.types;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.util.ConsoleJS;
import dev.latvian.mods.rhino.annotations.typing.JSInfo;
import me.shedaniel.architectury.registry.CreativeTabs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author ZZZank
 */
public class CreativeTabRegistryEventJS extends EventJS {

	@JSInfo("""
			Registered tabs from Vanilla and KubeJS.""")
	public static final Map<ResourceLocation, CreativeModeTab> TABS = new HashMap<>();

	static {
		for (var tab : CreativeModeTab.TABS) {
			TABS.put(new ResourceLocation(tab.getRecipeFolderName()), tab);
		}
	}

	@JSInfo("""
			register a CreativeModeTab. Its namespace will always be `kubejs`
			""")
	public CreativeModeTab create(String id, Supplier<ItemStackJS> icon) {
		var fullId = new ResourceLocation(KubeJS.MOD_ID, id);

		if (TABS.containsKey(fullId)) {
			ConsoleJS.STARTUP.errorf("Tab with id '%s' already registered!", fullId);
			return TABS.get(fullId);
		}

		var tab = CreativeTabs.create(fullId, () -> icon.get().getItemStack());
		TABS.put(fullId, tab);

		return tab;
	}
}
