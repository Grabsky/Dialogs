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
