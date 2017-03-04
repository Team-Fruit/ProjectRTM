package net.teamfruit.projectrtm.rtm.modelpack.modelset;

import net.minecraft.util.ResourceLocation;
import net.teamfruit.projectrtm.rtm.modelpack.ModelPackManager;
import net.teamfruit.projectrtm.rtm.modelpack.cfg.ModelConfig;

public abstract class ModelSetBase<T extends ModelConfig> {
	protected final T cfg;
	private boolean isDummyModel;

	/**ダミー用*/
	public ModelSetBase() {
		this.cfg = this.getDummyConfig();
		this.isDummyModel = true;
	}

	public ModelSetBase(T par1) {
		this.cfg = par1;
		this.isDummyModel = false;
	}

	public T getConfig() {
		return this.cfg;
	}

	public abstract T getDummyConfig();

	public boolean isDummy() {
		return this.isDummyModel;
	}

	protected ResourceLocation getSoundResource(String par1) {
		if (par1!=null&&par1.length()>0) {
			if (par1.contains(":")) {
				String[] sa = par1.split(":");
				return ModelPackManager.INSTANCE.getResource(sa[0], sa[1]);
			} else {
				return ModelPackManager.INSTANCE.getResource("rtm", par1);
			}
		}
		return null;
	}
}