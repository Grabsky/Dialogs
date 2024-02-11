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
