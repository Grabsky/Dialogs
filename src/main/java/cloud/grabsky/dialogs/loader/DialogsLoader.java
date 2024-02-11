package cloud.grabsky.dialogs.loader;

import cloud.grabsky.configuration.paper.adapter.ComponentAdapter;
import cloud.grabsky.configuration.paper.adapter.NamespacedKeyAdapter;
import cloud.grabsky.configuration.paper.adapter.SoundAdapterFactory;
import cloud.grabsky.configuration.paper.adapter.SoundSourceAdapter;
import cloud.grabsky.dialogs.Dialog;
import cloud.grabsky.dialogs.Dialogs;
import cloud.grabsky.dialogs.configuration.adapter.DialogElementAdapterFactory;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import okio.BufferedSource;
import okio.Okio;
import org.bukkit.NamespacedKey;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import static cloud.grabsky.configuration.paper.util.Resources.ensureResourceExistence;

@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public final class DialogsLoader {

    private final @NotNull Dialogs plugin;

    private final Map<String, Dialog> dialogs = new HashMap<>();

    private static final Type ADAPTER_TYPE = Types.newParameterizedType(Map.class, String.class, Dialog.class);

    public @UnmodifiableView @NotNull Map<String, Dialog> getDialogs() {
        return Collections.unmodifiableMap(dialogs);
    }

    @SuppressWarnings("unchecked")
    public boolean load() throws IOException {
        final File directory = new File(plugin.getDataFolder(), "dialogs");
        // Trying...
        try {
            // Creating directory and default file.
            if (directory.mkdirs() == true) {
                ensureResourceExistence(plugin, new File(directory, "example.json"));
            }
            // Listing files inside the directory.
            final @Nullable File[] files = directory.listFiles();
            // Returning amd logging a message if directory is empty.
            if (files == null || files.length == 0) {
                plugin.getLogger().info("No dialogs has been found inside " + directory + "...");
                return true;
            }
            // Creating a new instance of Moshi.
            final Moshi moshi = new Moshi.Builder()
                    .add(NamespacedKey.class, NamespacedKeyAdapter.INSTANCE)
                    .add(Sound.Source.class, SoundSourceAdapter.INSTANCE)
                    .add(Component.class, ComponentAdapter.INSTANCE)
                    .add(DialogElementAdapterFactory.INSTANCE)
                    .add(SoundAdapterFactory.INSTANCE)
                    .build();
            // Clearing the map before populating it again.
            dialogs.clear();
            // Total number of files that attempted to be loaded.
            int filesTotal = 0;
            // Total number of files that were successfully loaded.
            int filesLoaded = 0;
            // Iterating over all files inside the dialogs directory.
            for (final File file : files) {
                // Skipping null (?) or non-JSON files.
                if (file == null || file.getName().endsWith(".json") == false)
                    continue;
                // Incrementing total number of files as we're about to attempt loading of the file.
                filesTotal++;
                // Another try...catch block is used here to make sure exception will interrupt loading of current file only, not all of them.
                try {
                    // Creating new BufferedSource instance from the file.
                    final BufferedSource source = Okio.buffer(Okio.source(file));
                    // Converting file contents to a result map.
                    final @Nullable Map<String, Dialog> result = (Map<String, Dialog>) moshi.adapter(ADAPTER_TYPE).lenient().fromJson(source);
                    // Logging error in case result ended up being null.
                    if (result == null) {
                        plugin.getLogger().severe("Could not load dialogs collection located inside \"" + file + "\" file.");
                        plugin.getLogger().severe("  null");
                        // Continuing to the next file...
                        continue;
                    }
                    // Iterating over each entry collected from the file.
                    result.forEach((name, dialog) -> {
                        // Removing extension from the file name.
                        final String nameWithoutExtension = file.getName().substring(0, file.getName().lastIndexOf('.')).replace(" ", "_");
                        // Adding to the internal map.
                        dialogs.put(nameWithoutExtension + "/" + name, dialog);
                    });
                    // Incrementing number of files that were successfully loaded.
                    filesLoaded++;
                } catch (final IOException e) {
                    plugin.getLogger().severe("Could not load dialogs collection located inside \"" + file + "\" file.");
                    plugin.getLogger().severe("  " + e.getMessage());
                }
            }
            return (filesTotal == filesLoaded);
        } catch (final IOException e) {
            plugin.getLogger().severe("An error occurred while trying to save default dialogs file.");
            e.printStackTrace();
        }
        // An exception must have been thrown, so returning false here is expected.
        return false;
    }

}
