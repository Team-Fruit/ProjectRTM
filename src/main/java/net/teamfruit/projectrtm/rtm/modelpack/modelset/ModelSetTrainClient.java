package net.teamfruit.projectrtm.rtm.modelpack.modelset;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.teamfruit.projectrtm.ngtlib.renderer.model.Material;
import net.teamfruit.projectrtm.ngtlib.renderer.model.TextureSet;
import net.teamfruit.projectrtm.rtm.modelpack.ModelPackManager;
import net.teamfruit.projectrtm.rtm.modelpack.cfg.TrainConfig;
import net.teamfruit.projectrtm.rtm.modelpack.model.ModelBogie;
import net.teamfruit.projectrtm.rtm.render.ModelObject;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelSetTrainClient extends ModelSetVehicleBaseClient {
	public final ModelObject[] bogieModels;
	public final String sound_brakeRelease_s;
	public final String sound_brakeRelease_w;

	public ModelSetTrainClient() {
		super();
		TextureSet tex = new TextureSet(new Material((byte) 0, ModelPackManager.INSTANCE.getResource("textures/train/hoge.png")), 0, false);
		this.bogieModels = new ModelObject[2];
		this.bogieModels[0] = this.bogieModels[1] = new ModelObject(new ModelBogie(), new TextureSet[] { tex }, this);
		this.sound_brakeRelease_s = null;
		this.sound_brakeRelease_w = null;
	}

	public ModelSetTrainClient(TrainConfig par1Cfg) {
		super(par1Cfg);

		this.bogieModels = this.registerBogieModel();
		ResourceLocation s0 = this.getSoundResource(par1Cfg.sound_BrakeRelease);
		this.sound_brakeRelease_s = s0==null ? null : s0.getResourceDomain()+":"+s0.getResourcePath();
		ResourceLocation s1 = this.getSoundResource(par1Cfg.sound_BrakeRelease2);
		this.sound_brakeRelease_w = s1==null ? null : s1.getResourceDomain()+":"+s1.getResourcePath();
	}

	private ModelObject[] registerBogieModel() {
		ModelObject[] modelBogies = new ModelObject[2];
		for (int i = 0; i<2; ++i) {
			modelBogies[i] = new ModelObject(((TrainConfig) this.getConfig()).getBogieModel(i), this, null, "isBogie");
		}
		return modelBogies;
	}

	@Override
	protected void renderPartsInGui(Minecraft par1) {
		TrainConfig cfg = (TrainConfig) this.cfg;
		float[] fa = cfg.getBogiePos()[0];
		int bogieIndex = 0;
		GL11.glTranslatef(fa[0], fa[1], fa[2]);
		this.bogieModels[bogieIndex].render(null, cfg, 0, 0.0F);
	}

	@Override
	public TrainConfig getDummyConfig() {
		return TrainConfig.getDummyConfig();
	}
}