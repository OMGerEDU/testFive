package android.content;

/**
 * Minimal stub of Android's Context.
 */
public class Context {
    private final ContentResolver resolver = new ContentResolver();

    public ContentResolver getContentResolver() {
        return resolver;
    }
}

