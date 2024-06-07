package dev.latvian.kubejs.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.latvian.kubejs.server.ServerEventJS;
import dev.latvian.kubejs.util.ClassWrapper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;

/**
 * @author LatvianModder
 */
public class CommandRegistryEventJS extends ServerEventJS {
	public final CommandDispatcher<CommandSourceStack> dispatcher;
	public final Commands.CommandSelection selection;

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

	public ClassWrapper<Commands> getCommands() {
		return new ClassWrapper<>(Commands.class);
	}

	public ClassWrapper<ArgumentTypeWrapper> getArguments() {
		return new ClassWrapper<>(ArgumentTypeWrapper.class);
	}

	// Used to access the static members of SharedSuggestionProvider
	// can be used within commands like so:
	// [cmd] .suggests((ctx, builder) => event.builtinSuggestions.suggest(["123", "456"], builder))
	public ClassWrapper<SharedSuggestionProvider> getBuiltinSuggestions() {
		return new ClassWrapper<>(SharedSuggestionProvider.class);
	}

}