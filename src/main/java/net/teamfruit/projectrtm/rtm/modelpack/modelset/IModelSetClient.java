package net.teamfruit.projectrtm.rtm.modelpack.modelset;

import net.minecraft.client.Minecraft;
import net.teamfruit.projectrtm.rtm.gui.GuiButtonSelectModel;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IModelSetClient {
	void renderSelectButton(GuiButtonSelectModel par1, Minecraft par2, int par3, int par4);

	void renderModelInGui(Minecraft par1);
}