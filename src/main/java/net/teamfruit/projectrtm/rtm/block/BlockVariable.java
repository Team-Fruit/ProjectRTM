package net.teamfruit.projectrtm.rtm.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.teamfruit.projectrtm.rtm.RTMBlock;

public class BlockVariable extends Block {
	public BlockVariable() {
		super(Material.rock);
		this.setLightOpacity(0);
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public int getRenderType() {
		return RTMBlock.renderIdVariableBlock;
	}
}