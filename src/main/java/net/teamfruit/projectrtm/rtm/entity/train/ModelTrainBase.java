package net.teamfruit.projectrtm.rtm.entity.train;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.teamfruit.projectrtm.ngtlib.renderer.model.MCModel;

@SideOnly(Side.CLIENT)
public abstract class ModelTrainBase extends MCModel {
	public ModelTrainBase() {
		this(1024, 1024);
	}

	public ModelTrainBase(int width, int height) {
		this.init();
	}

	public abstract void init();
}