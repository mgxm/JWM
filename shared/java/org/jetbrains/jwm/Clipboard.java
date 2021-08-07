package org.jetbrains.jwm;

import lombok.*;
import java.util.*;
import java.util.function.*;
import java.util.concurrent.*;
import org.jetbrains.annotations.*;
import org.jetbrains.jwm.impl.*;

public class Clipboard {
    @ApiStatus.Internal
    public static Map<String, ClipboardFormat> _formats = Collections.synchronizedMap(new HashMap<String, ClipboardFormat>());

    public static void set(ClipboardEntry ... entries) {
        assert entries.length > 0;
        _nSet(entries);
    }

    /**
     * <p>Gets the system clipboard content.</p>
     *
     * <p>Clipboard content returned as an entry with specified format and serialized byte data.</p>
     * <p>Uses provided formats list to extract preferred clipboard data. First format has
     * has the highest priority, last specified format has the lowest priority.</p>
     *
     * <p>If formats list empty or there is no available data with specified formats,
     * method returns null.</p>
     *
     * @param formats       List of clipboard formats to extract
     * @return              Extracted clipboard entry; may be null
     */
    @Nullable
    public static ClipboardEntry get(ClipboardFormat ... formats) {
        assert formats.length > 0: "must contain at least one format entry";
        return _nGet(formats);
    }

    public static ClipboardFormat[] getFormats() {
        return _nGetFormats();
    }

    public static void clear() {
        _nClear();
    }

    /**
     * For user defined formats registration
     */
    @NotNull @SneakyThrows
    public static ClipboardFormat registerFormat(String formatId) {
        return _registerFormatInternal(formatId, true);
    }

    /**
     * For predefined (external) formats registration
     */
    @ApiStatus.Internal @NotNull @SneakyThrows
    public static ClipboardFormat _registerPredefinedFormat(String formatId) {
        return _registerFormatInternal(formatId, false);
    }

    @ApiStatus.Internal @NotNull @SneakyThrows
    public static ClipboardFormat _registerFormatInternal(String formatId, boolean needRegistration) {
        ClipboardFormat format = _formats.get(formatId);

        if (format != null)
            return format;
        if (needRegistration && !_nRegisterFormat(formatId))
            throw new RuntimeException("Failed to register format: " + formatId);

        return _cacheFormatEntry(formatId);
    }

    @ApiStatus.Internal @NotNull
    public static ClipboardFormat _cacheFormatEntry(String formatId) {
        ClipboardFormat format = new ClipboardFormat(formatId);
        _formats.put(formatId, format);
        return format;
    }

    @ApiStatus.Internal public static native void _nSet(ClipboardEntry[] entries);
    @ApiStatus.Internal public static native ClipboardEntry _nGet(ClipboardFormat[] formats);
    @ApiStatus.Internal public static native ClipboardFormat[] _nGetFormats();
    @ApiStatus.Internal public static native void _nClear();
    @ApiStatus.Internal public static native boolean _nRegisterFormat(String formatId);
}