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
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;

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
        // Used to calculate when next queued task should be started.
        long nextTaskStartsIn = 0;
        // Iterating over all elements in this Dialog and scheduling to display them.
        for (final DialogElement element : elements) {

            if (element instanceof AnimatedTextElement animatedText) {
                final AnimatedTextElement.Channel channel = animatedText.channel();
                final Iterator<Component> frames = animatedText.frames().iterator();
                // Scheduling a new asynchrnous repeat task.
                plugin.getBedrockScheduler().repeatAsync(nextTaskStartsIn, animatedText.refreshRate(), element.ticksToWait() / animatedText.refreshRate(), (iteration) -> {
                    // Cancelling task in case Player connection has been reset.
                    if (target.isConnected() == false)
                        return false;
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
                });
                // Calculating "start" time of the next element. Additionally, refresh rate value is added as to prevent elements from overlapping.
                nextTaskStartsIn += element.ticksToWait() + animatedText.refreshRate();
            }

            else if (element instanceof TextElement textElement) {
                // Parsing the message, setting placeholders if supported.
                final Message.StringMessage message = (Dialogs.isPlaceholderAPI() == true)
                        ? Message.of(PlaceholderAPI.setPlaceholders(target, textElement.value()))
                        : Message.of(textElement.value());
                // Scheduling a new asynchrnous run task.
                plugin.getBedrockScheduler().runAsync(nextTaskStartsIn, (task) -> {
                    // Skipping task execution in case Player connection has been reset.
                    if (target.isConnected() == false)
                        return;
                    // Getting the message channel.
                    final TextElement.Channel channel = textElement.channel();
                    // Sending message based on channel type.
                    switch (channel) {
                        case CHAT_MESSAGE -> message.send(target);
                        case CHAT_BROADCAST -> message.broadcast();
                        case ACTIONBAR -> message.sendActionBar(target);
                    }
                });
                // Calculating "start" time of the next element.
                nextTaskStartsIn += element.ticksToWait();
            }

            else if (element instanceof ConsoleCommandElement consoleCommand) {
                // Scheduling a new run task. Command dispatch have to be called on the main thread.
                plugin.getBedrockScheduler().run(nextTaskStartsIn, (task) -> {
                    // Skipping task execution in case Player connection has been reset.
                    if (target.isConnected() == false)
                        return;
                    // Preparing command string.
                    final String command = (Dialogs.isPlaceholderAPI() == true) ? PlaceholderAPI.setPlaceholders(target, consoleCommand.value()) : consoleCommand.value();
                    // Dispatching the command.
                    plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command);
                });
                // Calculating "start" time of the next element.
                nextTaskStartsIn += element.ticksToWait();
            }

        }
    }

}
