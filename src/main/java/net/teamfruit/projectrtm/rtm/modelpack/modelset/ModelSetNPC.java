package net.teamfruit.projectrtm.rtm.modelpack.modelset;

import net.minecraft.util.ResourceLocation;
import net.teamfruit.projectrtm.rtm.modelpack.ModelPackManager;
import net.teamfruit.projectrtm.rtm.modelpack.cfg.NPCConfig;

public class ModelSetNPC extends ModelSetBase<NPCConfig> {
	public final ResourceLocation texture;
	public final ResourceLocation lightTexture;

	public ModelSetNPC() {
		super();
		this.texture = ModelPackManager.INSTANCE.getResource("textures/container/19g_JRF_0.png");
		this.lightTexture = null;
	}

	public ModelSetNPC(NPCConfig cfg) {
		super(cfg);
		this.texture = ModelPackManager.INSTANCE.getResource(cfg.texture);
		this.lightTexture = cfg.lightTexture!=null ? ModelPackManager.INSTANCE.getResource(cfg.lightTexture) : null;
	}

	@Override
	public NPCConfig getDummyConfig() {
		return NPCConfig.getDummy();
	}
}