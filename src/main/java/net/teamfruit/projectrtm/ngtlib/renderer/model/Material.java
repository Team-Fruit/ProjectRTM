package net.teamfruit.projectrtm.ngtlib.renderer.model;

import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class Material {
	public final byte id;
	public final ResourceLocation texture;
	//public float[] color = {1.0F, 1.0F, 1.0F, 1.0F};

	public Material(byte par1, ResourceLocation par2) {
		this.id = par1;
		this.texture = par2;
	}
}