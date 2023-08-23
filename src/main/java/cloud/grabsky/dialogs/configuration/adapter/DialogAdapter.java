package cloud.grabsky.dialogs.configuration.adapter;

import cloud.grabsky.dialogs.Dialog;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DialogAdapter extends JsonAdapter<Dialog> {
    /* SINGLETON */ public static DialogAdapter INSTANCE = new DialogAdapter();

    @Override
    public @NotNull Dialog fromJson(final @NotNull JsonReader in) throws IOException {
        in.beginObject();
        // ...
        final String text = (in.nextName().equals("text") == true) ? in.nextString() : "";
        final int pause = (in.nextName().equals("pause") == true) ? in.nextInt() : 0;
        // ...
        in.endObject();
        // ...
        return new Dialog(text, pause);
    }

    @Override
    public void toJson(final @Nullable JsonWriter out, final @Nullable Dialog value) throws IOException {
        throw new UnsupportedOperationException("NOT_IMPLEMENTED");
    }

}
