package net.teamfruit.projectrtm.rtm.modelpack.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.model.ModelChest;
import net.teamfruit.projectrtm.ngtlib.io.FileType;
import net.teamfruit.projectrtm.ngtlib.renderer.model.GroupObject;
import net.teamfruit.projectrtm.ngtlib.renderer.model.IModelNGT;
import net.teamfruit.projectrtm.ngtlib.renderer.model.Material;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelContainer_BigChest implements IModelNGT {
	private ModelChest model = new ModelChest();

	@Override
	public void renderAll(boolean smoothing) {
		GL11.glPushMatrix();
		GL11.glScalef(1.0F, -1.0F, -1.0F);
		GL11.glTranslatef(-1.5F, -3.0F, -1.5F);
		float scale = 3.0F;
		GL11.glScalef(scale, scale, scale);
		this.model.renderAll();
		GL11.glPopMatrix();
	}

	@Override
	public void renderOnly(boolean smoothing, String... groupNames) {
		;
	}

	@Override
	public void renderPart(boolean smoothing, String partName) {
		;
	}

	@Override
	public int getDrawMode() {
		return 0;
	}

	@Override
	public ArrayList<GroupObject> getGroupObjects() {
		return new ArrayList<GroupObject>();
	}

	@Override
	public Map<String, Material> getMaterials() {
		return new HashMap<String, Material>();
	}

	@Override
	public FileType getType() {
		return FileType.CLASS;
	}
}