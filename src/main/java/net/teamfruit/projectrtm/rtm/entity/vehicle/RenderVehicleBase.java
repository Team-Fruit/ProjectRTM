package net.teamfruit.projectrtm.rtm.entity.vehicle;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.util.vector.Vector3f;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.teamfruit.projectrtm.ngtlib.math.NGTMath;
import net.teamfruit.projectrtm.ngtlib.math.NGTVec;
import net.teamfruit.projectrtm.ngtlib.renderer.GLHelper;
import net.teamfruit.projectrtm.ngtlib.util.NGTUtil;
import net.teamfruit.projectrtm.ngtlib.util.NGTUtilClient;
import net.teamfruit.projectrtm.rtm.RTMCore;
import net.teamfruit.projectrtm.rtm.entity.train.EntityTrainBase;
import net.teamfruit.projectrtm.rtm.entity.train.util.TrainState;
import net.teamfruit.projectrtm.rtm.entity.train.util.TrainState.TrainStateType;
import net.teamfruit.projectrtm.rtm.modelpack.cfg.TrainConfig;
import net.teamfruit.projectrtm.rtm.modelpack.cfg.VehicleBaseConfig.Light;
import net.teamfruit.projectrtm.rtm.modelpack.cfg.VehicleBaseConfig.Rollsign;
import net.teamfruit.projectrtm.rtm.modelpack.modelset.ModelSetVehicleBaseClient;
import net.teamfruit.projectrtm.rtm.render.PartsRenderer;
import net.teamfruit.projectrtm.rtm.util.RenderUtil;

@SideOnly(Side.CLIENT)
public abstract class RenderVehicleBase extends Render {
	protected static final int DAYLIGHT_LIMIT = 10;

	protected NGTVec normalVec = new NGTVec(0.0D, 0.0D, 1.0D);
	protected NGTVec vecLight = new NGTVec(0.0D, 0.0D, 1.0D);
	protected Vector3f lightVecF = new Vector3f();

	protected void renderVehicleBase(final EntityVehicleBase vehicle, final double par2, final double par4, final double par6, final float par8, final float par9) {
		GL11.glPushMatrix();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glTranslatef((float) par2, (float) par4, (float) par6);

		final ModelSetVehicleBaseClient modelSet = (ModelSetVehicleBaseClient) vehicle.getModelSet();
		if (modelSet!=null) {
			final float yaw = vehicle.prevRotationYaw+MathHelper.wrapAngleTo180_float(vehicle.rotationYaw-vehicle.prevRotationYaw)*par9;
			GL11.glRotatef(yaw, 0.0F, 1.0F, 0.0F);
			final float pitch = vehicle.prevRotationPitch+(vehicle.rotationPitch-vehicle.prevRotationPitch)*par9;
			GL11.glRotatef(-pitch, 1.0F, 0.0F, 0.0F);
			final float roll = vehicle.prevRotationRoll+(vehicle.rotationRoll-vehicle.prevRotationRoll)*par9;
			GL11.glRotatef(roll, 0.0F, 0.0F, 1.0F);
			final float[] fa = modelSet.getConfig().offset;
			GL11.glTranslated(fa[0], fa[1], fa[2]);

			renderVehicleMain(vehicle, modelSet, par9);
		} else
			RTMCore.proxy.renderMissingModel();

		GL11.glPopMatrix();
	}

