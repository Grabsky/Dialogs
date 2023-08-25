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
package cloud.grabsky.dialogs.configuration.adapter;

import cloud.grabsky.dialogs.DialogElement;
import cloud.grabsky.dialogs.elements.AnimatedTextElement;
import cloud.grabsky.dialogs.elements.ConsoleCommandElement;
import cloud.grabsky.dialogs.elements.TextElement;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DialogElementAdapter extends JsonAdapter<DialogElement> {
    /* SINGLETON */ public static DialogElementAdapter INSTANCE = new DialogElementAdapter();

    @Override
    public @NotNull DialogElement fromJson(final @NotNull JsonReader in) throws IOException {
        // Beginning the JSON object.
        in.beginObject();
        // Getting the type.
        final String type = (in.nextName().equals("type") == true) ? in.nextString().toLowerCase() : "";
        // Doing stuff based on the type.
        return switch (type) {
            case "text/chat_message", "text/chat_broadcast", "text/actionbar" -> {
                final TextElement.Init init = new TextElement.Init(TextElement.Channel.fromIdentifier(type));
                // ...
                while (in.hasNext() == true) {
                    final String name = in.nextName().toLowerCase();
                    // ...
                    switch (name) {
                        case "value" -> init.value = in.nextString();
                        case "lock_until_next_element" -> init.lock_until_next_element = in.nextBoolean();
                        case "ticks_to_wait_before_continuing" -> init.ticks_to_wait_before_continuing = in.nextInt();
                    }
                }
                // Ending the JSON object.
                in.endObject();
                // Initializing and returning the value.
                yield init.init();
            }
            case "animated_text/actionbar" -> {
                final AnimatedTextElement.Init init = new AnimatedTextElement.Init(AnimatedTextElement.Channel.fromIdentifier(type));
                // ...
                while (in.hasNext() == true) {
                    final String name = in.nextName().toLowerCase();
                    // ...
                    switch (name) {
                        case "value" -> init.value = in.nextString();
                        case "refresh_rate" -> init.refresh_rate = in.nextLong();
                        case "min_letters_per_frame" -> init.min_letters_per_frame = in.nextInt();
                        case "max_letters_per_frame" -> init.max_letters_per_frame = in.nextInt();
                        case "lock_until_next_element" -> init.lock_until_next_element = in.nextBoolean();
                        case "ticks_to_wait_before_continuing" -> init.ticks_to_wait_before_continuing = in.nextInt();
                    }
                }
                // Ending the JSON object.
                in.endObject();
                // Initializing and returning the value.
                yield init.init();
            }
            case "console_command" -> {
                final ConsoleCommandElement.Init init = new ConsoleCommandElement.Init();
                // ...
                while (in.hasNext() == true) {
                    final String name = in.nextName().toLowerCase();
                    // ...
                    switch (name) {
                        case "value" -> init.value = in.nextString();
                        case "ticks_to_wait_before_continuing" -> init.ticks_to_wait_before_continuing = in.nextInt();
                    }
                }
                // Ending the JSON object.
                in.endObject();
                // Initializing and returning the value.
                yield init.init();
            }
            default -> {
                // Ending the JSON object.
                in.endObject();
                // Throwing exception on unexpected input.
                throw new IllegalArgumentException(type);
            }
        };
    }

    @Override
    public void toJson(final @Nullable JsonWriter out, final @Nullable DialogElement value) {
        throw new UnsupportedOperationException("NOT_IMPLEMENTED");
    }

}
