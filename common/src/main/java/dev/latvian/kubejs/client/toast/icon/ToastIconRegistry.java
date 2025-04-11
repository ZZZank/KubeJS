package dev.latvian.kubejs.client.toast.icon;

import com.mojang.serialization.Codec;
import lombok.val;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author ZZZank
 */
public record ToastIconRegistry(int index, Codec<? extends ToastIcon> codec) implements ToastIconType {

    private static final List<ToastIconType> REGISTERED = new ArrayList<>();

    public static ToastIconType register(Codec<? extends ToastIcon> codec) {
        val entry = new ToastIconRegistry(REGISTERED.size(), codec);
        REGISTERED.add(entry);
        return entry;
    }

    public static Collection<ToastIconType> getRegistered() {
        return Collections.unmodifiableList(REGISTERED);
    }

    public static ToastIconType get(int index) {
        return index < 0 || index >= REGISTERED.size()
            ? null
            : REGISTERED.get(index);
    }

    public static ToastIconType getOrDefault(int index, ToastIconType defaultValue) {
        val got = get(index);
        return got == null ? defaultValue : got;
    }
}
