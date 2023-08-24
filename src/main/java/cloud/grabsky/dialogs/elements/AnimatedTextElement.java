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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
public final class AnimatedTextElement implements DialogElement {

    private static final Pattern TAG_PATTERN = Pattern.compile("(?=<)|(?<=>)");

    public AnimatedTextElement(final AnimatedTextElement.Channel channel, final String value, final boolean isLockOnLastFrame, final int ticksToWait) {
        this.channel = channel;
        this.value = value;
        this.isLockOnLastFrame = isLockOnLastFrame;
        this.ticksToWait = ticksToWait;
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
            int num = random.nextInt(2, 3);
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
     * Channel to use when forwarding this instance of {@link AnimatedTextElement}.
     */
    @Getter(AccessLevel.PUBLIC)
    private final AnimatedTextElement.Channel channel;

    /**
     * Dialog {@link String} encoded using {@link MiniMessage} serializer.
     */
    @Getter(AccessLevel.PUBLIC)
    private final String value;

    /**
     * Returns {@code true} if this instance of {@link AnimatedTextElement} should lock on last frame while waiting for the next dialog.
     */
    @Getter(AccessLevel.PUBLIC)
    private final boolean isLockOnLastFrame;

    /**
     * Pause to wait after displaying this {@link AnimatedTextElement}. Measured in {@code ticks}.
     */
    @Getter(AccessLevel.PUBLIC)
    private final int ticksToWait;

    /**
     * List of all {@link Component} frames generated for this {@link AnimatedTextElement} instance.
     */
    @Getter(AccessLevel.PUBLIC)
    private transient final List<Component> frames;

    /**
     * Returns first {@link Component} frame of this {@link AnimatedTextElement}.
     */
    public Component firstFrame() {
        return frames.get(0);
    }

    /**
     * Returns last {@link Component} frame of this {@link AnimatedTextElement}.
     */
    public Component lastFrame() {
        return frames.get(frames.size() - 1);
    }


    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public enum Channel {

        ACTIONBAR("animated_text/actionbar");

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