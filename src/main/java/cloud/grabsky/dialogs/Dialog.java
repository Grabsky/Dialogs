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
import cloud.grabsky.dialogs.elements.AnimatedActionBarElement;
import cloud.grabsky.dialogs.elements.CommandElement;
import cloud.grabsky.dialogs.elements.MessageElement;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public final class Dialog implements Collection<DialogElement> {

    private static final Dialogs plugin = Dialogs.getInstance();

    private static final String LAST_DIALOG_KEY = "last_dialog";

    @Delegate @Getter(AccessLevel.PUBLIC)
    private final Collection<DialogElement> elements;

    public void trigger(final @NotNull Player target) {
        final String dialogIdentifier = UUID.randomUUID().toString();
        // Updating dialog.
        target.setMetadata(LAST_DIALOG_KEY, new FixedMetadataValue(plugin, dialogIdentifier));
        // Used to calculate when next queued task should be started.
        long nextTaskStartsIn = 0;
        // Iterating over all elements in this Dialog and scheduling to display them.
        for (final DialogElement element : elements) {

            if (element instanceof AnimatedActionBarElement animatedActionBar) {
                final Iterator<Component> frames = animatedActionBar.frames().iterator();
                // Scheduling a new asynchrnous repeat task.
                plugin.getBedrockScheduler().repeatAsync(nextTaskStartsIn, animatedActionBar.refreshRate(), element.ticksToWait() / animatedActionBar.refreshRate(), (iteration) -> {
                    // Cancelling task in case Player connection has been reset OR other dialog has been started in the meanwhile.
                    if (isDialogStillValid(target, dialogIdentifier) == false)
                        return false;
                    // Preparing the message Component.
                    final Component component = (frames.hasNext() == true)
                            ? frames.next()
                            : (animatedActionBar.lockUntilNextElement() == true)
                                    ? animatedActionBar.lastFrame()
                                    : null;
                    // Currently only action bar messages can be "animated".
                    Message.of(component).sendActionBar(target);
                    // Playing the animation sound.
                    if (frames.hasNext() == true)
                        target.playSound(target, Sound.BLOCK_NOTE_BLOCK_HAT, 1.0F, 0.1F);
                    // Continuing... Should be cancelled automatically when max iterations is reached.
                    return true;
                });
                // Calculating "start" time of the next element. Additionally, refresh rate value is added as to prevent elements from overlapping.
                nextTaskStartsIn += element.ticksToWait() + animatedActionBar.refreshRate();
            }

            else if (element instanceof MessageElement messageElement) {
                // Parsing the message, setting placeholders if supported.
                final Message.StringMessage message = (Dialogs.isPlaceholderAPI() == true)
                        ? Message.of(PlaceholderAPI.setPlaceholders(target, messageElement.value()))
                        : Message.of(messageElement.value());
                // Scheduling a new asynchrnous run task.
                plugin.getBedrockScheduler().runAsync(nextTaskStartsIn, (task) -> {
                    // Cancelling task in case Player connection has been reset OR other dialog has been started in the meanwhile.
                    if (isDialogStillValid(target, dialogIdentifier) == false)
                        return;
                    // Getting the message audience type.
                    final MessageElement.AudienceType audience = messageElement.audience();
                    // Getting the actual audience.
                    final Audience actualAudience = switch (audience) {
                        case PLAYER -> target;
                        case CONSOLE -> Bukkit.getConsoleSender();
                        case SERVER -> Bukkit.getServer();
                    };
                    // Getting the message type.
                    final MessageElement.Type type = messageElement.type();
                    // Sending message based in specific type.
                    switch (type) {
                        case CHAT_MESSAGE -> message.send(actualAudience);
                        case ACTIONBAR_MESSAGE -> message.sendActionBar(actualAudience);
                    }
                });
                // Calculating "start" time of the next element.
                nextTaskStartsIn += element.ticksToWait();
            }

            else if (element instanceof CommandElement commandElement) {
                // Scheduling a new run task. Command dispatch have to be called on the main thread.
                plugin.getBedrockScheduler().run(nextTaskStartsIn, (task) -> {
                    // Cancelling task in case Player connection has been reset OR other dialog has been started in the meanwhile.
                    if (isDialogStillValid(target, dialogIdentifier) == false)
                        return;
                    // Preparing command string.
                    final String command = (Dialogs.isPlaceholderAPI() == true) ? PlaceholderAPI.setPlaceholders(target, commandElement.value()) : commandElement.value();
                    // Getting the command sender.
                    final CommandSender sender = (commandElement.type() == CommandElement.Type.PLAYER_COMMAND) ? target : plugin.getServer().getConsoleSender();
                    // Dispatching the command.
                    plugin.getServer().dispatchCommand(sender, command);
                });
                // Calculating "start" time of the next element.
                nextTaskStartsIn += element.ticksToWait();
            }

        }
    }

    // Returns false in case Player connection has been reset OR other dialog has been started in the meanwhile.
    private static boolean isDialogStillValid(final @NotNull Player target, final @NotNull String dialogIdentifier) {
        return target.isConnected() == true && (target.getMetadata(LAST_DIALOG_KEY).isEmpty() == true || target.getMetadata(LAST_DIALOG_KEY).get(0).asString().equalsIgnoreCase(dialogIdentifier) == true);
    }

}
