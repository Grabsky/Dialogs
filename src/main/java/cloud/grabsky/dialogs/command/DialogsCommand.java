/*
 * Dialogs (https://github.com/Grabsky/Dialogs)
 *
 * Copyright (C) 2024  Grabsky <michal.czopek.foss@proton.me>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License v3 as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License v3 for more details.
 */
package cloud.grabsky.dialogs.command;

import cloud.grabsky.bedrock.components.Message;
import cloud.grabsky.commands.ArgumentQueue;
import cloud.grabsky.commands.RootCommand;
import cloud.grabsky.commands.RootCommandContext;
import cloud.grabsky.commands.RootCommandInput;
import cloud.grabsky.commands.annotation.Command;
import cloud.grabsky.commands.annotation.Dependency;
import cloud.grabsky.commands.component.CompletionsProvider;
import cloud.grabsky.commands.component.ExceptionHandler;
import cloud.grabsky.commands.exception.CommandLogicException;
import cloud.grabsky.commands.exception.MissingInputException;
import cloud.grabsky.dialogs.Dialog;
import cloud.grabsky.dialogs.Dialogs;
import cloud.grabsky.dialogs.configuration.PluginLocale;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

@Command(name = "dialogs", permission = "dialogs.command.dialogs", usage = "/dialogs (...)")
public final class DialogsCommand extends RootCommand {

    @Dependency
    private @UnknownNullability Dialogs plugin;


    private static final ExceptionHandler.Factory DIALOGS_SEND_USAGE = (exception) -> {
        if (exception instanceof MissingInputException)
            return (ExceptionHandler<CommandLogicException>) (e, context) -> Message.of(PluginLocale.COMMAND_DIALOGS_SEND_USAGE).send(context.getExecutor());
        // Let other exceptions be handled internally.
        return null;
    };

    @Override
    public @NotNull CompletionsProvider onTabComplete(@NotNull final RootCommandContext context, final int index) throws CommandLogicException {
        final CommandSender sender = context.getExecutor().asCommandSender();
        final RootCommandInput input = context.getInput();
        // Returning list of sub-commands when no argument was specified in the input.
        if (index == 0) return CompletionsProvider.of(
                Stream.of("reload", "send")
                        .filter(literal -> sender.hasPermission(this.getPermission() + "." + literal) == true)
                        .toList()
        );
        // Getting the first literal (argument) of user input.
        final String literal = input.at(1).toLowerCase();
        // Returning empty completions provider when missing permission for that literal.
        if (sender.hasPermission(this.getPermission() + "." + literal) == false)
            return CompletionsProvider.EMPTY;
        // Returning sub-command-aware completions provider.
        return switch (literal) {
            case "send" -> switch (index) {
                case 1 -> CompletionsProvider.of(Player.class);
                case 2 -> CompletionsProvider.of(plugin.getDialogsLoader().getDialogs().keySet());
                default -> CompletionsProvider.EMPTY;
            };
            default -> CompletionsProvider.EMPTY;
        };
    }


    @Override
    public void onCommand(final @NotNull RootCommandContext context, final @NotNull ArgumentQueue arguments) throws CommandLogicException {
        final CommandSender sender = context.getExecutor().asCommandSender();
        // Showing usage when no argument has been provided.
        if (arguments.hasNext() == false) {
            Message.of(PluginLocale.COMMAND_DIALOGS_USAGE).send(sender);
            return;
        }
        // Getting first argument as String.
        final String argument = arguments.next(String.class).asRequired().toLowerCase();
        // Doing stuff based on whatever the first argument is.
        switch (argument) {
            // Handling "/dialogs reload" command...
            case "reload" -> {
                if (sender.hasPermission(this.getPermission() + ".reload") == true) {
                    final boolean isSuccess = plugin.onReload();
                    // Sending message to the sender.
                    Message.of(isSuccess == true ? PluginLocale.COMMAND_DIALOGS_RELOAD_SUCCESS : PluginLocale.COMMAND_DIALOGS_RELOAD_FAILURE).send(sender);
                    return;
                }
                // Sending error message to the sender.
                Message.of(PluginLocale.MISSING_PERMISSIONS).send(sender);
            }
            // Handling "/dialogs send (...)" command...
            case "send" -> {
                if (sender.hasPermission(this.getPermission() + ".send") == true) {
                    final Player target = arguments.next(Player.class).asRequired(DIALOGS_SEND_USAGE);
                    final String dialogIdentifier = arguments.next(String.class).asRequired(DIALOGS_SEND_USAGE);
                    // Getting dialog from specified identifier.
                    final @Nullable Dialog dialog = plugin.getDialogsLoader().getDialogs().get(dialogIdentifier);
                    // Sending error message in case dialog with specified identifier was not found.
                    if (dialog == null || dialog.isEmpty() == true) {
                        Message.of(PluginLocale.COMMAND_DIALOGS_SEND_FAILURE_NOT_FOUND).placeholder("input", dialogIdentifier).send(sender);
                        return;
                    }
                    // Triggering Dialog on specified target.
                    dialog.trigger(target);
                    return;
                }
                // Sending error message to the sender.
                Message.of(PluginLocale.MISSING_PERMISSIONS).send(sender);
            }
            // Showing usage when invalid/unexpected argument has been provided.
            default -> Message.of(PluginLocale.COMMAND_DIALOGS_USAGE).send(sender);
        }
    }
}

