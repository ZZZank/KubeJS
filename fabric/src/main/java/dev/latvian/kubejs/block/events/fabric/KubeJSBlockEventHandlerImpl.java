package dev.latvian.kubejs.block.events.fabric;

import dev.latvian.kubejs.block.events.KubeJSBlockEventHandler;
import dev.latvian.kubejs.fluid.FluidBuilder;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * @see KubeJSBlockEventHandler
 */
public class KubeJSBlockEventHandlerImpl {
	public static LiquidBlock buildFluidBlock(FluidBuilder builder, BlockBehaviour.Properties properties) {
		return new LiquidBlock(builder.stillFluid, properties) {
		};
	}
}