	/**車体&方向幕&ライトの描画*/
	protected void renderVehicleMain(final EntityVehicleBase vehicle, final ModelSetVehicleBaseClient modelSet, final float par4) {
		final boolean smoothing = modelSet.getConfig().smoothing;
		final boolean culling = modelSet.getConfig().doCulling;

		if (smoothing)
			GL11.glShadeModel(GL11.GL_SMOOTH);

		if (!culling)
			GL11.glDisable(GL11.GL_CULL_FACE);

		//EntityRenderer.1412->pass=1
		final int pass = MinecraftForgeClient.getRenderPass();
		if (pass==0)
			for (int i = 0; i<2; ++i)//0;通常, 1:発光
			{
				if (i==1&&!modelSet.vehicleModel.light)
					continue;

				if (i==1)
					renderLight(vehicle, modelSet, par4);
				else {
					//半透明ピクセルは描画しない
					GL11.glAlphaFunc(GL11.GL_EQUAL, 1.0F);//a==1.0

					//車内灯
					final Light[] lights = modelSet.getConfig().interiorLights;
					byte b = 0;
					if (vehicle instanceof EntityTrainBase)
						b = ((EntityTrainBase) vehicle).getTrainStateData(TrainStateType.State_InteriorLight.id);
					final int value = getLightValue(vehicle);
					//boolean flag = !NGTUtilClient.usingShader() && lights != null && vehicle.shouldUseInteriorLight();
					boolean flag = false;
					if (lights!=null&&vehicle.shouldUseInteriorLight()) {
						flag = true;
						final float r = b==TrainState.InteriorLight_On.data ? 1.0F : -1.0F;
						for (int k = 0; k<lights.length; ++k) {
							final float[] pos = lights[k].pos;
							RenderUtil.enableCustomLighting(k, pos[0], pos[1], pos[2], r, 1.0F, 1.0F);
						}
					}

					renderBody(vehicle, modelSet, i, par4);

					//車内灯
					if (lights!=null&&flag)
						for (int k = 0; k<lights.length; ++k)
							RenderUtil.disableCustomLighting(k);

					GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
				}
			}
		else if (pass==1)
			if (modelSet.vehicleModel.alphaBlend)//半透明部分描画
			{
				GL11.glAlphaFunc(GL11.GL_LESS, 1.0F);//a<1.0
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				renderBody(vehicle, modelSet, 1, par4);
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);//a>0.1
			}

		if (!culling)
			GL11.glEnable(GL11.GL_CULL_FACE);

		if (smoothing)
			GL11.glShadeModel(GL11.GL_FLAT);

