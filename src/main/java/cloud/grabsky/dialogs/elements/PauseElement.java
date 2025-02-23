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
public final class PauseElement implements DialogElement {

    @Getter(AccessLevel.PUBLIC)
    private final int ticksToWait;

    @Getter(AccessLevel.PUBLIC)
    private final List<Condition> conditions;


    @Internal
    @RequiredArgsConstructor(access = AccessLevel.PUBLIC)
    // NOTE: Field names does not follow Java Naming Convention to provide 1:1 mapping with JSON keys.
    public static final class Init implements LazyInit<PauseElement> {

        // Nullability cannot be determined because it depends entirely on the end-user.
        public @UnknownNullability Integer ticks_to_wait_before_continuing = null;

        // Following field(s) have defaults and can be omitted or defined as null by the end-user.
        public @NotNull List<Condition> conditions = Collections.emptyList();

        @Override
        public @NotNull PauseElement init() throws IllegalStateException {
            // Throwing an error in case "value" field is invalid.
            if (ticks_to_wait_before_continuing == null)
                throw new IllegalStateException("Field \"ticks_to_wait_before_continuing\" is required but is either null or has not been found.");
            // Creating and returning element.
            return new PauseElement(ticks_to_wait_before_continuing, conditions);
        }

    }

}
