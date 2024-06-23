package dev.latvian.kubejs.bindings;

import com.google.gson.JsonObject;
import dev.latvian.kubejs.fluid.FluidStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.text.TextKeybind;
import dev.latvian.kubejs.text.TextString;
import dev.latvian.kubejs.text.TextTranslate;
import dev.latvian.kubejs.util.MapJS;
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

	public static Text join(Text separator, Iterable<Text> texts) {
		return Text.join(separator, texts);
	}

	public static Text string(Object text) {
		return new TextString(text);
	}

	public static Text translate(String key) {
		return new TextTranslate(key, new Object[0]);
	}

	public static Text translate(String key, Object... objects) {
		return new TextTranslate(key, objects);
	}

	public static Text keybind(String keybind) {
		return new TextKeybind(keybind);
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
        } else if (o instanceof ClickEvent ce) {
            return ce;
        }

        var json = MapJS.json(o);
        if (json != null) {
            var action = GsonHelper.getAsString(json, "action");
            var value = GsonHelper.getAsString(json, "value");
            return new ClickEvent(
                Objects.requireNonNull(ClickEvent.Action.getByName(action), "Invalid click event action " + action + "!"),
                value
            );
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
}