package net.teamfruit.projectrtm.rtm.modelpack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public interface IModelSelector {
	String getModelType();

	String getModelName();

	void setModelName(String par1);

	/**{x,y,z} or {entityId, -1, 0}*/
	int[] getPos();

	@SideOnly(Side.CLIENT)
	boolean closeGui(String par1);
}