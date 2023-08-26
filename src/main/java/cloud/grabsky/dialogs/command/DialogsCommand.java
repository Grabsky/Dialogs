/*
 * MIT License
 *
 * Copyright (c) 2023 Grabsky <44530932+Grabsky@users.noreply.github.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * HORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
import cloud.grabsky.dialogs.configuration.PluginDialogs;
import cloud.grabsky.dialogs.configuration.PluginLocale;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.stream.Stream;

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
                case 2 -> CompletionsProvider.of(PluginDialogs.DIALOGS.keySet());
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
            // Showing usage when invalid/unexpected argument has been provided.
            default -> Message.of(PluginLocale.COMMAND_DIALOGS_USAGE).send(sender);
            // Handling "/dialogs reload" command...
            case "reload" -> {
                if (sender.hasPermission(this.getPermission() + ".reload") == true) {
                    final boolean isSuccess = plugin.reloadConfiguration();
                    // Sending message to the sender.
                    Message.of(isSuccess == true ? PluginLocale.COMMAND_DIALOGS_RELOAD_SUCCESS : PluginLocale.COMMAND_DIALOGS_RELOAD_FAILURE).send(sender);
                    return;
                }
                // Sending error message to the sender.
                Message.of(PluginLocale.Commands.MISSING_PERMISSIONS).send(sender);
            }
            // Handling "/dialogs send (...)" command...
            case "send" -> {
                if (sender.hasPermission(this.getPermission() + ".send") == true) {
                    final Player target = arguments.next(Player.class).asRequired(DIALOGS_SEND_USAGE);
                    final String dialogIdentifier = arguments.next(String.class).asRequired(DIALOGS_SEND_USAGE);
                    // Getting dialog from specified identifier.
                    final @Nullable Dialog dialog = PluginDialogs.DIALOGS.get(dialogIdentifier);
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
                Message.of(PluginLocale.Commands.MISSING_PERMISSIONS).send(sender);
            }
        }
    }
}

