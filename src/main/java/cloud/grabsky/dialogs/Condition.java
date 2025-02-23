package cloud.grabsky.dialogs;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public final class Condition {

    @Getter(AccessLevel.PUBLIC)
    private final String placeholder;

    @Getter(AccessLevel.PUBLIC)
    private final Operator operator;

    @Getter(AccessLevel.PUBLIC)
    private final String value;

    public boolean testCondition(final @NotNull Player player) {
        final String parsedPlaceholder = PlaceholderAPI.setPlaceholders(player, placeholder);
        final String parsedValue = PlaceholderAPI.setPlaceholders(player, value);
        return switch (operator) {
            case EQUALS -> parsedPlaceholder.equals(parsedValue);
            case NOT_EQUALS -> parsedPlaceholder.equals(parsedValue) == false;
            case GREATER_THAN, GREATER_THAN_OR_EQUALS, SMALLER_THAN, SMALLER_THAN_OR_EQUALS -> {
                // Parsing values to Double.
                final Double a = toDouble(parsedPlaceholder);
                final Double b = toDouble(parsedValue);
                // Returning false if first value is not a number.
                if (a == null) {
                    Dialogs.getInstance().getLogger().warning("Tried to compare placeholder '" + placeholder + "' but output is not a number: '" + parsedPlaceholder + "'");
                    yield false;
                }
                // Returning false if second value is not a number.
                if (b == null) {
                    Dialogs.getInstance().getLogger().warning("Tried to compare placeholder '" + placeholder + "' but expected value is not a number: '" + parsedValue + "'");
                    yield false;
                }
                // Comparing numbers based on the specified operator and returning the result.
                if (operator == Operator.GREATER_THAN)
                    yield a > b;
                if (operator == Operator.GREATER_THAN_OR_EQUALS)
                    yield a >= b;
                if (operator == Operator.SMALLER_THAN)
                    yield a < b;
                if (operator == Operator.SMALLER_THAN_OR_EQUALS)
                    yield a <= b;
                // In any other case, false is returned.
                yield false;
            }
            case CONTAINS -> parsedPlaceholder.contains(parsedValue);
            case NOT_CONTAINS -> parsedPlaceholder.contains(parsedValue) == false;
            case STARTS_WITH -> parsedPlaceholder.startsWith(parsedValue);
            case ENDS_WITH -> parsedPlaceholder.endsWith(parsedValue);
        };
    }

    private @Nullable Double toDouble(final String string) {
        try {
            return Double.parseDouble(string);
        } catch (final NumberFormatException ___) {
            return null;
        }
    }

    public enum Operator {
        EQUALS,
        NOT_EQUALS,
        GREATER_THAN,
        GREATER_THAN_OR_EQUALS,
        SMALLER_THAN,
        SMALLER_THAN_OR_EQUALS,
        CONTAINS,
        NOT_CONTAINS,
        STARTS_WITH,
        ENDS_WITH
    }

}
