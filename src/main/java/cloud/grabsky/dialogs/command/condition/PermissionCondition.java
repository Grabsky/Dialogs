package cloud.grabsky.dialogs.command.condition;

import cloud.grabsky.bedrock.components.Message;
import cloud.grabsky.commands.RootCommandContext;
import cloud.grabsky.commands.condition.Condition;
import cloud.grabsky.dialogs.configuration.PluginLocale;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class PermissionCondition implements Condition {

    private final String[] permissions;

    public static PermissionCondition of(final String... permissions) {
        return new PermissionCondition(permissions);
    }

    @Override
    public boolean test(final RootCommandContext context) {
        final CommandSender sender = context.getExecutor().asCommandSender();
        // ...
        for (final String permission : permissions)
            if (sender.hasPermission(permission) == false)
                return false;
        // ...
        return true;
    }

    @Override
    public void accept(final @NotNull RootCommandContext context) {
        Message.of(PluginLocale.Commands.MISSING_PERMISSIONS).send(context.getExecutor());
    }

}
