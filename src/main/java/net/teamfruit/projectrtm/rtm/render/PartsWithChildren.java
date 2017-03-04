package net.teamfruit.projectrtm.rtm.render;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PartsWithChildren extends Parts {
	public final List<Parts> childParts = new ArrayList<Parts>();

	public PartsWithChildren(String... par1) {
		super(par1);
	}

	public void addParts(Parts par1) {
		this.childParts.add(par1);
	}

	@Override
	public void init(PartsRenderer renderer) {
		super.init(renderer);

		for (Parts parts : this.childParts) {
			parts.init(renderer);
		}
	}
}