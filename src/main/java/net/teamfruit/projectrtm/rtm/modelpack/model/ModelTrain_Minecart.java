package net.teamfruit.projectrtm.rtm.modelpack.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelMinecart;
import net.teamfruit.projectrtm.ngtlib.io.FileType;
import net.teamfruit.projectrtm.ngtlib.renderer.model.GroupObject;
import net.teamfruit.projectrtm.ngtlib.renderer.model.IModelNGT;
import net.teamfruit.projectrtm.ngtlib.renderer.model.Material;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelTrain_Minecart implements IModelNGT {
	private ModelBase model = new ModelMinecart();

	@Override
	public void renderAll(boolean smoothing) {
		GL11.glPushMatrix();
		GL11.glScalef(1.0F, -1.0F, -1.0F);
		GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
		this.model.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F*3.0F);
		GL11.glPopMatrix();
	}

	@Override
	public void renderOnly(boolean smoothing, String... groupNames) {
		this.renderAll(smoothing);
	}

	@Override
	public void renderPart(boolean smoothing, String partName) {
		this.renderAll(smoothing);
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