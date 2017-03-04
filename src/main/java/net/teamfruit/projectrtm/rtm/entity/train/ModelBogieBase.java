package net.teamfruit.projectrtm.rtm.entity.train;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.teamfruit.projectrtm.ngtlib.renderer.model.MCModel;

@SideOnly(Side.CLIENT)
public abstract class ModelBogieBase extends MCModel {
	public ModelBogieBase() {
		this(256, 256);
	}

	public ModelBogieBase(int width, int height) {
		this.init();
	}

	public abstract void init();
}