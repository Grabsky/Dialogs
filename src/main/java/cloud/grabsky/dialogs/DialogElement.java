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

import net.kyori.adventure.text.Component;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

/**
 * Represents a static, non-animated dialog element.
 */
public interface DialogElement {

    /**
     * Number of ticks to wait after starting this {@link DialogElement}, before moving onto the next {@link DialogElement}. Measured in {@code ticks}.
     */
    int ticksToWait();

    /**
     * List of all conditions that must be met in order to execute this {@link DialogElement}.
     */
    List<Condition> conditions();

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
