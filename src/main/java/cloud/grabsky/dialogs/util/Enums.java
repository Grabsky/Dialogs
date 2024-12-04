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
package cloud.grabsky.dialogs.util;

import org.jetbrains.annotations.NotNull;

public final class Enums {

    public static <T extends Enum<?>> @NotNull T fromName(final @NotNull Class<T> clazz, final @NotNull String name) throws IllegalArgumentException {
        // Returning first matching enum.
        for (final T en : clazz.getEnumConstants())
            if (en.name().equalsIgnoreCase(name) == true)
                return en;
        // Throwing an exception in case nothing was found.
        throw new IllegalArgumentException("No enum constant found for " + clazz.getName() + "." + name + ".");
    }

}
