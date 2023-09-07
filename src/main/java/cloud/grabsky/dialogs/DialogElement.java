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

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;

/**
 * Represents a static, non-animated dialog element.
 */
public interface DialogElement {

    /**
     * Number of ticks to wait after starting this {@link DialogElement}, before moving onto the next {@link DialogElement}. Measured in {@code ticks}.
     */
    int ticksToWait();

    /**
     * Represents dialog element that can be animated.
     */
    interface Animated extends DialogElement {

        /**
         * Animation refresh rate. Measured in {@code ticks}.
         */
        long refreshRate();

        /**
         * Minimum number of letters per frame.
         */
        int minLettersPerFrame();

        /**
         * Minimum number of letters per frame.
         */
        int maxLettersPerFrame();

        /**
         * Returns {@code true} if this instance should lock on last frame while waiting for the next dialog.
         */
        boolean lockUntilNextElement();

        /**
         * List of all {@link Component} frames generated for this instance.
         */
        @UnmodifiableView @NotNull List<Component> frames();

        /**
         * Returns first {@link Component} frame of this instance.
         */
        default Component firstFrame() {
            return frames().get(0);
        }

        /**
         * Returns last {@link Component} frame of this instance.
         */
        default Component lastFrame() {
            return frames().get(frames().size() - 1);
        }

    }

}
