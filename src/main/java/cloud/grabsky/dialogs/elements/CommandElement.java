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
import cloud.grabsky.dialogs.DialogElement;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class CommandElement implements DialogElement {

    /**
     * Type to use when forwarding this instance of {@link CommandElement}.
     */
    @Getter(AccessLevel.PUBLIC)
    private final CommandElement.Type type;

    /**
     * Command {@link String} to be executed.
     */
    @Getter(AccessLevel.PUBLIC)
    private final String value;

    @Getter(AccessLevel.PUBLIC)
    private final int ticksToWait;

    /**
     * Defines type (executor) of this command.
     */
    // NOTE: Split into two separate types as to not introduce required property that defines command executor. Optional property with default value does not make much sense in that case.
    public enum Type {
        PLAYER_COMMAND, CONSOLE_COMMAND;
    }


    @Internal
    @RequiredArgsConstructor(access = AccessLevel.PUBLIC)
    // NOTE: Field names does not follow Java Naming Convention to provide 1:1 mapping with JSON keys.
    public static final class Init implements LazyInit<CommandElement> {

        // Not an actual JSON field, filled by JsonAdapter based on context.
        private final @NotNull CommandElement.Type type;

        // Nullability cannot be determined because it depends entirely on the end-user.
        public @UnknownNullability String value;
        public @NotNull Integer ticks_to_wait_before_continuing = 1;

        @Override
        public @NotNull CommandElement init() throws IllegalStateException {
            // Throwing an error in case "value" field is invalid.
            if (value == null)
                throw new IllegalStateException("Field \"value\" is required but is either null or has not been found.");
            // Creating and returning element.
            return new CommandElement(type, value, ticks_to_wait_before_continuing);
        }

    }

}
