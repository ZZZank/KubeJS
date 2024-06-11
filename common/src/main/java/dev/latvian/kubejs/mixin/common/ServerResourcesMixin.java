package dev.latvian.kubejs.mixin.common;

import dev.latvian.kubejs.server.ServerScriptManager;
import net.minecraft.server.ServerResources;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

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
		//bad injection practice, but we need to make injection point targeting somewhere before `this.commands = new Commands(arg);` in `ServerResources.<init>`
		ServerScriptManager.instance = new ServerScriptManager();
		ServerScriptManager.instance.init((ServerResources) (Object) this);
		ServerScriptManager.instance.loadFromDirectory();
		return arg;
	}

	@ModifyArg(method = "loadResources", at = @At(value = "INVOKE", ordinal = 0,
			target = "Lnet/minecraft/server/packs/resources/ReloadableResourceManager;reload(Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;Ljava/util/List;Ljava/util/concurrent/CompletableFuture;)Ljava/util/concurrent/CompletableFuture;"),
			index = 2)
	private static List<PackResources> resourcePackList(List<PackResources> list) {
		return ServerScriptManager.instance.resourcePackList(list);
	}
}
