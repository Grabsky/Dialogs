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
package cloud.grabsky.dialogs.elements;

import cloud.grabsky.configuration.util.LazyInit;
import cloud.grabsky.dialogs.Condition;
import cloud.grabsky.dialogs.DialogElement;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
public final class AnimatedActionBarElement implements DialogElement.Animated {

    private static final Pattern TAG_PATTERN = Pattern.compile("(?=<)|(?<=>)");

    public AnimatedActionBarElement(
            final @NotNull AudienceType audience,
            final @NotNull String value,
            final long refreshRate,
            final int minLettersPerFrame,
            final int maxLettersPerFrame,
            final Sound typingSound,
            final boolean lockUntilNextElement,
            final int ticksToWait,
            final List<Condition> conditions
    ) {
        this.audience = audience;
        this.value = value;
        this.refreshRate = refreshRate;
        this.minLettersPerFrame = minLettersPerFrame;
        this.maxLettersPerFrame = maxLettersPerFrame;
        this.typingSound = typingSound;
        this.lockUntilNextElement = lockUntilNextElement;
        this.ticksToWait = ticksToWait;
        this.conditions = conditions;
        // ...
        final ArrayList<Component> frames = new ArrayList<>();
        // ...
        final Random random = new Random();
        // Iterating over splitted dialog, with all MiniMessage tags removed.
        final Iterator<String> iterator = TAG_PATTERN.splitAsStream(value).map(String::new).iterator();
        // ...
        final StringBuilder builder = new StringBuilder();
        // ...
        while (iterator.hasNext() == true) {
            // ...
            final String currentPart = iterator.next();
            // ...
            if (MiniMessage.miniMessage().stripTags(currentPart).isEmpty() == true) {
                frames.add(MiniMessage.miniMessage().deserialize(builder + currentPart).compact());
                // ...
                builder.append(currentPart);
                // ...
                continue;
            }
            // Normal text...
            int num = (minLettersPerFrame < maxLettersPerFrame) ? random.nextInt(minLettersPerFrame, maxLettersPerFrame) : maxLettersPerFrame;
            // ...
            int charsProcessed = 0;
            // ...
            while (charsProcessed < currentPart.length()) {
                final String currentPartRandomized = currentPart.substring(charsProcessed, Math.min(currentPart.length(), charsProcessed + num));
                // ...
                frames.add(MiniMessage.miniMessage().deserialize(builder + currentPartRandomized).compact());
                // ...
                builder.append(currentPartRandomized);
                charsProcessed += num;
            }
        }
        // ...
        this.frames = frames;
    }

    /**
     * Audience to forward this message to.
     */
    @Getter(AccessLevel.PUBLIC)
    private final transient AudienceType audience;

    /**
     * Dialog {@link String} encoded using {@link MiniMessage} serializer.
     */
    @Getter(AccessLevel.PUBLIC)
    private final @NotNull String value;

    /* AnimatedDialogElement */

    @Getter(AccessLevel.PUBLIC)
    private final long refreshRate;

    @Getter(AccessLevel.PUBLIC)
    private final int minLettersPerFrame;

    @Getter(AccessLevel.PUBLIC)
    private final int maxLettersPerFrame;

    @Getter(AccessLevel.PUBLIC)
    private final Sound typingSound;

    @Getter(AccessLevel.PUBLIC)
    private final boolean lockUntilNextElement;

    @Getter(AccessLevel.PUBLIC)
    private transient final List<Component> frames;

    @Getter(AccessLevel.PUBLIC)
    private final int ticksToWait;

    @Getter(AccessLevel.PUBLIC)
    private final List<Condition> conditions;


    /**
     * Defines support audience types to forward the {@link AnimatedActionBarElement} to.
     */
    public enum AudienceType {
        PLAYER, SERVER;
    }


    @Internal
    @RequiredArgsConstructor(access = AccessLevel.PUBLIC)
    // NOTE: Field names does not follow Java Naming Convention to provide 1:1 mapping with JSON keys.
    public static final class Init implements LazyInit<AnimatedActionBarElement> {

        // Following field(s) have defaults and can be omitted or definhed as null by the end-user.
        public @NotNull AudienceType audience = AudienceType.PLAYER;

        // Nullability cannot be determined because it depends entirely on the end-user.
        public @UnknownNullability String value;

        // Following field(s) have defaults and can be omitted or defined as null by the end-user.
        public @NotNull Long refresh_rate = 2L;
        public @NotNull Integer min_letters_per_frame = 2;
        public @NotNull Integer max_letters_per_frame = 3;
        public @NotNull Sound typing_sound = Sound.sound(Key.key("block.note_block.hat"), Sound.Source.MASTER, 1.0f, 1.5f);
        public @NotNull Boolean lock_until_next_element = true;
        public @NotNull List<Condition> conditions = Collections.emptyList();

        // Nullability cannot be determined because it depends entirely on the end-user.
        public @UnknownNullability Integer ticks_to_wait_before_continuing;

        @Override
        public @NotNull AnimatedActionBarElement init() throws IllegalStateException {
            // Throwing an error in case "value" field is invalid.
            if (value == null)
                throw new IllegalStateException("Field \"value\" is required but is either null or has not been found.");
            // Throwing an error in case "ticks_to_wait_before_continuing" field is invalid.
            if (ticks_to_wait_before_continuing == null)
                throw new IllegalStateException("Field \"ticks_to_wait_before_continuing\" is required but is either null or has not been found.");
            // Creating and returning element.
            return new AnimatedActionBarElement(audience, value, refresh_rate, min_letters_per_frame, max_letters_per_frame, typing_sound, lock_until_next_element, ticks_to_wait_before_continuing, conditions);
        }

    }

}
