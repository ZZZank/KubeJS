package dev.latvian.kubejs.registry.types.tab;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.mods.rhino.annotations.typing.JSInfo;
import net.minecraft.world.item.CreativeModeTab;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ZZZank
 */
public class KjsTabs {
    @JSInfo("""
        Registered tabs from Vanilla and KesseractJS.
        
        Vanilla tabs are indexed by their recipe folder name.
        
        Tabs registered by us will be indexed by `kubejs.{id}`.
        """)
    public static final Map<String, CreativeModeTab> TABS = new HashMap<>();

    static {
        for (var tab : CreativeModeTab.TABS) {
            TABS.put(tab.getRecipeFolderName(), tab);
        }
    }

    public static CreativeModeTab get(String id) {
        var tab = TABS.get(id);
        if (tab == null) {
            tab = KjsTabs.TABS.get(KubeJS.MOD_ID + '.' + id);
        }
        return tab;
    }
}
