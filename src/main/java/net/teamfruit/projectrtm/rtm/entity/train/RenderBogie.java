package net.teamfruit.projectrtm.rtm.entity.train;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.teamfruit.projectrtm.rtm.RTMCore;
import net.teamfruit.projectrtm.rtm.modelpack.cfg.VehicleBaseConfig;
import net.teamfruit.projectrtm.rtm.modelpack.modelset.ModelSetTrainClient;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBogie extends Render {
	public RenderBogie() {
		super();
		this.shadowSize = 1.0F;
	}

	private final void renderBogie(EntityBogie bogie, double par2, double par4, double par6, float par8, float par9) {
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glTranslatef((float) par2, (float) par4, (float) par6);
		float yaw = bogie.prevRotationYaw+MathHelper.wrapAngleTo180_float(bogie.rotationYaw-bogie.prevRotationYaw)*par9;
		GL11.glRotatef(yaw, 0.0F, 1.0F, 0.0F);
		float pitch = bogie.prevRotationPitch+(bogie.rotationPitch-bogie.prevRotationPitch)*par9;
		GL11.glRotatef(-pitch, 1.0F, 0.0F, 0.0F);

		byte index = bogie.getBogieId();
		boolean flag = true;
		if (bogie.getTrain()!=null) {
			ModelSetTrainClient modelset = (ModelSetTrainClient) bogie.getTrain().getModelSet();
			if (!modelset.isDummy()) {
				VehicleBaseConfig cfg = modelset.getConfig();
				modelset.bogieModels[index].render(bogie, cfg, 0, par9);
				flag = false;
			}
		}

		if (flag) {
			RTMCore.proxy.renderMissingModel();
		}

		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glPopMatrix();
	}

	@Override
	public void doRender(Entity par1, double par2, double par4, double par6, float par8, float par9) {
		this.renderBogie((EntityBogie) par1, par2, par4, par6, par8, par9);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return new ResourceLocation("textures/train/bogie.png");
	}
}