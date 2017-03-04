package net.teamfruit.projectrtm.rtm.modelpack.modelset;

import net.teamfruit.projectrtm.rtm.modelpack.cfg.ContainerConfig;

public class ModelSetContainer extends ModelSetBase<ContainerConfig> {
	public ModelSetContainer() {
		super();
	}

	public ModelSetContainer(ContainerConfig par1) {
		super(par1);
	}

	@Override
	public ContainerConfig getDummyConfig() {
		return ContainerConfig.getDummy();
	}
}