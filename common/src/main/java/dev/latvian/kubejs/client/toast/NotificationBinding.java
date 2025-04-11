package dev.latvian.kubejs.client.toast;

import dev.latvian.kubejs.bindings.TextWrapper;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaObject;
import dev.latvian.mods.rhino.native_java.type.info.TypeInfo;
import dev.latvian.mods.rhino.util.HideFromJS;
import lombok.val;
import net.minecraft.network.chat.Component;

import java.util.Map;
import java.util.function.Consumer;

/**
 * @author ZZZank
 */
public class NotificationBinding {

    @HideFromJS
    public static NotificationData of(Context cx, Object object, TypeInfo target) {
        if (object instanceof NotificationData b) {
            return b;
        } else if (object instanceof Map<?, ?> map) {
            return null; // FIXME
        } else if (object instanceof BaseFunction func) {
            val consumer = (Consumer<NotificationData>) NativeJavaObject.createInterfaceAdapter(cx, Consumer.class, func);
            return make(consumer);
        }
        return new NotificationData(TextWrapper.componentOf(object));
    }

    public static NotificationData ofText(Component title) {
        return new NotificationData(title);
    }

    public static NotificationData ofTitles(Component title, Component subTitle) {
        return new NotificationData(title.copy().append("\n").append(subTitle));
    }

    public static NotificationData make(Consumer<NotificationData> consumer) {
        val b = new NotificationData();
        consumer.accept(b);
        return b;
    }
}
