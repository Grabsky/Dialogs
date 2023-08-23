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
package cloud.grabsky.dialogs.configuration;

import cloud.grabsky.configuration.JsonConfiguration;
import cloud.grabsky.configuration.JsonPath;
import net.kyori.adventure.text.Component;

public final class PluginLocale implements JsonConfiguration {

    @JsonPath("commands.dialogs_usage")
    public static Component COMMAND_DIALOGS_USAGE;

    @JsonPath("commands.dialogs_send_usage")
    public static Component COMMAND_DIALOGS_SEND_USAGE;

    @JsonPath("commands.dialogs_send_failure_not_found")
    public static String COMMAND_DIALOGS_SEND_FAILURE_NOT_FOUND;


    public static final class Commands implements JsonConfiguration {

        // Commands > General

        @JsonPath("missing_permissions")
        public static Component MISSING_PERMISSIONS;

        // Commands > Executors

        @JsonPath("invalid_executor_player")
        public static Component INVALID_EXECUTOR_PLAYER;

        @JsonPath("invalid_executor_console")
        public static Component INVALID_EXECUTOR_CONSOLE;

        // Commands > Arguments

        @JsonPath("invalid_boolean")
        public static String INVALID_BOOLEAN;

        @JsonPath("invalid_short")
        public static String INVALID_SHORT;

        @JsonPath("invalid_short_not_in_range")
        public static String INVALID_SHORT_NOT_IN_RANGE;

        @JsonPath("invalid_integer")
        public static String INVALID_INTEGER;

        @JsonPath("invalid_integer_not_in_range")
        public static String INVALID_INTEGER_NOT_IN_RANGE;

        @JsonPath("invalid_long")
        public static String INVALID_LONG;

        @JsonPath("invalid_long_not_in_range")
        public static String INVALID_LONG_NOT_IN_RANGE;

        @JsonPath("invalid_float")
        public static String INVALID_FLOAT;

        @JsonPath("invalid_float_not_in_range")
        public static String INVALID_FLOAT_NOT_IN_RANGE;

        @JsonPath("invalid_double")
        public static String INVALID_DOUBLE;

        @JsonPath("invalid_double_not_in_range")
        public static String INVALID_DOUBLE_NOT_IN_RANGE;

        @JsonPath("invalid_uuid")
        public static String INVALID_UUID;

        @JsonPath("invalid_player")
        public static String INVALID_PLAYER;

        @JsonPath("invalid_offline_player")
        public static String INVALID_OFFLINE_PLAYER;

        @JsonPath("invalid_world")
        public static String INVALID_WORLD;

        @JsonPath("invalid_enchantment")
        public static String INVALID_ENCHANTMENT;

        @JsonPath("invalid_material")
        public static String INVALID_MATERIAL;

        @JsonPath("invalid_entity_type")
        public static String INVALID_ENTITY_TYPE;

        @JsonPath("invalid_namespacedkey")
        public static String INVALID_NAMESPACEDKEY;

        @JsonPath("invalid_position")
        public static String INVALID_POSITION;

        /* CUSTOM */



    }

}
