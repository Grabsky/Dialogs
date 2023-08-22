package cloud.grabsky.dialogs.configuration;

import cloud.grabsky.configuration.JsonConfiguration;
import cloud.grabsky.configuration.JsonPath;

import java.util.Map;

public class PluginDialogs implements JsonConfiguration {

    @JsonPath("dialogs")
    public static Map<String, String> DIALOGS;

}
