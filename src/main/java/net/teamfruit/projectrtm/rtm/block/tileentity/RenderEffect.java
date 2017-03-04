package net.teamfruit.projectrtm.rtm.block.tileentity;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.teamfruit.projectrtm.ngtlib.renderer.GLHelper;
import net.teamfruit.projectrtm.ngtlib.renderer.model.IModelNGT;
import net.teamfruit.projectrtm.ngtlib.renderer.model.ModelLoader;
import net.teamfruit.projectrtm.ngtlib.renderer.model.VecAccuracy;
import net.teamfruit.projectrtm.rtm.RTMCore;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderEffect extends TileEntitySpecialRenderer {
	private static final ResourceLocation tex_N = new ResourceLocation("rtm", "textures/effect/explosionN.png");
	private static final ResourceLocation tex_S = new ResourceLocation("rtm", "textures/effect/explosionS.png");
	private static final ResourceLocation tex_R = new ResourceLocation("rtm", "textures/effect/explosionR.png");

	private IModelNGT model;
	private IModelNGT sphere;
	private IModelNGT ring;
	private boolean finishLoading;

	private static final String[] partNames = { "obj1", "obj2", "obj3", "obj4", "obj5" };

	public RenderEffect() {
		Thread thread = new Thread() {
			@Override
			public void run() {
				RenderEffect.this.model = ModelLoader.loadModel(new ResourceLocation("rtm", "models/Model_ExplosionN.mqo"), VecAccuracy.MEDIUM);
				RenderEffect.this.sphere = ModelLoader.loadModel(new ResourceLocation("rtm", "models/Model_ExplosionS.mqo"), VecAccuracy.MEDIUM);
				RenderEffect.this.ring = ModelLoader.loadModel(new ResourceLocation("rtm", "models/Model_ExplosionR.mqo"), VecAccuracy.MEDIUM);
				RenderEffect.this.finishLoading = true;
			}
		};
		thread.start();
	}

	private final void render(TileEntityEffect par1, double par2, double par3, double par4, float par5) {
		if (!this.finishLoading) {
			return;
		}

		GL11.glPushMatrix();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glTranslatef((float) par2+0.5F, (float) par3+0.5F, (float) par4+0.5F);

		if (par1.tickCount<TileEntityEffect.Phase1) {
			float alpha = 1.0F-((float) par1.tickCount/(float) TileEntityEffect.Phase1);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDepthMask(true);
			GLHelper.setLightmapMaxBrightness();
			GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);

			//大きさ10m
			float size = (-0.025F*(float) Math.pow((float) par1.tickCount-20.0F, 2.0D)+10.0F)*0.25F*TileEntityEffect.Scale;
			GL11.glScalef(size, size, size);
			this.bindTexture(tex_S);
			GL11.glMatrixMode(GL11.GL_TEXTURE);
			GL11.glLoadIdentity();
			float f0 = (float) (par1.tickCount%100)+par5;
			GL11.glTranslatef(0.0F, -f0*0.01F, 0.0F);
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			this.sphere.renderAll(RTMCore.smoothing);
			GL11.glMatrixMode(GL11.GL_TEXTURE);
			GL11.glLoadIdentity();
			GL11.glMatrixMode(GL11.GL_MODELVIEW);

			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_ALPHA_TEST);
			GL11.glDepthMask(true);

		} else {
			float f0 = (float) (par1.tickCount-TileEntityEffect.Phase1);
			float size1 = (float) (par1.getSigmoid(f0, TileEntityEffect.Slope1)*TileEntityEffect.Scale);
			float size2 = (float) (par1.getSigmoid(f0, TileEntityEffect.Slope2)*TileEntityEffect.Scale);

			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);//アルファブレンド
			GL11.glDepthMask(true);
			float color = (1.0F-(f0/(float) (TileEntityEffect.Phase3-TileEntityEffect.Phase1)));
			GL11.glColor4f(color, color, color, color);
			GLHelper.setLightmapMaxBrightness();

			this.bindTexture(tex_N);
			GL11.glMatrixMode(GL11.GL_TEXTURE);
			GL11.glLoadIdentity();
			float f1 = (float) (par1.tickCount%100)+par5;
			GL11.glTranslatef(0.0F, f1*0.01F, 0.0F);
			GL11.glMatrixMode(GL11.GL_MODELVIEW);

			GL11.glPushMatrix();
			GL11.glScalef(size1, size1, size1);
			this.model.renderPart(RTMCore.smoothing, "objC");
			GL11.glPopMatrix();

			GL11.glPushMatrix();
			GL11.glScalef(size1, size2, size1);
			this.model.renderPart(RTMCore.smoothing, "objB");
			GL11.glPopMatrix();

			GL11.glPushMatrix();
			GL11.glScalef(size2, size2, size2);
			float color2 = color*0.75F;
			GL11.glColor4f(color2, color2, color2, color2);
			float f2 = (float) (par1.tickCount%2880)*0.125F;
			GL11.glRotatef(f2, 0.0F, 1.0F, 0.0F);
			this.model.renderPart(RTMCore.smoothing, "objD_1");
			GL11.glRotatef(-f2*2.0F, 0.0F, 1.0F, 0.0F);
			this.model.renderPart(RTMCore.smoothing, "objD_2");
			GL11.glPopMatrix();

			GL11.glMatrixMode(GL11.GL_TEXTURE);
			GL11.glLoadIdentity();
			GL11.glTranslatef(0.0F, -f1*0.01F, 0.0F);
			GL11.glMatrixMode(GL11.GL_MODELVIEW);

			GL11.glPushMatrix();
			float f3 = (size1-size2)*6.61F;
			GL11.glTranslatef(0.0F, -f3, 0.0F);
			GL11.glScalef(size1, size1, size1);
			GL11.glColor4f(color, color, color, color);
			this.model.renderPart(RTMCore.smoothing, "objA");
			GL11.glPopMatrix();

			this.bindTexture(tex_R);
			GL11.glMatrixMode(GL11.GL_TEXTURE);
			GL11.glLoadIdentity();
			GL11.glTranslatef(0.0F, -f1*0.01F, 0.0F);
			GL11.glMatrixMode(GL11.GL_MODELVIEW);

			GL11.glPushMatrix();
			f3 = (float) (par1.getLinear(f0)*0.1D);
			GL11.glScalef(f3, f3*0.75F, f3);
			GL11.glTranslatef(0.0F, 0.5F, 0.0F);
			for (int i = 0; i<partNames.length; ++i) {
				GL11.glColor4f(color, color, color, color2);
				this.ring.renderPart(RTMCore.smoothing, partNames[i]);
				color2 *= 0.75F;
			}
			GL11.glPopMatrix();

			GL11.glMatrixMode(GL11.GL_TEXTURE);
			GL11.glLoadIdentity();
			GL11.glMatrixMode(GL11.GL_MODELVIEW);

			GL11.glDisable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_ALPHA_TEST);
			GL11.glDepthMask(true);
		}

		GL11.glPopMatrix();
	}

	@Override
	public void renderTileEntityAt(TileEntity par1, double par2, double par3, double par4, float par5) {
		this.render((TileEntityEffect) par1, par2, par3, par4, par5);
	}
}