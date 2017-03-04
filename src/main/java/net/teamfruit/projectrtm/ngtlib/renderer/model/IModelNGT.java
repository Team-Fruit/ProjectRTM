package net.teamfruit.projectrtm.ngtlib.renderer.model;

import static cpw.mods.fml.relauncher.Side.*;

import java.util.ArrayList;
import java.util.Map;

import cpw.mods.fml.relauncher.SideOnly;
import net.teamfruit.projectrtm.ngtlib.io.FileType;

/**モデルファイルのデータを格納*/
@SideOnly(CLIENT)
public interface IModelNGT {
	void renderAll(boolean smoothing);

	void renderOnly(boolean smoothing, String... groupNames);

	void renderPart(boolean smoothing, String partName);

	/**GL_QUADS or GL_TRIANGLES*/
	int getDrawMode();

	ArrayList<GroupObject> getGroupObjects();

	Map<String, Material> getMaterials();

	FileType getType();
}