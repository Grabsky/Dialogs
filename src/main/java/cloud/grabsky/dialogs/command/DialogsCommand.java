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
import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Command(name = "dialogs", permission = "dialogs.command.dialogs")
public final class DialogsCommand extends RootCommand {

    @Dependency
    private @UnknownNullability Dialogs plugin;


    private static final Pattern TAG_PATTERN = Pattern.compile("<[^>]*>");

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
            // ...
            default -> CompletionsProvider.EMPTY;
        };
    }

    public void onDialogsReload(final @NotNull RootCommandContext context, final @NotNull ArgumentQueue arguments) {
        final CommandSender sender = context.getExecutor().asCommandSender();
        // Checking permissions.
        if (sender.hasPermission(this.getPermission() + ".reload") == true) {
            // ...
            return;
        }
        // Sending error message.
        Message.of(PluginLocale.Commands.MISSING_PERMISSIONS).send(sender);
    }

    public void onDialogsSend(final @NotNull RootCommandContext context, final @NotNull ArgumentQueue arguments) {

    }

    @Override
    public void onCommand(final @NotNull RootCommandContext context, final @NotNull ArgumentQueue arguments) throws CommandLogicException {
        final CommandSender sender = context.getExecutor().asCommandSender();
        // ...
        if (arguments.hasNext() == false) {
            Message.of(PluginLocale.COMMAND_DIALOGS_USAGE).send(sender);
            return;
        }
        // ...
        final String argument = arguments.next(String.class).asRequired().toLowerCase();
        // ...
        switch (argument) {
            case "reload" -> {
                plugin.reloadConfiguration();
            }
            case "send" -> {
                final Player target = arguments.next(Player.class).asRequired(DIALOGS_SEND_USAGE);
                final String dialogIdentifier = arguments.next(String.class).asRequired(DIALOGS_SEND_USAGE);
                // ...
                final @Nullable List<Dialog> dialogs = PluginDialogs.DIALOGS.get(dialogIdentifier);
                // Sending error message in case dialog with specified identifier was not found.
                if (dialogs == null || dialogs.isEmpty() == true) {
                    Message.of(PluginLocale.COMMAND_DIALOGS_SEND_FAILURE_NOT_FOUND).placeholder("input", dialogIdentifier).send(sender);
                    return;
                }
                final Iterator<Dialog> dialogsIterator = dialogs.iterator();
                final Dialog initialDialog = dialogsIterator.next();
                // Getting the iterator for the result.
                final AtomicReference<Iterator<Component>> atomicFramesIterator = new AtomicReference<>(initialDialog.getFrames().iterator());
                // Calculating extended iterations count, this is (duration_ticks) / (task_repeat_period).
                final AtomicReference<Dialog> atomicDialog = new AtomicReference<>(initialDialog);
                final AtomicInteger pauseDuration = new AtomicInteger(initialDialog.getPause() / 2);
                // Scheduling a new repeat task to run every 2 ticks until result list is fully consumed.
                plugin.getBedrockScheduler().repeatAsync(0L, 2L, Short.MAX_VALUE, (task) -> {
                    // In case iterator has reached the end, action bar is now sent for n more iterations. Unfortunately there is no way to specify it's duration. (protocol does not support that)
                    if (atomicFramesIterator.get().hasNext() == false) {
                        // Extending duration of action bar being shown to the player.
                        if (pauseDuration.addAndGet(-1) > 0) {
                            Message.of(atomicDialog.get().getLastFrame()).sendActionBar(target);
                            return true;
                        }
                        if (dialogsIterator.hasNext() == true) {
                            atomicDialog.set(dialogsIterator.next());
                            pauseDuration.set(atomicDialog.get().getPause() / 2);
                            atomicFramesIterator.set(atomicDialog.get().getFrames().iterator());
                            return true;
                        }
                        // Cancelling the task when extended iteration count has finished.
                        return false;
                    }
                    // ...
                    final Component next = atomicFramesIterator.get().next();
                    // ...
                    Message.of(next).sendActionBar(target);
                    target.playSound(target, Sound.BLOCK_NOTE_BLOCK_HAT, 1.0F, 0.1F);
                    // ...
                    return true;
                });
            }
        }
    }
}

