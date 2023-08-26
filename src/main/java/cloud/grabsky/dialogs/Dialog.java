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
package cloud.grabsky.dialogs;

import cloud.grabsky.bedrock.components.Message;
import cloud.grabsky.dialogs.elements.AnimatedTextElement;
import cloud.grabsky.dialogs.elements.ConsoleCommandElement;
import cloud.grabsky.dialogs.elements.TextElement;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiPredicate;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public final class Dialog implements Collection<DialogElement> {

    private static final Dialogs plugin = Dialogs.getInstance();

    @Delegate @Getter(AccessLevel.PUBLIC)
    private final Collection<DialogElement> elements;

    public void trigger(final @NotNull Player target) {
        final List<BukkitTask> queue = new ArrayList<>();
        // Used to calculate when next queued task should be started.
        long nextTaskStartsIn = 0;
        // Iterating over all elements in this Dialog and scheduling to display them.
        for (final DialogElement element : elements) {
            if (element instanceof AnimatedTextElement animatedText) {
                // Scheduling and adding task to the tasks list.
                queue.add(plugin.getBedrockScheduler().repeatAsync(
                        nextTaskStartsIn,
                        animatedText.refreshRate(),
                        element.ticksToWait() / animatedText.refreshRate(),
                        createPredicate(queue, target, element))
                );
                // ...
                nextTaskStartsIn += element.ticksToWait() + animatedText.refreshRate(); // Two additional ticks had to be added, possibly due to a bug. Will be investigated later on.
                // Continuing...
                continue;
            }
            // Scheduling and adding task to the tasks list.
            queue.add(plugin.getBedrockScheduler().repeatAsync(nextTaskStartsIn, 2L, element.ticksToWait() / 2, createPredicate(queue, target, element)));
            // ...
            nextTaskStartsIn += element.ticksToWait() + 2L; // Two additional ticks had to be added, possibly due to a bug. Will be investigated later on.
        }
    }

    private @NotNull BiPredicate<BukkitRunnable, Integer> createPredicate(final List<BukkitTask> queue, final Player target, final DialogElement element) throws IllegalArgumentException {
        if (element instanceof TextElement textElement) {
            // Parsing the message, setting placeholders if supported.
            final Message.StringMessage message = (Dialogs.isPlaceholderAPI() == true)
                    ? Message.of(PlaceholderAPI.setPlaceholders(target, textElement.value()))
                    : Message.of(textElement.value());
            // Returning...
            return (runnable, iteration) -> {
                // Cancelling all remaining tasks when target happen to be offline.
                if (target == null || target.isOnline() == false) {
                    // Cancelling all OTHER tasks.
                    queue.stream().filter(queuedTask -> queuedTask.getTaskId() != runnable.getTaskId()).forEach(BukkitTask::cancel);
                    // Cancelling THIS task.
                    return false;
                }
                // Getting the message channel.
                final TextElement.Channel channel = textElement.channel();
                // Sending message based on channel type.
                switch (channel) {
                    case CHAT_MESSAGE -> message.send(target);
                    case CHAT_BROADCAST -> message.broadcast();
                    case ACTIONBAR -> {
                        message.sendActionBar(target);
                        // Returning true when TextElement#lockUntilNextElement is true. Makes the task re-execute until iterations limit is reached.
                        return textElement.lockUntilNextElement() == true;
                    }
                }
                // Exiting...
                return false;
            };
        } else if (element instanceof AnimatedTextElement animatedText) {
            final AnimatedTextElement.Channel channel = animatedText.channel();
            final Iterator<Component> frames = animatedText.frames().iterator();
            // Returning...
            return (runnable, iteration) -> {
                // Cancelling all remaining tasks when target happen to be offline.
                if (target == null || target.isOnline() == false) {
                    // Cancelling all OTHER tasks.
                    queue.stream().filter(queuedTask -> queuedTask.getTaskId() != runnable.getTaskId()).forEach(BukkitTask::cancel);
                    // Cancelling THIS task.
                    return false;
                }
                // Preparing the message Component.
                final Component component = (frames.hasNext() == true)
                        ? frames.next()
                        : (animatedText.lockUntilNextElement() == true)
                                ? animatedText.lastFrame()
                                : null;
                // Currently only action bar messages can be "animated".
                switch (channel) {
                    case ACTIONBAR -> Message.of(component).sendActionBar(target);
                }
                if (frames.hasNext() == true)
                    target.playSound(target, Sound.BLOCK_NOTE_BLOCK_HAT, 1.0F, 0.1F);
                // Continuing... Should be cancelled automatically when max iterations is reached.
                return true;
            };
        } else if (element instanceof ConsoleCommandElement consoleCommand) {
            // Returning...
            return (runnable, iteration) -> {
                // Cancelling all remaining tasks when target happen to be offline.
                if (target == null || target.isOnline() == false) {
                    // Cancelling all OTHER tasks.
                    queue.stream().filter(queuedTask -> queuedTask.getTaskId() != runnable.getTaskId()).forEach(BukkitTask::cancel);
                    // Cancelling THIS task.
                    return false;
                }
                // Scheduling command execution to the main thread.
                plugin.getBedrockScheduler().run(1L, (___) -> {
                    // Preparing command string.
                    final String command = (Dialogs.isPlaceholderAPI() == true) ? PlaceholderAPI.setPlaceholders(target, consoleCommand.value()) : consoleCommand.value();
                    // Dispatching the command.
                    plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command);
                });
                // Exiting...
                return false;
            };
        }
        // ...
        throw new IllegalArgumentException("Dialog element instance of class \"" + element.getClass().getSimpleName() + "\" is not supported.");
    }

}
