package dev.latvian.kubejs.bindings;

import com.google.gson.JsonParseException;
import dev.latvian.kubejs.util.JSObjectType;
import dev.latvian.kubejs.util.ListJS;
import dev.latvian.kubejs.util.MapJS;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.annotations.typing.JSInfo;
import dev.latvian.mods.rhino.mod.wrapper.ColorWrapper;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@JSInfo("The hub for all things text components. Format text to your hearts content!")
public class TextWrapper0 {
    @JSInfo("Returns a Component of the input")
    public static MutableComponent of(@Nullable Object o) {
        o = UtilsJS.wrap(o, JSObjectType.ANY);
        if (o == null) {
            return literal("null");
        } else if (o instanceof MutableComponent component) {
            return component;
        } else if (o instanceof Component component) {
            return component.copy();
        } else if (o instanceof CharSequence || o instanceof Number || o instanceof Character) {
            return literal(o.toString());
        } else if (o instanceof Enum<?> e) {
            return literal(e.name());
        } else if (o instanceof StringTag tag) {
            var s = tag.getAsString();
            if (s.startsWith("{") && s.endsWith("}")) {
                try {
                    return Component.Serializer.fromJson(s);
                } catch (JsonParseException ex) {
                    return literal("Error: " + ex);
                }
            } else {
                return literal(s);
            }
        } else if (o instanceof Map<?, ?> map && (map.containsKey("text") || map.containsKey("translate"))) {
            MutableComponent text;

            if (map.containsKey("text")) {
                text = literal(map.get("text").toString());
            } else {
                Object[] with;

                if (map.get("with") instanceof Collection<?> a) {
                    with = new Object[a.size()];
                    var i = 0;

                    for (var e1 : a) {
                        with[i] = e1;

                        if (with[i] instanceof MapJS || with[i] instanceof ListJS) {
                            with[i] = of(e1);
                        }

                        i++;
                    }
                } else {
                    with = new Object[0];
                }

                text = translatable(map.get("translate").toString(), with);
            }

            if (map.containsKey("color")) {
                text.kjs$color(ColorWrapper.of(map.get("color")));
            }

            text.kjs$bold((Boolean) map.getOrDefault("bold", null));
            text.kjs$italic((Boolean) map.getOrDefault("italic", null));
            text.kjs$underlined((Boolean) map.getOrDefault("underlined", null));
            text.kjs$strikethrough((Boolean) map.getOrDefault("strikethrough", null));
            text.kjs$obfuscated((Boolean) map.getOrDefault("obfuscated", null));

            text.kjs$insertion((String) map.getOrDefault("insertion", null));
            text.kjs$font(map.containsKey("font") ? new ResourceLocation(map.get("font").toString()) : null);
            text.kjs$click(map.containsKey("click") ? clickEventOf(map.get("click")) : null);
            text.kjs$hover(map.containsKey("hover") ? of(map.get("hover")) : null);

            if (map.get("extra") instanceof Iterable<?> itr) {
                for (var e : itr) {
                    text.append(of(e));
                }
            }

            return text;
        } else if (o instanceof Iterable<?> list) {
            var text = empty();

            for (var e1 : list) {
                text.append(of(e1));
            }

            return text;
        }

        return literal(o.toString());
    }

    @JSInfo("Checks if the passed in component, and all its children are empty")
    public static boolean isEmpty(Component component) {
        return component.getContents().isEmpty() && component.getSiblings().isEmpty();
    }

