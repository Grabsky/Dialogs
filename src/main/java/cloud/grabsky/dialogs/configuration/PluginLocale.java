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

    // Missing Permissions

    @JsonPath("missing_permissions")
    public static Component MISSING_PERMISSIONS;

    // Commands > Dialogs

    @JsonPath("commands.dialogs_usage")
    public static Component COMMAND_DIALOGS_USAGE;

    // Commands > Dialogs > Reload

    @JsonPath("commands.dialogs_reload_success")
    public static Component COMMAND_DIALOGS_RELOAD_SUCCESS;

    @JsonPath("commands.dialogs_reload_failure")
    public static Component COMMAND_DIALOGS_RELOAD_FAILURE;

    // Commands > Dialogs > Send

    @JsonPath("commands.dialogs_send_usage")
    public static Component COMMAND_DIALOGS_SEND_USAGE;

    @JsonPath("commands.dialogs_send_failure_not_found")
    public static String COMMAND_DIALOGS_SEND_FAILURE_NOT_FOUND;

}
