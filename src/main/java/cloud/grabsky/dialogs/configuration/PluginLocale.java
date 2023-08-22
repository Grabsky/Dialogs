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

}
