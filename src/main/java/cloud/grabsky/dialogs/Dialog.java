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
import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Predicate;

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
        int combinedDuration = 0;
        // ...
        for (final DialogElement element : elements) {
            plugin.getBedrockScheduler().repeatAsync(combinedDuration, 2L, element.ticksToWait() / 2, createPredicate(target, element));
            combinedDuration += element.ticksToWait();
        }
    }

    private @NotNull Predicate<Integer> createPredicate(final Player target, final DialogElement element) throws IllegalArgumentException {
        if (element instanceof TextElement textElement) {
            return (iteration) -> {
                // Cancelling when target happen to be offline.
                if (target == null || target.isOnline() == false)
                    return false;
                // Getting the message channel.
                final TextElement.Channel channel = textElement.channel();
                // Preparing the message.
                final Message.StringMessage message = Message.of(textElement.value());
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
            return (iteration) -> {
                // Cancelling when target happen to be offline.
                if (target == null || target.isOnline() == false)
                    return false;
                // ...
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
            return (iteration) -> {
                // Cancelling when target happen to be offline.
                if (target == null || target.isOnline() == false)
                    return false;
                // Scheduling command execution to the main thread.
                plugin.getBedrockScheduler().run(1L, (___) -> {
                    // Preparing command string.
                    final String command = consoleCommand.value().replace("<player>", target.getName());
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
