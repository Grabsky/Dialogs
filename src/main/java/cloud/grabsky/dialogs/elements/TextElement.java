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

import cloud.grabsky.dialogs.DialogElement;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
public final class TextElement implements DialogElement {

    public TextElement(final TextElement.Channel channel, final String value, final int ticksToWait) {
        this.channel = channel;
        this.value = value;
        this.ticksToWait = ticksToWait;
    }

    /**
     * Channel to use when forwarding this instance of {@link AnimatedTextElement}.
     */
    @Getter(AccessLevel.PUBLIC)
    private final TextElement.Channel channel;

    /**
     * Dialog {@link String} encoded using {@link MiniMessage} serializer.
     */
    @Getter(AccessLevel.PUBLIC)
    private final String value;

    /**
     * Pause to wait after displaying this {@link TextElement}. Measured in {@code ticks}.
     */
    @Getter(AccessLevel.PUBLIC)
    private final int ticksToWait;


    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public enum Channel {

        CHAT_MESSAGE("text/chat_message"),
        CHAT_BROADCAST("text/chat_broadcast"),
        ACTIONBAR("text/actionbar");

        private static final Channel[] VALUES = Channel.values();

        private final String identifier;

        public static @NotNull Channel fromIdentifier(final @NotNull String identifier) throws IllegalArgumentException {
            // Returning first matching enum.
            for (final Channel channel : VALUES)
                if (channel.identifier.equalsIgnoreCase(identifier) == true)
                    return channel;
            // Returning null in case nothing has been found.
            throw new IllegalArgumentException(identifier);
        }

    }

}
