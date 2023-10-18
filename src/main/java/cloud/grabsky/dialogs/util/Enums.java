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
