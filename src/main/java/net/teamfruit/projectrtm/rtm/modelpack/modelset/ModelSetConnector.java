package net.teamfruit.projectrtm.rtm.modelpack.modelset;

import net.teamfruit.projectrtm.rtm.modelpack.cfg.ConnectorConfig;

public class ModelSetConnector extends ModelSetBase<ConnectorConfig> {
	public ModelSetConnector() {
		super();
	}

	public ModelSetConnector(ConnectorConfig cfg) {
		super(cfg);
	}

	@Override
	public ConnectorConfig getDummyConfig() {
		return ConnectorConfig.getDummy();
	}
}