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
import com.google.gson.Gson;
import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.bukkit.event.Listener;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

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


    @SuppressWarnings("UnstableApiUsage")
    public static final class PluginLoader implements io.papermc.paper.plugin.loader.PluginLoader {

        @Override
        public void classloader(final @NotNull PluginClasspathBuilder classpathBuilder) throws IllegalStateException {
            final MavenLibraryResolver resolver = new MavenLibraryResolver();
            // Parsing the file.
            try (final InputStream in = getClass().getResourceAsStream("/paper-libraries.json")) {
                final PluginLibraries libraries = new Gson().fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), PluginLibraries.class);
                // Adding repositorties.
                libraries.asRepositories().forEach(resolver::addRepository);
                // Adding dependencies.
                libraries.asDependencies().forEach(resolver::addDependency);
                // Adding library resolver.
                classpathBuilder.addLibrary(resolver);
            } catch (final IOException e) {
                throw new IllegalStateException(e);
            }
        }

        @RequiredArgsConstructor(access = AccessLevel.PUBLIC)
        private static final class PluginLibraries {

            private final Map<String, String> repositories;
            private final List<String> dependencies;

            public Stream<RemoteRepository> asRepositories() {
                return repositories.entrySet().stream().map(entry -> new RemoteRepository.Builder(entry.getKey(), "default", entry.getValue()).build());
            }

            public Stream<Dependency> asDependencies() {
                return dependencies.stream().map(value -> new Dependency(new DefaultArtifact(value), null));
            }

        }

    }

}
