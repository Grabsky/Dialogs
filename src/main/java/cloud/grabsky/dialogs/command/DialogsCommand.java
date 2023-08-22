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
import cloud.grabsky.dialogs.Dialogs;
import cloud.grabsky.dialogs.configuration.PluginDialogs;
import cloud.grabsky.dialogs.configuration.PluginLocale;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
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
                Stream.of("send")
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
                case 3 -> CompletionsProvider.of("40");
                default -> CompletionsProvider.EMPTY;
            };
            // ...
            default -> CompletionsProvider.EMPTY;
        };
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
            case "send" -> {
                final Player target = arguments.next(Player.class).asRequired(DIALOGS_SEND_USAGE);
                final String dialogIdentifier = arguments.next(String.class).asRequired(DIALOGS_SEND_USAGE);
                final int durationTicks = arguments.next(Integer.class).asRequired(DIALOGS_SEND_USAGE);
                // ...
                final @Nullable String dialog = PluginDialogs.DIALOGS.get(dialogIdentifier);
                // Sending error message in case dialog with specified identifier was not found.
                if (dialog == null) {
                    Message.of(PluginLocale.COMMAND_DIALOGS_SEND_FAILURE_NOT_FOUND).placeholder("input", dialogIdentifier).send(sender);
                    return;
                }
                // Creating an ArrayList to store the dialog parts in.
                final List<String> result = new ArrayList<>();
                // Creating a Matcher from pattern to select all MiniMessage tags.
                final Matcher matcher =  TAG_PATTERN.matcher(dialog);
                // Iterating over splitted dialog, with all MiniMessage tags removed.
                // This should create something like that ["<_TAG_>H", "e", "l", "l", "o", " ", "<red>W", "o, ...]
                TAG_PATTERN.splitAsStream(dialog).forEach(str -> {
                    // Adding each character of a dialog split to the result list.
                    for (final char c : str.toCharArray()) {
                        result.add(c + "");
                    }
                    // Adding each MiniMessage tag to the result list.
                    if (matcher.find() == true)
                        result.add(matcher.group());
                });
                // Creating a Random with array size as a seed. That way "type animation" will not be randomized for the same dialog called multiple times.
                final Random random = new Random(result.size());
                // Getting the iterator for the result.
                final Iterator<String> iterator = result.iterator();
                // ...
                final StringBuilder builder = new StringBuilder();
                // Calculating extended iterations count, this is (duration_ticks) / (task_repeat_period).
                final AtomicInteger extendedIterationsCount = new AtomicInteger(durationTicks / 2);
                // Scheduling a new repeat task to run every 2 ticks until result list is fully consumed.
                plugin.getBedrockScheduler().repeat(0L, 2L, result.size(), (task) -> {
                    // In case iterator has reached the end, action bar is now sent for n more iterations. Unfortunately there is no way to specify it's duration. (protocol does not support that)
                    if (iterator.hasNext() == false) {
                        // Extending duration of action bar being shown to the player.
                        if (extendedIterationsCount.addAndGet(-1) > 0) {
                            Message.of(builder.toString()).placeholder("player", target).sendActionBar(target);
                            return true;
                        }
                        // Cancelling the task when extended iteration count has finished.
                        return false;
                    }
                    // ...
                    for (int i = 0; i < random.nextInt(2, 4); i++) {
                        final String next = iterator.next();
                        // ...
                        builder.append(next);
                        // ...
                        Message.of(builder.toString()).placeholder("player",target).sendActionBar(target);
                    }
                    return true;
                });
            }
        }
    }
}