		if (pass==0) {
			if (modelSet.rollsignTexture!=null)
				renderRollsign(vehicle, modelSet);
		} else if (pass==1)
			if (!NGTUtilClient.usingShader()) {
				GL11.glDisable(GL11.GL_CULL_FACE);
				this.renderLightEffect(vehicle, modelSet);
				GL11.glEnable(GL11.GL_CULL_FACE);
			}
	}

	protected void renderBody(final EntityVehicleBase vehicle, final ModelSetVehicleBaseClient modelSet, final int pass, final float par4) {
		modelSet.vehicleModel.renderWithTexture(vehicle, pass, par4);
	}

	/**ライトの描画*/
	protected void renderLight(final EntityVehicleBase vehicle, final ModelSetVehicleBaseClient modelSet, final float par4) {
		final boolean isTrain = vehicle instanceof EntityTrainBase;
		int dir = 0;
		byte mode = 0;
		boolean b0 = false;
		boolean b1 = false;

		if (isTrain) {
			final EntityTrainBase train = (EntityTrainBase) vehicle;
			dir = train.getTrainDirection();
			mode = train.getTrainState(TrainStateType.State_Light.id).data;
			b0 = train.getConnectedTrain(dir)==null;//front空き
			b1 = train.getConnectedTrain(1-dir)==null;//back空き
		}

		for (int i = 0; i<3; ++i)//0:消灯,1:前照灯,2:尾灯
		{
			boolean doRender = false;
			if (isTrain&&((TrainConfig) modelSet.getConfig()).isSingleTrain&&b0&&b1)
				switch (i) {
					case 0:
						doRender = mode==0||mode==1;
						break;
					case 1:
						doRender = mode==1&&dir==0||mode==2;
						break;
					case 2:
						doRender = mode==1&&dir==1||mode==2;
						break;
				}
			else
				switch (i) {
					case 0:
						doRender = mode==0||mode==1;
						break;
					case 1:
						doRender = mode==1&&b0||mode==2;
						break;
					case 2:
						doRender = mode==1&&!b0&&b1||mode==2;
						break;
				}

			if (doRender) {
				if (i!=0) {
					RenderHelper.disableStandardItemLighting();
					GL11.glEnable(GL11.GL_BLEND);
					GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.8F);
					GLHelper.setLightmapMaxBrightness();
				}

				renderBody(vehicle, modelSet, i+2, par4);

				if (i!=0) {
					GL11.glDisable(GL11.GL_BLEND);
					GL11.glEnable(GL11.GL_ALPHA_TEST);
					RenderHelper.enableStandardItemLighting();
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				}
			}
		}
	}

	/**方向幕の描画*/
	protected void renderRollsign(final EntityVehicleBase vehicle, final ModelSetVehicleBaseClient modelset) {
		GL11.glPushMatrix();
		bindTexture(modelset.rollsignTexture);

		for (int i = 0; i<modelset.getConfig().rollsigns.length; ++i) {
			final Rollsign rollsign = modelset.getConfig().rollsigns[i];

			if (!rollsign.disableLighting) {
				RenderHelper.disableStandardItemLighting();
				GLHelper.setLightmapMaxBrightness();
			}

			final float f0 = (rollsign.uv[3]-rollsign.uv[2])/modelset.getConfig().rollsignNames.length;
			final float uMin = rollsign.uv[0];
			final float uMax = rollsign.uv[1];
			float f1 = 0.0F;
			if (vehicle instanceof EntityTrainBase) {
				final EntityTrainBase train = (EntityTrainBase) vehicle;
				f1 = rollsign.doAnimation ? train.getRollsignAnimation() : train.getTrainStateData(8);
			}
			final float vMin = rollsign.uv[2]+f0*f1;
			final float vMax = rollsign.uv[2]+f0*(f1+1.0F);

			final Tessellator tessellator = Tessellator.instance;
			tessellator.startDrawingQuads();
			for (int j = 0; j<modelset.getConfig().rollsigns[i].pos.length; ++j) {
				tessellator.addVertexWithUV(rollsign.pos[j][3][0], rollsign.pos[j][3][1], rollsign.pos[j][3][2], uMin, vMin);
				tessellator.addVertexWithUV(rollsign.pos[j][2][0], rollsign.pos[j][2][1], rollsign.pos[j][2][2], uMin, vMax);
				tessellator.addVertexWithUV(rollsign.pos[j][1][0], rollsign.pos[j][1][1], rollsign.pos[j][1][2], uMax, vMax);
				tessellator.addVertexWithUV(rollsign.pos[j][0][0], rollsign.pos[j][0][1], rollsign.pos[j][0][2], uMax, vMin);
			}
			tessellator.draw();

			if (!rollsign.disableLighting)
				RenderHelper.enableStandardItemLighting();
		}

		GL11.glPopMatrix();
	}

	private int getLightValue(final EntityVehicleBase vehicle) {
		final World world = NGTUtil.getClientWorld();
		final int x = MathHelper.floor_double(vehicle.posX);
		final int y = MathHelper.floor_double(vehicle.posY+0.5D);
		final int z = MathHelper.floor_double(vehicle.posZ);
		return NGTUtil.getLightValue(world, x, y, z);
	}

	/**前照灯のボリュームライト効果*/
	private void renderLightEffect(final EntityVehicleBase vehicle, final ModelSetVehicleBaseClient modelset) {
		final boolean isTrain = vehicle instanceof EntityTrainBase;
		int dir = 0;
		byte mode = 0;
		boolean b0 = false;
		boolean b1 = false;

		if (isTrain) {
			final EntityTrainBase train = (EntityTrainBase) vehicle;
			dir = train.getTrainDirection();
			mode = train.getTrainState(TrainStateType.State_Light.id).data;
			b0 = train.getConnectedTrain(dir)==null;//front空き
			b1 = train.getConnectedTrain(1-dir)==null;//back空き
		}

		if (modelset.getConfig().headLights==null||modelset.getConfig().tailLights==null||mode<=0)
			return;

		GL11.glPushMatrix();
		RenderHelper.disableStandardItemLighting();
		GLHelper.setLightmapMaxBrightness();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glDisable(GL11.GL_ALPHA_TEST);

		final int value = getLightValue(vehicle);

		GL11.glEnable(GL11.GL_BLEND);
		//if(value > DAYLIGHT_LIMIT)
		{
			//GL11.glBlendFunc(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ONE);//合成:スクリーン
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		}
		/*else
		{
			GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);//明るさの段階的変更が不可能
		}*/
		GL11.glDepthMask(false);

		int renderModeHead = -1;
		int renderModeTail = -1;
		final boolean b2 = isTrain&&((TrainConfig) modelset.getConfig()).isSingleTrain&&b0&&b1;//単行
		if (b2) {
			renderModeHead = mode==1 ? dir : mode==2 ? 2 : -1;
			renderModeTail = mode==1 ? 1-dir : mode==2 ? 2 : -1;
		} else {
			renderModeHead = mode==1&&b0||mode==2 ? 0 : -1;
			renderModeTail = mode==1&&!b0&&b1||mode==2 ? 0 : -1;
		}

		this.normalVec.setValue(0.0D, 0.0D, 1.0D);
		this.normalVec.rotateAroundY(NGTMath.toRadians(vehicle.rotationYaw));
		this.normalVec.rotateAroundZ(NGTMath.toRadians(vehicle.rotationPitch));
		this.lightVecF.set((float) this.normalVec.xCoord, (float) this.normalVec.yCoord, (float) this.normalVec.zCoord);

		if (renderModeHead>=0)
			for (final Light light : modelset.getConfig().headLights) {
				if (light.type==1&&value>DAYLIGHT_LIMIT)
					continue;
				this.renderLightEffect(vehicle, this.lightVecF, light, renderModeHead);
			}

		if (renderModeTail>=0)
			for (final Light light : modelset.getConfig().tailLights) {
				if (light.type==1&&value>DAYLIGHT_LIMIT)
					continue;
				this.renderLightEffect(vehicle, this.lightVecF, light, renderModeTail);
			}

		GL11.glDepthMask(true);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glShadeModel(GL11.GL_FLAT);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		RenderHelper.enableStandardItemLighting();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glPopMatrix();
	}

	/**
	 * @param normal 車両の向き
	 * @param light
	 * @param mode 車体の 0:前, 1:後, 2:両側
	 */
	private void renderLightEffect(final EntityVehicleBase vehicle, final Vector3f normal, final Light light, final int mode) {
		if (mode==2) {
			this.renderLightEffect(vehicle, normal, light, 0);
			this.renderLightEffect(vehicle, normal, light, 1);
			return;
		}

		this.vecLight.setValue(light.pos[0], light.pos[1], light.pos[2]);
		this.vecLight.rotateAroundY(NGTMath.toRadians(vehicle.rotationYaw));
		this.vecLight.rotateAroundZ(NGTMath.toRadians(vehicle.rotationPitch));
		this.vecLight.addVector(vehicle.posX, vehicle.posY, vehicle.posZ);

		GL11.glPushMatrix();
		GL11.glTranslatef(light.pos[0], light.pos[1], mode==0 ? light.pos[2] : -light.pos[2]);
		if (mode==1)
			GL11.glScalef(1.0F, 1.0F, -1.0F);

		PartsRenderer.renderLightEffectS(normal, this.vecLight.xCoord, this.vecLight.yCoord, this.vecLight.zCoord,
				light.r, 0.0625F, 16.0F, light.color, light.type, mode==1);

		if (mode==1)
			GL11.glScalef(1.0F, 1.0F, -1.0F);

		GL11.glPopMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(final Entity par1) {
		return null;
	}

	@Override
	protected void bindEntityTexture(final Entity entiy) {
	}

	@Override
	public void doRender(final Entity par1, final double par2, final double par4, final double par6, final float par8, final float par9) {
		renderVehicleBase((EntityVehicleBase) par1, par2, par4, par6, par8, par9);
	}
}