    @JSInfo("Returns a ClickEvent of the input")
    public static ClickEvent clickEventOf(Object o) {
        if (o == null) {
            return null;
        } else if (o instanceof ClickEvent ce) {
            return ce;
        }

        var json = MapJS.json(o);
        if (json != null) {
            var action = GsonHelper.getAsString(json, "action");
            var value = GsonHelper.getAsString(json, "value");
            return new ClickEvent(Objects.requireNonNull(ClickEvent.Action.getByName(action), "Invalid click event action %s!".formatted(action)), value);
        }

        var s = o.toString();

        var split = s.split(":", 2);

        return switch (split[0]) {
            case "command" -> new ClickEvent(ClickEvent.Action.RUN_COMMAND, split[1]);
            case "suggest_command" -> new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, split[1]);
            case "copy" -> new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, split[1]);
            case "file" -> new ClickEvent(ClickEvent.Action.OPEN_FILE, split[1]);
            default -> {
                var action = ClickEvent.Action.getByName(split[0]);
                if (action != null) {
                    yield new ClickEvent(action, split[1]);
                }

                yield new ClickEvent(ClickEvent.Action.OPEN_URL, s);
            }
        };
    }

    @JSInfo("Returns a colorful representation of the input nbt. Useful for displaying NBT to the player")
    public static Component prettyPrintNbt(Tag tag) {
        return NbtUtils.toPrettyComponent(tag);
    }

    @JSInfo("Joins all components in the list with the separator component")
    public static MutableComponent join(MutableComponent separator, Iterable<? extends Component> texts) {
        var joined = empty();
        var first = true;

        for (var t : texts) {
            if (first) {
                first = false;
            } else if (!isEmpty(separator)) {
                joined.append(separator);
            }

            joined.append(t);
        }

        return joined;
    }

    @JSInfo("Returns an empty component")
    public static MutableComponent empty() {
        return new TextComponent("");
    }

    @JSInfo("Joins all components")
    public static MutableComponent join(Component... texts) {
        return join(empty(), Arrays.asList(texts));
    }

    @JSInfo("Returns a plain component of the passed in string, even if empty")
    public static MutableComponent string(String text) {
        return Component.literal(text);
    }

    @JSInfo("Returns a plain component of the input")
    public static MutableComponent literal(String text) {
        return new TextComponent(text);
    }

    @JSInfo("Returns a translatable component of the input key")
    public static MutableComponent translate(String key) {
        return new TranslatableComponent(key);
    }

    @JSInfo("Returns a translatable component of the input key, with args of the objects")
    public static MutableComponent translate(String key, Object... objects) {
        return new TranslatableComponent(key, objects);
    }

    @JSInfo("Returns a translatable component of the input key")
    public static MutableComponent translatable(String key) {
        return new TranslatableComponent(key);
    }

    @JSInfo("Returns a translatable component of the input key, with args of the objects")
    public static MutableComponent translatable(String key, Object... objects) {
        return new TranslatableComponent(key, objects);
    }

    @JSInfo("Returns a keybinding component of the input keybinding descriptor")
    public static MutableComponent keybind(String keybind) {
        return new KeybindComponent(keybind);
    }

    @JSInfo("Returns a score component of the input objective, for the provided selector")
    public static MutableComponent score(String selector, String objective) {
        return new ScoreComponent(selector, objective);
    }

    @JSInfo("Returns a component displaying all entities matching the input selector")
    public static MutableComponent selector(String selector) {
        return new SelectorComponent(selector);
    }

//    @JSInfo("Returns a component displaying all entities matching the input selector, with a custom separator")
//    public static MutableComponent selector(String selector, Component separator) {
//        return new SelectorComponent(selector, Optional.of(separator));
//    }

    @JSInfo("Returns a component of the input, colored black")
    public static MutableComponent black(Object text) {
        return of(text).kjs$black();
    }

    @JSInfo("Returns a component of the input, colored dark blue")
    public static MutableComponent darkBlue(Object text) {
        return of(text).kjs$darkBlue();
    }

    @JSInfo("Returns a component of the input, colored dark green")
    public static MutableComponent darkGreen(Object text) {
        return of(text).kjs$darkGreen();
    }

    @JSInfo("Returns a component of the input, colored dark aqua")
    public static MutableComponent darkAqua(Object text) {
        return of(text).kjs$darkAqua();
    }

    @JSInfo("Returns a component of the input, colored dark red")
    public static MutableComponent darkRed(Object text) {
        return of(text).kjs$darkRed();
    }

    @JSInfo("Returns a component of the input, colored dark purple")
    public static MutableComponent darkPurple(Object text) {
        return of(text).kjs$darkPurple();
    }

    @JSInfo("Returns a component of the input, colored gold")
    public static MutableComponent gold(Object text) {
        return of(text).kjs$gold();
    }

    @JSInfo("Returns a component of the input, colored gray")
    public static MutableComponent gray(Object text) {
        return of(text).kjs$gray();
    }

    @JSInfo("Returns a component of the input, colored dark gray")
    public static MutableComponent darkGray(Object text) {
        return of(text).kjs$darkGray();
    }

    @JSInfo("Returns a component of the input, colored blue")
    public static MutableComponent blue(Object text) {
        return of(text).kjs$blue();
    }

    @JSInfo("Returns a component of the input, colored green")
    public static MutableComponent green(Object text) {
        return of(text).kjs$green();
    }

    @JSInfo("Returns a component of the input, colored aqua")
    public static MutableComponent aqua(Object text) {
        return of(text).kjs$aqua();
    }

    @JSInfo("Returns a component of the input, colored red")
    public static MutableComponent red(Object text) {
        return of(text).kjs$red();
    }

    @JSInfo("Returns a component of the input, colored light purple")
    public static MutableComponent lightPurple(Object text) {
        return of(text).kjs$lightPurple();
    }

    @JSInfo("Returns a component of the input, colored yellow")
    public static MutableComponent yellow(Object text) {
        return of(text).kjs$yellow();
    }

    @JSInfo("Returns a component of the input, colored white")
    public static MutableComponent white(Object text) {
        return of(text).kjs$white();
    }
}