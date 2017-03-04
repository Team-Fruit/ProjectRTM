package net.teamfruit.projectrtm.rtm;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.teamfruit.projectrtm.rtm.entity.train.util.FormationManager;
import net.teamfruit.projectrtm.rtm.entity.vehicle.EntityVehicleBase;
import net.teamfruit.projectrtm.rtm.entity.vehicle.IUpdateVehicle;
import net.teamfruit.projectrtm.rtm.modelpack.ModelPackLoadThread;
import net.teamfruit.projectrtm.rtm.modelpack.ModelPackManager;
import net.teamfruit.projectrtm.rtm.modelpack.cfg.ConnectorConfig;
import net.teamfruit.projectrtm.rtm.modelpack.cfg.ContainerConfig;
import net.teamfruit.projectrtm.rtm.modelpack.cfg.FirearmConfig;
import net.teamfruit.projectrtm.rtm.modelpack.cfg.MachineConfig;
import net.teamfruit.projectrtm.rtm.modelpack.cfg.NPCConfig;
import net.teamfruit.projectrtm.rtm.modelpack.cfg.RailConfig;
import net.teamfruit.projectrtm.rtm.modelpack.cfg.SignalConfig;
import net.teamfruit.projectrtm.rtm.modelpack.cfg.TrainConfig;
import net.teamfruit.projectrtm.rtm.modelpack.cfg.VehicleConfig;
import net.teamfruit.projectrtm.rtm.modelpack.cfg.WireConfig;
import net.teamfruit.projectrtm.rtm.modelpack.modelset.ModelSetConnector;
import net.teamfruit.projectrtm.rtm.modelpack.modelset.ModelSetContainer;
import net.teamfruit.projectrtm.rtm.modelpack.modelset.ModelSetFirearm;
import net.teamfruit.projectrtm.rtm.modelpack.modelset.ModelSetMachine;
import net.teamfruit.projectrtm.rtm.modelpack.modelset.ModelSetNPC;
import net.teamfruit.projectrtm.rtm.modelpack.modelset.ModelSetRail;
import net.teamfruit.projectrtm.rtm.modelpack.modelset.ModelSetSignal;
import net.teamfruit.projectrtm.rtm.modelpack.modelset.ModelSetTrain;
import net.teamfruit.projectrtm.rtm.modelpack.modelset.ModelSetVehicle;
import net.teamfruit.projectrtm.rtm.modelpack.modelset.ModelSetWire;
import net.teamfruit.projectrtm.rtm.network.PacketPlaySound;
import cpw.mods.fml.relauncher.Side;

public class CommonProxy {
	private final FormationManager fm = new FormationManager(false);

	public void preInit() {
		ModelPackManager.INSTANCE.registerType("ModelFirearm", FirearmConfig.class, ModelSetFirearm.class);
		ModelPackManager.INSTANCE.registerType("ModelRail", RailConfig.class, ModelSetRail.class);
		ModelPackManager.INSTANCE.registerType("ModelSignal", SignalConfig.class, ModelSetSignal.class);
		ModelPackManager.INSTANCE.registerType("ModelTrain", TrainConfig.class, ModelSetTrain.class);
		ModelPackManager.INSTANCE.registerType("ModelContainer", ContainerConfig.class, ModelSetContainer.class);
		ModelPackManager.INSTANCE.registerType("ModelVehicle", VehicleConfig.class, ModelSetVehicle.class);
		ModelPackManager.INSTANCE.registerType("ModelNPC", NPCConfig.class, ModelSetNPC.class);
		ModelPackManager.INSTANCE.registerType("ModelMachine", MachineConfig.class, ModelSetMachine.class);
		ModelPackManager.INSTANCE.registerType("ModelWire", WireConfig.class, ModelSetWire.class);
		ModelPackManager.INSTANCE.registerType("ModelConnector", ConnectorConfig.class, ModelSetConnector.class);

		ModelPackLoadThread thread = new ModelPackLoadThread(Side.SERVER);
		thread.start();
	}

	public void init() {
	}

	public IUpdateVehicle getSoundUpdater(EntityVehicleBase par1) {
		return null;
	}

	/**@return 0:接続なし, 1:接続完了*/
	public byte getConnectionState() {
		return 1;
	}

	/**@param par1 : 0:接続なし, 1:接続完了*/
	public void setConnectionState(byte par1) {
	}

	public void spawnModParticle(World world, double x, double y, double z, double mX, double mY, double mZ) {
	}

	public void renderMissingModel() {
	}

	public float getFov(EntityPlayer player, float fov) {
		return 1.0F;
	}

	/**
	 * 音を鳴らす、リピートなし
	 * @param entity
	 * @param sound null可
	 */
	public void playSound(Entity entity, ResourceLocation sound, float vol, float pitch) {
		if (sound!=null) {
			RTMCore.NETWORK_WRAPPER.sendToAll(new PacketPlaySound(entity, sound, vol, pitch));
		}
	}

	public void playSound(TileEntity entity, ResourceLocation sound, float vol, float pitch) {
		;
	}

	/**Sever/Clientでインスタンス分けて取得*/
	public FormationManager getFormationManager() {
		return this.fm;
	}
}