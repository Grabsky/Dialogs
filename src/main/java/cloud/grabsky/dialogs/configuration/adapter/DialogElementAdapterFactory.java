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
package cloud.grabsky.dialogs.configuration.adapter;

import cloud.grabsky.configuration.paper.adapter.StringComponentAdapter;
import cloud.grabsky.dialogs.Condition;
import cloud.grabsky.dialogs.DialogElement;
import cloud.grabsky.dialogs.elements.AnimatedActionBarElement;
import cloud.grabsky.dialogs.elements.CommandElement;
import cloud.grabsky.dialogs.elements.MessageElement;
import cloud.grabsky.dialogs.elements.PauseElement;
import cloud.grabsky.dialogs.elements.SoundElement;
import cloud.grabsky.dialogs.util.Enums;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonReader.Token;
import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import net.kyori.adventure.sound.Sound;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static com.squareup.moshi.Types.getRawType;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DialogElementAdapterFactory implements JsonAdapter.Factory {
    /* SINGLETON */ public static DialogElementAdapterFactory INSTANCE = new DialogElementAdapterFactory();

    private static final Type LIST_OF_CONDITIONS = Types.newParameterizedType(List.class, Condition.class);
    private static final Type LIST_OF_STRINGS = Types.newParameterizedType(List.class, String.class);
    private static final Type LIST_OF_SOUNDS = Types.newParameterizedType(List.class, Sound.class);

    @Override
    public @Nullable JsonAdapter<DialogElement> create(final @NotNull Type type, final @NotNull Set<? extends Annotation> annotations, final @NotNull Moshi moshi) {
        if (DialogElement.class.isAssignableFrom(getRawType(type)) == false)
            return null;
        // ...
        final var adapter0 = moshi.adapter(Sound.class);
        final var adapter1 = moshi.adapter(LIST_OF_CONDITIONS);
        final var adapter2 = moshi.adapter(LIST_OF_STRINGS);
        final var adapter3 = moshi.adapter(LIST_OF_SOUNDS);
        // ...
        return new JsonAdapter<>() {

            @Override @SuppressWarnings("unchecked")
            public @NotNull DialogElement fromJson(final @NotNull JsonReader in) throws IOException {
                // Beginning the JSON object.
                in.beginObject();
                // Getting the type.
                final String type = (in.nextName().equals("type") == true) ? in.nextString().toLowerCase() : "";
                // Doing stuff based on the type.
                return switch (type) {
                    case "chat_message", "actionbar_message" -> {
                        final MessageElement.Init init = new MessageElement.Init(Enums.fromName(MessageElement.Type.class, type));
                        // ...
                        while (in.hasNext() == true) {
                            final String name = in.nextName().toLowerCase();
                            // ...
                            switch (name) {
                                case "audience" -> init.audience = Enums.fromName(MessageElement.AudienceType.class, in.nextString());
                                case "value" -> init.value = (in.peek() == Token.BEGIN_ARRAY) ? StringComponentAdapter.INSTANCE.fromJson(in) : in.nextString();
                                case "ticks_to_wait_before_continuing" -> init.ticks_to_wait_before_continuing = in.nextInt();
                                case "conditions" -> {
                                    // Parsing the Sound object.
                                    final @Nullable List<Condition> conditions = (List<Condition>) adapter1.nullSafe().fromJson(in);
                                    // Skipping when specified as null.
                                    if (conditions != null) init.conditions = conditions;
                                }
                            }
                        }
                        // Ending the JSON object.
                        in.endObject();
                        // Initializing and returning the value.
                        yield init.init();
                    }
                    case "actionbar_animation" -> {
                        final AnimatedActionBarElement.Init init = new AnimatedActionBarElement.Init();
                        // ...
                        while (in.hasNext() == true) {
                            final String name = in.nextName().toLowerCase();
                            // ...
                            switch (name) {
                                case "audience" -> init.audience = Enums.fromName(AnimatedActionBarElement.AudienceType.class, in.nextString());
                                case "value" -> init.value = in.nextString();
                                case "refresh_rate" -> init.refresh_rate = in.nextLong();
                                case "min_letters_per_frame" -> init.min_letters_per_frame = in.nextInt();
                                case "max_letters_per_frame" -> init.max_letters_per_frame = in.nextInt();
                                case "typing_sound" -> {
                                    // Parsing the Sound object.
                                    final @Nullable Sound sound = adapter0.nullSafe().fromJson(in);
                                    // Skipping when specified as null.
                                    if (sound != null) init.typing_sound = sound;
                                }
                                case "lock_until_next_element" -> init.lock_until_next_element = in.nextBoolean();
                                case "ticks_to_wait_before_continuing" -> init.ticks_to_wait_before_continuing = in.nextInt();
                                case "conditions" -> {
                                    // Parsing the Sound object.
                                    final @Nullable List<Condition> conditions = (List<Condition>) adapter1.nullSafe().fromJson(in);
                                    // Skipping when specified as null.
                                    if (conditions != null) init.conditions = conditions;
                                }
                            }
                        }
                        // Ending the JSON object.
                        in.endObject();
                        // Initializing and returning the value.
                        yield init.init();
                    }
                    case "player_command", "console_command" -> {
                        final CommandElement.Init init = new CommandElement.Init(Enums.fromName(CommandElement.Type.class, type));
                        // ...
                        while (in.hasNext() == true) {
                            final String name = in.nextName().toLowerCase();
                            // ...
                            switch (name) {
                                case "value" -> {
                                    if (in.peek() == Token.BEGIN_ARRAY) {
                                        init.value = (List<String>) adapter2.nullSafe().fromJson(in);
                                        continue;
                                    }
                                    init.value = List.of(in.nextString());
                                }
                                case "ticks_to_wait_before_continuing" -> init.ticks_to_wait_before_continuing = in.nextInt();
                                case "conditions" -> {
                                    // Parsing the Sound object.
                                    final @Nullable List<Condition> conditions = (List<Condition>) adapter1.nullSafe().fromJson(in);
                                    // Skipping when specified as null.
                                    if (conditions != null) init.conditions = conditions;
                                }
                            }
                        }
                        // Ending the JSON object.
                        in.endObject();
                        // Initializing and returning the value.
                        yield init.init();
                    }
                    case "sound" -> {
                        final SoundElement.Init init = new SoundElement.Init();
                        // ...
                        while (in.hasNext() == true) {
                            final String name = in.nextName().toLowerCase();
                            // ...
                            switch (name) {
                                case "audience" -> init.audience = Enums.fromName(SoundElement.AudienceType.class, in.nextString());
                                case "value" -> {
                                    if (in.peek() == Token.BEGIN_ARRAY) {
                                        init.value = (List<Sound>) adapter3.nullSafe().fromJson(in);
                                        continue;
                                    }
                                    init.value = Collections.singletonList(adapter0.nullSafe().fromJson(in));
                                }
                                case "ticks_to_wait_before_continuing" -> init.ticks_to_wait_before_continuing = in.nextInt();
                                case "conditions" -> {
                                    // Parsing the Sound object.
                                    final @Nullable List<Condition> conditions = (List<Condition>) adapter1.nullSafe().fromJson(in);
                                    // Skipping when specified as null.
                                    if (conditions != null) init.conditions = conditions;
                                }
                            }
                        }
                        // Ending the JSON object.
                        in.endObject();
                        // Initializing and returning the value.
                        yield init.init();
                    }
                    case "pause" -> {
                        final PauseElement.Init init = new PauseElement.Init();
                        // ...
                        while (in.hasNext() == true) {
                            final String name = in.nextName().toLowerCase();
                            // ...
                            switch (name) {
                                case "ticks_to_wait_before_continuing" -> init.ticks_to_wait_before_continuing = in.nextInt();
                                case "conditions" -> {
                                    // Parsing the Sound object.
                                    final @Nullable List<Condition> conditions = (List<Condition>) adapter1.nullSafe().fromJson(in);
                                    // Skipping when specified as null.
                                    if (conditions != null) init.conditions = conditions;
                                }
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

        };
    }

}
