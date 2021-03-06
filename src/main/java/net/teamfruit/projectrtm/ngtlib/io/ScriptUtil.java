package net.teamfruit.projectrtm.ngtlib.io;

import java.io.IOException;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import net.minecraft.util.ResourceLocation;

public final class ScriptUtil {
	//public static final PackageRegister REGISTER = new PackageRegister();

	/**JavaScriptの実行*/
	public static ScriptEngine doScript(ResourceLocation resource) {
		try {
			String script = NGTText.getText(resource, true);
			return doScript(script);
		} catch (IOException e) {
			throw new RuntimeException("Script load error : "+resource.getResourcePath(), e);
		}
	}

	public static ScriptEngine doScript(String s) {
		ScriptEngine se = (new ScriptEngineManager(null)).getEngineByName("js");//引数にnull入れないと20でぬるぽ

		try {
			if (se.toString().contains("Nashorn")) {
				//Java8ではimportPackage()が使えないので、その対策
				se.eval("load(\"nashorn:mozilla_compat.js\");");
			}

			//se.put("packreg", REGISTER);

			se.eval(s);
			return se;
		} catch (ScriptException e) {
			throw new RuntimeException("Script exec error", e);
		}
	}

	public static Object doScriptFunction(ScriptEngine se, String func, Object... args) {
		try {
			return ((Invocable) se).invokeFunction(func, args);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("Script exec error : "+func, e);
		} catch (ScriptException e) {
			throw new RuntimeException("Script exec error : "+func, e);
		}
	}

	public static void doScriptIgnoreError(ScriptEngine se, String func, Object... args) {
		try {
			((Invocable) se).invokeFunction(func, args);
		} catch (Exception e) {
			;
		}
	}

	public static Object getScriptField(ScriptEngine se, String fieldName) {
		return se.get(fieldName);
	}

}