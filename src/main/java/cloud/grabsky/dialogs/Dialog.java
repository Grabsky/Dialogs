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
package cloud.grabsky.dialogs;

import cloud.grabsky.bedrock.components.Message;
import cloud.grabsky.dialogs.elements.AnimatedActionBarElement;
import cloud.grabsky.dialogs.elements.CommandElement;
import cloud.grabsky.dialogs.elements.MessageElement;
import cloud.grabsky.dialogs.elements.PauseElement;
import cloud.grabsky.dialogs.elements.SoundElement;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;

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

            // Skipping execution of the element in case any of the conditions is not met.
            if (element.conditions().stream().anyMatch(condition -> condition.testCondition(target) == false) == true)
                continue;

            if (element instanceof AnimatedActionBarElement animatedActionBar) {
                final Iterator<Component> frames = animatedActionBar.frames().iterator();
                // Scheduling a new asynchronous repeat task.
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
                    if (frames.hasNext() == true && animatedActionBar.typingSound() != null && animatedActionBar.typingSound().volume() > 0.0f)
                        target.playSound(animatedActionBar.typingSound());
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
                // Scheduling a new asynchronous run task.
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
                    // Setting placeholders in commands.
                    final List<String> commands = (Dialogs.isPlaceholderAPI() == true)
                            ? commandElement.value().stream().map(command -> PlaceholderAPI.setPlaceholders(target, command)).toList()
                            : commandElement.value();
                    // Getting the command sender.
                    final CommandSender sender = (commandElement.type() == CommandElement.Type.PLAYER_COMMAND) ? target : plugin.getServer().getConsoleSender();
                    // Dispatching commands.
                    commands.forEach(command -> plugin.getServer().dispatchCommand(sender, command));
                });
                // Calculating "start" time of the next element.
                nextTaskStartsIn += element.ticksToWait();
            }

            else if (element instanceof SoundElement soundElement) {
                // Scheduling a new run task. Command dispatch have to be called on the main thread.
                plugin.getBedrockScheduler().run(nextTaskStartsIn, (task) -> {
                    // Cancelling task in case Player connection has been reset OR other dialog has been started in the meanwhile.
                    if (isDialogStillValid(target, dialogIdentifier) == false)
                        return;
                    // Getting the audience.
                    final Audience audience = (soundElement.audience() == SoundElement.AudienceType.PLAYER) ? target : plugin.getServer();
                    // Playing sounds.
                    soundElement.value().forEach(audience::playSound);
                });
                // Calculating "start" time of the next element.
                nextTaskStartsIn += element.ticksToWait();
            }

            else if (element instanceof PauseElement pauseElement) {
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
