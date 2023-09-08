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
public final class PauseElement implements DialogElement {

    @Getter(AccessLevel.PUBLIC)
    private final int ticksToWait;


    @Internal
    @RequiredArgsConstructor(access = AccessLevel.PUBLIC)
    // NOTE: Field names does not follow Java Naming Convention to provide 1:1 mapping with JSON keys.
    public static final class Init implements LazyInit<PauseElement> {

        // Nullability cannot be determined because it depends entirely on the end-user.
        public @UnknownNullability Integer ticks_to_wait_before_continuing = null;

        @Override
        public @NotNull PauseElement init() throws IllegalStateException {
            // Throwing an error in case "value" field is invalid.
            if (ticks_to_wait_before_continuing == null)
                throw new IllegalStateException("Field \"ticks_to_wait_before_continuing\" is required but is either null or has not been found.");
            // Creating and returning element.
            return new PauseElement(ticks_to_wait_before_continuing);
        }

    }

}
