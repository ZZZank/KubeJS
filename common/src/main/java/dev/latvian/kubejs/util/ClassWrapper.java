package dev.latvian.kubejs.util;

import com.github.bsideup.jabel.Desugar;
import dev.latvian.mods.rhino.NativeJavaClass;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.CustomJavaObjectWrapper;

/**
 * @author ZZZank
 */
@Desugar
public record ClassWrapper<T>(Class<T> wrappedClass) implements CustomJavaObjectWrapper {
	@Override
	public Scriptable wrapAsJavaObject(Context data, Scriptable scope, Class<?> staticType) {
		return new NativeJavaClass(scope, wrappedClass);
	}

	@Override
	public String toString() {
		return "ClassWrapper[" + wrappedClass.getName() + "]";
	}
}