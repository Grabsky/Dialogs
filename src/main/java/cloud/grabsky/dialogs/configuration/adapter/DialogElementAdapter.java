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
import com.squareup.moshi.JsonDataException;
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
    public @Nullable DialogElement fromJson(final @NotNull JsonReader in) throws IOException {
        // Beginning the JSON object.
        in.beginObject();
        // Getting the type.
        final String type = (in.nextName().equals("type") == true) ? in.nextString().toLowerCase() : "";
        // Doing stuff based on the type.
        return switch (type) {
            case "text/chat_message", "text/chat_broadcast", "text/actionbar" -> {
                // Throwing exception on unexpected input.
                if (in.nextName().equalsIgnoreCase("value") == false)
                    throw new JsonDataException("Expected \"value\" but found something else.");
                // Getting the value.
                final String value = in.nextString();
                // Throwing exception on unexpected input.
                if (in.nextName().equalsIgnoreCase("ticks_to_wait_before_continuing") == false)
                    throw new JsonDataException("Expected \"ticks_to_wait_before_continuing\" but found something else.");
                // Getting the value.
                final int ticksToWait = in.nextInt();
                // Ending the JSON object.
                in.endObject();
                // Returning new instance of TextElement.
                yield new TextElement(TextElement.Channel.fromIdentifier(type), value, ticksToWait);
            }
            case "animated_text/actionbar" -> {
                // Throwing exception on unexpected input.
                if (in.nextName().equalsIgnoreCase("value") == false)
                    throw new JsonDataException("Expected \"value\" but found something else.");
                // Getting the value.
                final String value = in.nextString();
                // Throwing exception on unexpected input.
                if (in.nextName().equalsIgnoreCase("lock_on_last_frame") == false)
                    throw new JsonDataException("Expected \"lock_on_last_frame\" but found something else.");
                // Getting the value.
                final boolean lockOnLastFrame = in.nextBoolean();
                // Throwing exception on unexpected input.
                if (in.nextName().equalsIgnoreCase("ticks_to_wait_before_continuing") == false)
                    throw new JsonDataException("Expected \"ticks_to_wait_before_continuing\" but found something else.");
                // Getting the value.
                final int ticksToWait = in.nextInt();
                // Ending the JSON object.
                in.endObject();
                // Returning new instance of ConsoleCommandElement.
                yield new AnimatedTextElement(AnimatedTextElement.Channel.fromIdentifier(type), value, lockOnLastFrame, ticksToWait);
            }
            case "console_command" -> {
                // Throwing exception on unexpected input.
                if (in.nextName().equalsIgnoreCase("value") == false)
                    throw new JsonDataException("Expected \"value\" but found something else.");
                // Getting the value.
                final String value = in.nextString();
                // Throwing exception on unexpected input.
                if (in.nextName().equalsIgnoreCase("ticks_to_wait_before_continuing") == false)
                    throw new JsonDataException("Expected \"ticks_to_wait_before_continuing\" but found something else.");
                // Getting the value.
                final int ticksToWait = in.nextInt();
                // Ending the JSON object.
                in.endObject();
                // Returning new instance of ConsoleCommandElement.
                yield new ConsoleCommandElement(value, ticksToWait);
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
