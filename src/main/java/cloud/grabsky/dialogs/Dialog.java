package cloud.grabsky.dialogs;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import lombok.AccessLevel;
import lombok.Getter;

public final class Dialog {

    private static final Pattern TAG_PATTERN = Pattern.compile("(?=<)|(?<=>)"); // Pattern.compile("<[^>]*>");

    public Dialog(final String text, final int pause) {
        this.text = text;
        this.pause = pause;
        // ...
        final ArrayList<Component> frames = new ArrayList<>();
        // ...
        final Random random = new Random();
        // Iterating over splitted dialog, with all MiniMessage tags removed.
        final Iterator<String> iterator = TAG_PATTERN.splitAsStream(text).map(String::new).iterator();
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
     * Dialog {@link String} encoded using {@link MiniMessage} serializer.
     */
    @Getter(AccessLevel.PUBLIC)
    private final String text;

    /**
     * Pause to wait after displaying this {@link Dialog}. Measured in {@code ticks}.
     */
    @Getter(AccessLevel.PUBLIC)
    private final int pause;

    @Getter(AccessLevel.PUBLIC)
    private transient final List<Component> frames;

    public Component getFirstFrame() {
        return frames.get(0);
    }

    public Component getLastFrame() {
        return frames.get(frames.size() - 1);
    }

}
