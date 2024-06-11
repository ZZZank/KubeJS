package dev.latvian.kubejs.mixin.common;

import dev.latvian.kubejs.server.ServerScriptManager;
import net.minecraft.server.ServerResources;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

/**
 * @author LatvianModder
 */
@Mixin(ServerResources.class)
public abstract class ServerResourcesMixin implements AutoCloseable {
	@ModifyArg(
			method = "<init>",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/packs/resources/SimpleReloadableResourceManager;<init>(Lnet/minecraft/server/packs/PackType;)V")
	)
	private PackType init(PackType arg) {
		//bad injection practice, but we need to make injection point targeting somewhere not "<init>"
		ServerScriptManager.instance.init((ServerResources) (Object) this);
		return arg;
	}

	@ModifyVariable(method = "loadResources", at = @At(value = "HEAD", ordinal = 0), argsOnly = true)
	private static List<PackResources> resourcePackList(List<PackResources> list) {
		ServerScriptManager.instance = new ServerScriptManager();
		return ServerScriptManager.instance.resourcePackList(list);
	}
}
