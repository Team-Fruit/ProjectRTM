package net.teamfruit.projectrtm.rtm.modelpack.texture;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.teamfruit.projectrtm.ngtlib.io.IProgressWatcher;
import net.teamfruit.projectrtm.ngtlib.io.NGTFileLoadException;
import net.teamfruit.projectrtm.ngtlib.io.NGTFileLoader;
import net.teamfruit.projectrtm.ngtlib.io.NGTJson;
import net.teamfruit.projectrtm.ngtlib.io.NGTLog;

public final class TextureManager {
	public static final TextureManager INSTANCE = new TextureManager();

	private final Map<TexturePropertyType, Map<String, TextureProperty>> allTextureMap = new HashMap<TexturePropertyType, Map<String, TextureProperty>>();
	//private final Map<TexturePropertyType, Map<String, TextureProperty>> smpTextureMap = new HashMap<TexturePropertyType, Map<String, TextureProperty>>();

	private TextureManager() {
	}

	public void load(IProgressWatcher par1) {
		for (int i = 0; i<TexturePropertyType.values().length; ++i) {
			TexturePropertyType tpt = TexturePropertyType.values()[i];
			if (!tpt.useJson) {
				continue;
			}

			Map<String, TextureProperty> map = new HashMap<String, TextureProperty>();
			this.allTextureMap.put(tpt, map);

			par1.setValue(0, 3, "Loading Textures");
			List<File> fileList = NGTFileLoader.findFile(tpt.toString(), ".json", "");
			par1.setValue(0, 4, "Registering ModelPack");
			par1.setMaxValue(1, fileList.size(), "");
			int count = 0;
			for (File file : fileList) {
				++count;
				String json = NGTJson.readFromJson(file);
				try {
					TextureProperty property = (TextureProperty) NGTJson.getObjectFromJson(json, tpt.type);
					if (property!=null) {
						property.init();
						map.put(property.texture, property);
						par1.setValue(1, count, property.texture);
						NGTLog.debug("Register Texture : %s (%s)", property.texture, tpt.toString());
					}
				} catch (NGTFileLoadException e) {
					e.printStackTrace();
				}
			}
		}

		Map<String, TextureProperty> map = new HashMap<String, TextureProperty>();
		this.allTextureMap.put(TexturePropertyType.RRS, map);

		par1.setValue(0, 5, "Loading RailloadSign");
		List<File> fileList = NGTFileLoader.findFile("rrs_", ".png", "");
		par1.setValue(0, 6, "Loading RailloadSign");
		par1.setMaxValue(1, fileList.size(), "");
		int count = 0;
		for (File file : fileList) {
			++count;
			String name = file.getName();
			RRSProperty prop = new RRSProperty(name);
			prop.init();
			map.put(prop.texture, prop);
			par1.setValue(1, count, name);
			NGTLog.debug("Register Texture : %s (RRS)", name);
		}
	}

	public <T extends TextureProperty> T getProperty(TexturePropertyType type, String key) {
		//Map<String, SignBoardProperty> map = NGTUtil.isSMP() ? this.smpTextureMap : this.allTextureMap;
		if (this.allTextureMap.containsKey(type)) {
			return (T) this.allTextureMap.get(type).get(key);//Serverでぬるぽ出ないように
		}
		return null;
	}

	public List<TextureProperty> getTextureList(TexturePropertyType type) {
		List<TextureProperty> list = new ArrayList<TextureProperty>();
		Map<TexturePropertyType, Map<String, TextureProperty>> map = this.allTextureMap;
		for (TextureProperty prop : map.get(type).values()) {
			list.add(prop);
		}

		Collections.sort(list, new Comparator<TextureProperty>() {
			@Override
			public int compare(TextureProperty o1, TextureProperty o2) {
				return o1.texture.compareTo(o2.texture);
			}
		});
		return list;
	}

	public enum TexturePropertyType {
		SignBoard(SignBoardProperty.class, true),
		Flag(FlagProperty.class, true),
		RRS(RRSProperty.class, false);

		public final Class<? extends TextureProperty> type;
		public final boolean useJson;

		private TexturePropertyType(Class<? extends TextureProperty> clazz, boolean par2) {
			this.type = clazz;
			this.useJson = par2;
		}
	}
}