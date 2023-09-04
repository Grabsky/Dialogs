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
package cloud.grabsky.dialogs;

import cloud.grabsky.bedrock.BedrockPlugin;
import cloud.grabsky.commands.RootCommandManager;
import cloud.grabsky.configuration.ConfigurationHolder;
import cloud.grabsky.configuration.ConfigurationMapper;
import cloud.grabsky.configuration.exception.ConfigurationMappingException;
import cloud.grabsky.configuration.paper.PaperConfigurationMapper;
import cloud.grabsky.dialogs.command.DialogsCommand;
import cloud.grabsky.dialogs.configuration.PluginLocale;
import cloud.grabsky.dialogs.configuration.adapter.DialogElementAdapter;
import cloud.grabsky.dialogs.loader.DialogsLoader;
import org.bukkit.event.Listener;

import java.io.File;
import java.io.IOException;

import lombok.AccessLevel;
import lombok.Getter;

import static cloud.grabsky.configuration.paper.util.Resources.ensureResourceExistence;

public final class Dialogs extends BedrockPlugin implements Listener {

    @Getter(AccessLevel.PUBLIC)
    private static Dialogs instance;

    @Getter(AccessLevel.PUBLIC)
    private static boolean isPlaceholderAPI;

    @Getter(AccessLevel.PUBLIC)
    private DialogsLoader dialogsLoader;

    private ConfigurationMapper mapper;

    @Override
    public void onEnable() {
        super.onEnable();
        // ...
        instance = this;
        // ...
        try {
            Class.forName("me.clip.placeholderapi.PlaceholderAPI");
            // Mark PlaceholderAPI as present
            isPlaceholderAPI = true;
        } catch (final ClassNotFoundException ___) { /* IGNORE */ }
        // ...
        this.dialogsLoader = new DialogsLoader(this);
        // ...
        this.mapper = PaperConfigurationMapper.create(moshi -> moshi.add(DialogElement.class, DialogElementAdapter.INSTANCE));
        // ...
        if (this.reloadConfiguration() == false)
            this.getServer().shutdown();
        // ...
        new RootCommandManager(this)
                .registerDependency(Dialogs.class, this)
                .registerCommand(DialogsCommand.class);
        // ...
        this.getServer().getPluginManager().registerEvents(this, this);
    }


    public boolean reloadConfiguration() {
        try {
            return this.onReload();
        } catch (final IllegalStateException | ConfigurationMappingException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean onReload() throws ConfigurationMappingException, IllegalStateException {
        try {
            // Reloading configuration...
            final File locale = ensureResourceExistence(this, new File(this.getDataFolder(), "locale.json"));
            final File localeCommands = ensureResourceExistence(this, new File(this.getDataFolder(), "locale_commands.json"));
            // Reloading configuration files.
            mapper.map(
                    ConfigurationHolder.of(PluginLocale.class, locale),
                    ConfigurationHolder.of(PluginLocale.Commands.class, localeCommands)
            );
            // Returning the result of DialogsLoader#load method as to know whether plugin has reloaded sucessfully or not.
            return this.dialogsLoader.load();
        } catch (final IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
