package dev.latvian.kubejs.client.toast;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.kubejs.KubeJSCodecs;
import dev.latvian.kubejs.client.toast.icon.ToastIcon;
import dev.latvian.mods.rhino.mod.util.color.Color;
import dev.latvian.mods.rhino.mod.util.color.SimpleColor;
import dev.latvian.mods.rhino.mod.wrapper.ColorWrapper;
import net.minecraft.network.chat.Component;

import java.time.Duration;

/**
 * @author ZZZank
 */
public record NotificationData(
    Duration duration,
    Component text,
    ToastIcon icon,
    int iconSize,
    Color outlineColor,
    Color borderColor,
    Color backgroundColor,
    boolean textShadow
) {
    public static final Component[] NO_TEXT = new Component[0];
    public static final Duration DEFAULT_DURATION = Duration.ofSeconds(5L);
    public static final Color DEFAULT_BORDER_COLOR = new SimpleColor(0x472954);
    public static final Color DEFAULT_BACKGROUND_COLOR = new SimpleColor(0x241335);

    public static final Codec<NotificationData> CODEC = RecordCodecBuilder.create(
        builder -> builder.group(
            KubeJSCodecs.DURATION.optionalFieldOf("duration", DEFAULT_DURATION).forGetter(NotificationData::duration),
            KubeJSCodecs.COMPONENT.optionalFieldOf("text", null).forGetter(NotificationData::text),
            ToastIcon.CODEC.optionalFieldOf("icon", null).forGetter(NotificationData::icon),
            Codec.INT.optionalFieldOf("iconSize", 16).forGetter(NotificationData::iconSize),
            KubeJSCodecs.COLOR.optionalFieldOf("outlineColor", ColorWrapper.BLACK).forGetter(NotificationData::outlineColor),
            KubeJSCodecs.COLOR.optionalFieldOf("borderColor", DEFAULT_BORDER_COLOR).forGetter(NotificationData::borderColor),
            KubeJSCodecs.COLOR.optionalFieldOf("backgroundColor", DEFAULT_BACKGROUND_COLOR).forGetter(NotificationData::backgroundColor),
            Codec.BOOL.optionalFieldOf("textShadow", true).forGetter(NotificationData::textShadow)
        ).apply(builder, NotificationData::new)
    );
}
