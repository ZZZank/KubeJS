package dev.latvian.kubejs.client.toast;

import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.kubejs.KubeJSCodecs;
import dev.latvian.kubejs.bindings.TextWrapper;
import dev.latvian.kubejs.client.toast.icon.*;
import dev.latvian.kubejs.util.MapJS;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.mod.util.color.Color;
import dev.latvian.mods.rhino.mod.util.color.SimpleColor;
import dev.latvian.mods.rhino.mod.wrapper.ColorWrapper;
import dev.latvian.mods.rhino.native_java.type.info.TypeInfo;
import dev.latvian.mods.rhino.util.HideFromJS;
import lombok.*;
import lombok.experimental.Accessors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

/**
 * @author ZZZank
 */
@AllArgsConstructor
@Getter
@Accessors(fluent = true, chain = true)
@Setter
@ToString
@EqualsAndHashCode
public final class NotificationData {

    @HideFromJS
    public static NotificationData of(Context cx, Object object, TypeInfo target) {
        if (object instanceof NotificationData b) {
            return b;
        } else if (object instanceof Map<?, ?> map) {
            val decoded = CODEC.decode(JsonOps.INSTANCE, MapJS.json(map));
            return decoded.result().orElseThrow().getFirst();
        }
        return new NotificationData(TextWrapper.componentOf(object));
    }

    public static NotificationData ofText(Component title) {
        return new NotificationData(title);
    }

    public static NotificationData ofTitles(Component title, Component subTitle) {
        return new NotificationData(title.copy().append("\n").append(subTitle));
    }

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
            KubeJSCodecs.COLOR.optionalFieldOf("outlineColor", ColorWrapper.BLACK)
                .forGetter(NotificationData::outlineColor),
            KubeJSCodecs.COLOR.optionalFieldOf("borderColor", DEFAULT_BORDER_COLOR)
                .forGetter(NotificationData::borderColor),
            KubeJSCodecs.COLOR.optionalFieldOf("backgroundColor", DEFAULT_BACKGROUND_COLOR)
                .forGetter(NotificationData::backgroundColor),
            Codec.BOOL.optionalFieldOf("textShadow", true).forGetter(NotificationData::textShadow)
        ).apply(builder, NotificationData::new)
    );

    private Duration duration;
    private Component text;
    private ToastIcon icon;
    private int iconSize;
    private Color outlineColor;
    private Color borderColor;
    private Color backgroundColor;
    private boolean textShadow;

    public NotificationData(Component text) {
        this.duration = DEFAULT_DURATION;
        this.text = text;
        this.icon = NoIcon.INSTANCE;
        this.iconSize = 16;
        this.outlineColor = ColorWrapper.BLACK;
        this.borderColor = DEFAULT_BORDER_COLOR;
        this.backgroundColor = DEFAULT_BACKGROUND_COLOR;
        this.textShadow = true;
    }

    public NotificationData textureIcon(ResourceLocation textureLocation) {
        return icon(new TextureIcon(textureLocation));
    }

    public NotificationData itemIcon(ItemStack stack) {
        return icon(new ItemIcon(stack));
    }

    public NotificationData atlasIcon(ResourceLocation atlas, ResourceLocation sprite) {
        return icon(new AtlasIcon(Optional.ofNullable(atlas), sprite));
    }

    public NotificationData atlasIcon(ResourceLocation sprite) {
        return atlasIcon(null, sprite);
    }

    public NotificationData noIcon() {
        return icon(NoIcon.INSTANCE);
    }

    @Environment(EnvType.CLIENT)
    public void show() {
        val mc = Minecraft.getInstance();
        mc.getToasts().addToast(new NotificationToast(mc, this));
    }
}
