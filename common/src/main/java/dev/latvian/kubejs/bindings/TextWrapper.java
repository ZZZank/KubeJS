package dev.latvian.kubejs.bindings;

import com.google.gson.JsonObject;
import dev.latvian.kubejs.fluid.FluidStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.text.TextKeybind;
import dev.latvian.kubejs.text.TextString;
import dev.latvian.kubejs.text.TextTranslate;
import dev.latvian.kubejs.util.MapJS;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.annotations.typing.JSInfo;
import net.minecraft.network.chat.*;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.Objects;

/**
 * @author LatvianModder
 */
public class TextWrapper {
	public static Text of(Object object) {
		return Text.of(object);
	}

    @JSInfo("Joins all components in the list with the separator")
	public static Text join(Text separator, Iterable<Text> texts) {
		return Text.join(separator, texts);
	}

    @JSInfo("Checks if the passed in component, and all its children are empty")
    public static boolean isEmpty(Component component) {
        return component.getContents().isEmpty() && component.getSiblings().isEmpty();
    }

    @JSInfo("get a new, empty text")
    public static TextString empty() {
        return new TextString("");
    }

    @JSInfo("""
        Returns a plain text of the input
        
        non-string object will be force-converted into string""")
	public static TextString string(Object text) {
		return new TextString(text);
	}

    @JSInfo("Returns a translatable text of the input key")
	public static TextTranslate translate(String key) {
		return new TextTranslate(key, UtilsJS.EMPTY_OBJECT_ARRAY);
	}

    @JSInfo("Returns a translatable text of the input key, with args of the key")
	public static TextTranslate translate(String key, Object... objects) {
		return new TextTranslate(key, objects);
	}

    @JSInfo("Returns a keybinding Text of the input keybinding descriptor")
	public static TextKeybind keybind(String keybind) {
		return new TextKeybind(keybind);
	}

//    @JSInfo("Returns a score component of the input objective, for the provided selector")
//    public static ScoreComponent score(String selector, String objective) {
//        return new ScoreComponent(selector, objective);
//    }

//    @JSInfo("Returns a component displaying all entities matching the input selector")
//    public static SelectorComponent selector(String selector) {
//        return new SelectorComponent(selector);
//    }

    public static Text fromComponent(Component c) {
        var t = c instanceof TranslatableComponent transl
            ? new TextTranslate(transl.getKey(), transl.getArgs())
            : new TextString(c.getContents());

        var style = c.getStyle();
        t.bold(style.isBold())
            .italic(style.isItalic())
            .underlined(style.isUnderlined())
            .strikethrough(style.isStrikethrough())
            .obfuscated(style.isObfuscated())
            .insertion(style.getInsertion())
            .click(style.getClickEvent())
            .hover(style.getHoverEvent());

        for (var sibling : c.getSiblings()) {
            t.append(fromComponent(sibling));
        }

        return t;
    }

    public static Text black(Object text) {
		return of(text).black();
	}

	public static Text darkBlue(Object text) {
		return of(text).darkBlue();
	}

	public static Text darkGreen(Object text) {
		return of(text).darkGreen();
	}

	public static Text darkAqua(Object text) {
		return of(text).darkAqua();
	}

	public static Text darkRed(Object text) {
		return of(text).darkRed();
	}

	public static Text darkPurple(Object text) {
		return of(text).darkPurple();
	}

	public static Text gold(Object text) {
		return of(text).gold();
	}

	public static Text gray(Object text) {
		return of(text).gray();
	}

	public static Text darkGray(Object text) {
		return of(text).darkGray();
	}

	public static Text blue(Object text) {
		return of(text).blue();
	}

	public static Text green(Object text) {
		return of(text).green();
	}

	public static Text aqua(Object text) {
		return of(text).aqua();
	}

	public static Text red(Object text) {
		return of(text).red();
	}

	public static Text lightPurple(Object text) {
		return of(text).lightPurple();
	}

	public static Text yellow(Object text) {
		return of(text).yellow();
	}

	public static Text white(Object text) {
		return of(text).white();
	}

    @JSInfo("Returns a HoverEvent of the input")
    public static HoverEvent hoverEventOf(Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof HoverEvent hoverEvent) {
            return hoverEvent;
        }
        //vanilla json / kjs map
        else if (o instanceof JsonObject || o instanceof MapJS) {
            return HoverEvent.deserialize(MapJS.json(o));
        }
        //item / block / fluid
        else if (o instanceof ItemLike || o instanceof ItemStack || o instanceof ItemStackJS
            || o instanceof FluidStackJS) {
            return new HoverEvent(
                HoverEvent.Action.SHOW_ITEM,
                new HoverEvent.ItemStackInfo(ItemStackJS.of(o).getItemStack())
            );
        }
        //entity
        else if (o instanceof Entity entity) {
            return new HoverEvent(
                HoverEvent.Action.SHOW_ENTITY,
                new HoverEvent.EntityTooltipInfo(entity.getType(), entity.getUUID(), entity.getName())
            );
        }
        //fallback to text
        else {
            return new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                of(o).component()
            );
        }
    }

    @JSInfo("""
        Returns a ClickEvent of the input""")
    public static ClickEvent clickEventOf(Object o) {
        if (o == null) {
            return null;
        }
        //raw
        if (o instanceof ClickEvent ce) {
            return ce;
        }
        //json
        var json = MapJS.json(o);
        if (json != null) {
            var action = GsonHelper.getAsString(json, "action");
            var value = GsonHelper.getAsString(json, "value");
            return new ClickEvent(
                Objects.requireNonNull(ClickEvent.Action.getByName(action), "Invalid click event action " + action + "!"),
                value
            );
        }
        //string
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

//    @JSInfo("Returns a component displaying all entities matching the input selector, with a custom separator")
//    public static MutableComponent selector(String selector, Component separator) {
//        return new SelectorComponent(selector, Optional.of(separator));
//    }

//    @JSInfo("Returns a colorful representation of the input nbt. Useful for displaying NBT to the player")
//    public static Component prettyPrintNbt(Tag tag) {
//        return NbtUtils.toPrettyComponent(tag);
//    }
}