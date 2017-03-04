package net.teamfruit.projectrtm.rtm.electric;

import net.minecraft.nbt.NBTTagCompound;
import net.teamfruit.projectrtm.ngtlib.math.NGTMath;
import net.teamfruit.projectrtm.ngtlib.math.NGTVec;
import net.teamfruit.projectrtm.rtm.modelpack.IModelSelectorWithType;
import net.teamfruit.projectrtm.rtm.modelpack.ModelPackManager;
import net.teamfruit.projectrtm.rtm.modelpack.cfg.ConnectorConfig;
import net.teamfruit.projectrtm.rtm.modelpack.modelset.ModelSetConnector;

public abstract class TileEntityConnectorBase extends TileEntityElectricalWiring implements IModelSelectorWithType {
	private String modelName = "";
	private ModelSetConnector myModelSet;
	public NGTVec wirePos;

	@Deprecated
	private boolean needToUpdateModelName;

	public TileEntityConnectorBase() {
	}

	public TileEntityConnectorBase(int meta) {
		this.modelName = this.getDefaultName();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		String name = nbt.getString("ModelName");
		if (name.isEmpty())//v34互換
		{
			this.needToUpdateModelName = true;
			return;
		}
		this.setModelName(name);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setString("ModelName", this.modelName);
	}

	@Override
	public void updateEntity() {
		if (this.needToUpdateModelName)//v34互換
		{
			this.setModelName(this.getDefaultName());
			this.needToUpdateModelName = false;
		}

		super.updateEntity();
	}

	public ModelSetConnector getModelSet() {
		if (this.myModelSet==null||this.myModelSet.isDummy()) {
			this.myModelSet = ModelPackManager.INSTANCE.getModelSet("ModelConnector", this.modelName);

			ConnectorConfig cfg = this.myModelSet.getConfig();
			this.wirePos = new NGTVec(cfg.wirePos[0], cfg.wirePos[1], cfg.wirePos[2]);
			int meta = this.getBlockMetadata();
			switch (meta) {
				case 0:
					this.wirePos.rotateAroundZ(NGTMath.toRadians(180.0F));
					break;
				case 1:
					break;
				case 2://Z
					this.wirePos.rotateAroundX(NGTMath.toRadians(-90.0F));
					this.wirePos.rotateAroundY(NGTMath.toRadians(180.0F));
					break;
				case 3://Z
					this.wirePos.rotateAroundX(NGTMath.toRadians(-90.0F));
					break;
				case 4://X
					this.wirePos.rotateAroundX(NGTMath.toRadians(-90.0F));
					this.wirePos.rotateAroundY(NGTMath.toRadians(-90.0F));
					break;
				case 5://X
					this.wirePos.rotateAroundX(NGTMath.toRadians(-90.0F));
					this.wirePos.rotateAroundY(NGTMath.toRadians(90.0F));
					break;
			}
		}
		return this.myModelSet;
	}

	@Override
	public String getModelType() {
		return "ModelConnector";
	}

	@Override
	public String getModelName() {
		return this.modelName;
	}

	@Override
	public void setModelName(String par1) {
		this.modelName = par1;
		this.myModelSet = null;
		this.getDescriptionPacket();
	}

	@Override
	public int[] getPos() {
		return new int[] { this.xCoord, this.yCoord, this.zCoord };
	}

	@Override
	public boolean closeGui(String par1) {
		this.setModelName(par1);
		return true;
	}

	protected abstract String getDefaultName();
}