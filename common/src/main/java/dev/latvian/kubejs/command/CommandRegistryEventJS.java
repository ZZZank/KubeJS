package dev.latvian.kubejs.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.latvian.kubejs.server.ServerEventJS;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

/**
 * @author LatvianModder
 */
public class CommandRegistryEventJS extends ServerEventJS {
	private final CommandDispatcher<CommandSourceStack> dispatcher;
	private final Commands.CommandSelection selection;

	public CommandRegistryEventJS(CommandDispatcher<CommandSourceStack> dispatcher, Commands.CommandSelection selection) {
		this.dispatcher = dispatcher;
		this.selection = selection;
	}

	public boolean isSinglePlayer() {
		return selection == Commands.CommandSelection.ALL || selection == Commands.CommandSelection.INTEGRATED;
	}

	public CommandDispatcher<CommandSourceStack> getDispatcher() {
		return dispatcher;
	}

	public LiteralCommandNode<CommandSourceStack> register(final LiteralArgumentBuilder<CommandSourceStack> command) {
		return dispatcher.register(command);
	}

	public static LiteralArgumentBuilder<CommandSourceStack> literal(final String name) {
		return LiteralArgumentBuilder.literal(name);
	}

	public static  <T> RequiredArgumentBuilder<Object, T> argument(String name, ArgumentType<T> argumentType) {
		return RequiredArgumentBuilder.argument(name, argumentType);
	}
}