package net.teamfruit.projectrtm.ngtlib.renderer.model;

import net.minecraft.util.ResourceLocation;
import net.teamfruit.projectrtm.ngtlib.io.FileType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class ModelLoader {
	public static PolygonModel loadModel(String path, VecAccuracy par1, Object... args) {
		return loadModel(new ResourceLocation(path), par1, args);
	}

	public static PolygonModel loadModel(ResourceLocation resource, VecAccuracy par1, Object... args) {
		if (FileType.OBJ.match(resource.getResourcePath())) {
			return new ObjModel(resource, par1);
		} else if (FileType.MQO.match(resource.getResourcePath())) {
			if (args.length>0) {
				return new MqoModel(resource, (Integer) args[0], par1);
			} else {
				return new MqoModel(resource, par1);
			}
		}
		return null;
	}
}