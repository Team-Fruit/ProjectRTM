package net.teamfruit.projectrtm.rtm.block;

import net.teamfruit.projectrtm.ngtlib.block.RenderBlockLiquidBase;
import net.teamfruit.projectrtm.rtm.RTMBlock;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBlockLiquid extends RenderBlockLiquidBase {
	@Override
	public int getRenderId() {
		return RTMBlock.renderIdLiquid;
	}
}