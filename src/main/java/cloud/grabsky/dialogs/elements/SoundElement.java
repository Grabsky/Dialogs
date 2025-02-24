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
import net.kyori.adventure.sound.Sound;

import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public final class SoundElement implements DialogElement {

    /**
     * Audience to forward this message to.
     */
    @Getter(AccessLevel.PUBLIC)
    private final transient AudienceType audience;

    /**
     * List of sounds to be played.
     */
    @Getter(AccessLevel.PUBLIC)
    private final List<Sound> value;

    @Getter(AccessLevel.PUBLIC)
    private final int ticksToWait;

    @Getter(AccessLevel.PUBLIC)
    private final List<Condition> conditions;


    /**
     * Defines support audience types to forward the {@link SoundElement} to.
     */
    public enum AudienceType {
        PLAYER, SERVER;
    }


    @Internal
    @RequiredArgsConstructor(access = AccessLevel.PUBLIC)
    // NOTE: Field names does not follow Java Naming Convention to provide 1:1 mapping with JSON keys.
    public static final class Init implements LazyInit<SoundElement> {

        // Following field(s) have defaults and can be omitted or defined as null by the end-user.
        public @NotNull AudienceType audience = AudienceType.PLAYER;

        // Nullability cannot be determined because it depends entirely on the end-user.
        public @UnknownNullability List<Sound> value;

        // Following field(s) have defaults and can be omitted or defined as null by the end-user.
        public @NotNull Integer ticks_to_wait_before_continuing = 1;
        public @NotNull List<Condition> conditions = Collections.emptyList();


        @Override
        public @NotNull SoundElement init() throws IllegalStateException {
            // Throwing an error in case "value" field is invalid.
            if (value == null)
                throw new IllegalStateException("Field \"value\" is required but is either null or has not been found.");
            // Creating and returning element.
            return new SoundElement(audience, value, ticks_to_wait_before_continuing, conditions);
        }

    }

}