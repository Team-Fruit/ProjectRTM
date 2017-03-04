package net.teamfruit.projectrtm.rtm;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.teamfruit.projectrtm.ngtlib.io.NGTFileLoadException;
import net.teamfruit.projectrtm.ngtlib.io.NGTFileLoader;
import net.teamfruit.projectrtm.ngtlib.io.NGTJson;
import net.teamfruit.projectrtm.ngtlib.io.NGTLog;
import net.teamfruit.projectrtm.ngtlib.util.NGTUtil;
import net.teamfruit.projectrtm.ngtlib.util.NGTUtilClient;
import net.teamfruit.projectrtm.ngtlib.util.PackInfo;
import net.teamfruit.projectrtm.ngtlib.util.VersionChecker;
import net.teamfruit.projectrtm.rtm.block.RenderBlockLinePole;
import net.teamfruit.projectrtm.rtm.block.RenderBlockLiquid;
import net.teamfruit.projectrtm.rtm.block.RenderBlockScaffold;
import net.teamfruit.projectrtm.rtm.block.RenderBlockScaffoldStairs;
import net.teamfruit.projectrtm.rtm.block.RenderFluorescent;
import net.teamfruit.projectrtm.rtm.block.RenderVariableBlock;
import net.teamfruit.projectrtm.rtm.block.tileentity.RenderConverter;
import net.teamfruit.projectrtm.rtm.block.tileentity.RenderEffect;
import net.teamfruit.projectrtm.rtm.block.tileentity.RenderFlag;
import net.teamfruit.projectrtm.rtm.block.tileentity.RenderMachine;
import net.teamfruit.projectrtm.rtm.block.tileentity.RenderMirror;
import net.teamfruit.projectrtm.rtm.block.tileentity.RenderMovingMachine;
import net.teamfruit.projectrtm.rtm.block.tileentity.RenderPaint;
import net.teamfruit.projectrtm.rtm.block.tileentity.RenderPipe;
import net.teamfruit.projectrtm.rtm.block.tileentity.RenderRailroadSign;
import net.teamfruit.projectrtm.rtm.block.tileentity.RenderSignBoard;
import net.teamfruit.projectrtm.rtm.block.tileentity.RenderStation;
import net.teamfruit.projectrtm.rtm.block.tileentity.TileEntityConverterCore;
import net.teamfruit.projectrtm.rtm.block.tileentity.TileEntityCrossingGate;
import net.teamfruit.projectrtm.rtm.block.tileentity.TileEntityEffect;
import net.teamfruit.projectrtm.rtm.block.tileentity.TileEntityFlag;
import net.teamfruit.projectrtm.rtm.block.tileentity.TileEntityFluorescent;
import net.teamfruit.projectrtm.rtm.block.tileentity.TileEntityLight;
import net.teamfruit.projectrtm.rtm.block.tileentity.TileEntityMirror;
import net.teamfruit.projectrtm.rtm.block.tileentity.TileEntityMovingMachine;
import net.teamfruit.projectrtm.rtm.block.tileentity.TileEntityPaint;
import net.teamfruit.projectrtm.rtm.block.tileentity.TileEntityPipe;
import net.teamfruit.projectrtm.rtm.block.tileentity.TileEntityPoint;
import net.teamfruit.projectrtm.rtm.block.tileentity.TileEntityRailroadSign;
import net.teamfruit.projectrtm.rtm.block.tileentity.TileEntityScaffoldStairs;
import net.teamfruit.projectrtm.rtm.block.tileentity.TileEntitySignBoard;
import net.teamfruit.projectrtm.rtm.block.tileentity.TileEntityStation;
import net.teamfruit.projectrtm.rtm.block.tileentity.TileEntityTurnstile;
import net.teamfruit.projectrtm.rtm.electric.RenderElectricalWiring;
import net.teamfruit.projectrtm.rtm.electric.RenderSignal;
import net.teamfruit.projectrtm.rtm.electric.TileEntityConnector;
import net.teamfruit.projectrtm.rtm.electric.TileEntityInsulator;
import net.teamfruit.projectrtm.rtm.electric.TileEntitySignal;
import net.teamfruit.projectrtm.rtm.electric.TileEntityTicketVendor;
import net.teamfruit.projectrtm.rtm.entity.EntityATC;
import net.teamfruit.projectrtm.rtm.entity.EntityBullet;
import net.teamfruit.projectrtm.rtm.entity.EntityBumpingPost;
import net.teamfruit.projectrtm.rtm.entity.EntityMMBoundingBox;
import net.teamfruit.projectrtm.rtm.entity.EntityMeltedMetalFX;
import net.teamfruit.projectrtm.rtm.entity.EntityTrainDetector;
import net.teamfruit.projectrtm.rtm.entity.RenderBullet;
import net.teamfruit.projectrtm.rtm.entity.RenderEntityInstalledObject;
import net.teamfruit.projectrtm.rtm.entity.RenderMMBB;
import net.teamfruit.projectrtm.rtm.entity.npc.EntityNPC;
import net.teamfruit.projectrtm.rtm.entity.npc.RenderNPC;
import net.teamfruit.projectrtm.rtm.entity.train.EntityBogie;
import net.teamfruit.projectrtm.rtm.entity.train.EntityFreightCar;
import net.teamfruit.projectrtm.rtm.entity.train.EntityTanker;
import net.teamfruit.projectrtm.rtm.entity.train.EntityTrain;
import net.teamfruit.projectrtm.rtm.entity.train.EntityTrainBase;
import net.teamfruit.projectrtm.rtm.entity.train.RenderBogie;
import net.teamfruit.projectrtm.rtm.entity.train.RenderTrain;
import net.teamfruit.projectrtm.rtm.entity.train.parts.EntityArtillery;
import net.teamfruit.projectrtm.rtm.entity.train.parts.EntityContainer;
import net.teamfruit.projectrtm.rtm.entity.train.parts.EntityFloor;
import net.teamfruit.projectrtm.rtm.entity.train.parts.EntityTie;
import net.teamfruit.projectrtm.rtm.entity.train.parts.RenderArtillery;
import net.teamfruit.projectrtm.rtm.entity.train.parts.RenderContainer;
import net.teamfruit.projectrtm.rtm.entity.train.parts.RenderSeat;
import net.teamfruit.projectrtm.rtm.entity.train.parts.RenderTie;
import net.teamfruit.projectrtm.rtm.entity.train.util.FormationManager;
import net.teamfruit.projectrtm.rtm.entity.vehicle.EntityVehicle;
import net.teamfruit.projectrtm.rtm.entity.vehicle.EntityVehicleBase;
import net.teamfruit.projectrtm.rtm.entity.vehicle.IUpdateVehicle;
import net.teamfruit.projectrtm.rtm.entity.vehicle.RenderVehicle;
import net.teamfruit.projectrtm.rtm.event.RTMEventHandlerClient;
import net.teamfruit.projectrtm.rtm.event.RTMKeyHandlerClient;
import net.teamfruit.projectrtm.rtm.event.RTMTickHandlerClient;
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
import net.teamfruit.projectrtm.rtm.modelpack.modelset.ModelSetConnectorClient;
import net.teamfruit.projectrtm.rtm.modelpack.modelset.ModelSetContainerClient;
import net.teamfruit.projectrtm.rtm.modelpack.modelset.ModelSetFirearm;
import net.teamfruit.projectrtm.rtm.modelpack.modelset.ModelSetFirearmClient;
import net.teamfruit.projectrtm.rtm.modelpack.modelset.ModelSetMachineClient;
import net.teamfruit.projectrtm.rtm.modelpack.modelset.ModelSetNPC;
import net.teamfruit.projectrtm.rtm.modelpack.modelset.ModelSetRailClient;
import net.teamfruit.projectrtm.rtm.modelpack.modelset.ModelSetSignalClient;
import net.teamfruit.projectrtm.rtm.modelpack.modelset.ModelSetTrainClient;
import net.teamfruit.projectrtm.rtm.modelpack.modelset.ModelSetVehicleClient;
import net.teamfruit.projectrtm.rtm.modelpack.modelset.ModelSetWireClient;
import net.teamfruit.projectrtm.rtm.rail.RenderBlockLargeRail;
import net.teamfruit.projectrtm.rtm.rail.RenderLargeRail;
import net.teamfruit.projectrtm.rtm.rail.RenderMarkerBlock;
import net.teamfruit.projectrtm.rtm.rail.TileEntityLargeRailCore;
import net.teamfruit.projectrtm.rtm.rail.TileEntityLargeRailNormalCore;
import net.teamfruit.projectrtm.rtm.rail.TileEntityLargeRailSlopeCore;
import net.teamfruit.projectrtm.rtm.rail.TileEntityLargeRailSwitchCore;
import net.teamfruit.projectrtm.rtm.rail.TileEntityMarker;
import net.teamfruit.projectrtm.rtm.sound.MovingSoundEntity;
import net.teamfruit.projectrtm.rtm.sound.MovingSoundTileEntity;
import net.teamfruit.projectrtm.rtm.sound.SoundUpdaterTrain;
import net.teamfruit.projectrtm.rtm.sound.SoundUpdaterVehicle;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
	public static final byte ViewMode_Artillery = 0;
	public static final byte ViewMode_SR = 1;
	public static final byte ViewMode_AMR = 2;
	public static final byte ViewMode_NVD = 3;

	private final ModelBase missing = new ModelMissing();
	private static final ResourceLocation texture = new ResourceLocation("rtm", "textures/missing.png");
	private byte connectionState = 0;

	private final FormationManager fmClient = new FormationManager(true);

	private List<TileEntityLargeRailCore> unloadedRails = new ArrayList<TileEntityLargeRailCore>();

	@Override
	public void preInit() {
		this.versionCheck();

		RenderingRegistry.registerEntityRenderingHandler(EntityTrain.class, RenderTrain.INSTANCE);
		RenderingRegistry.registerEntityRenderingHandler(EntityFreightCar.class, RenderTrain.INSTANCE);
		RenderingRegistry.registerEntityRenderingHandler(EntityTanker.class, RenderTrain.INSTANCE);
		RenderingRegistry.registerEntityRenderingHandler(EntityBogie.class, new RenderBogie());
		RenderingRegistry.registerEntityRenderingHandler(EntityFloor.class, new RenderSeat());
		RenderingRegistry.registerEntityRenderingHandler(EntityATC.class, RenderEntityInstalledObject.INSTANCE);
		RenderingRegistry.registerEntityRenderingHandler(EntityTrainDetector.class, RenderEntityInstalledObject.INSTANCE);
		RenderingRegistry.registerEntityRenderingHandler(EntityBumpingPost.class, RenderEntityInstalledObject.INSTANCE);
		RenderingRegistry.registerEntityRenderingHandler(EntityContainer.class, new RenderContainer());
		RenderingRegistry.registerEntityRenderingHandler(EntityArtillery.class, new RenderArtillery());
		RenderingRegistry.registerEntityRenderingHandler(EntityBullet.class, RenderBullet.INSTANCE);
		RenderingRegistry.registerEntityRenderingHandler(EntityTie.class, new RenderTie());
		RenderingRegistry.registerEntityRenderingHandler(EntityMMBoundingBox.class, new RenderMMBB());
		RenderingRegistry.registerEntityRenderingHandler(EntityVehicle.class, RenderVehicle.INSTANCE);
		RenderingRegistry.registerEntityRenderingHandler(EntityNPC.class, new RenderNPC());

		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFluorescent.class, new RenderFluorescent());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLargeRailNormalCore.class, RenderLargeRail.INSTANCE);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLargeRailSwitchCore.class, RenderLargeRail.INSTANCE);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLargeRailSlopeCore.class, RenderLargeRail.INSTANCE);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityInsulator.class, RenderElectricalWiring.INSTANCE);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityConnector.class, RenderElectricalWiring.INSTANCE);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySignal.class, new RenderSignal());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRailroadSign.class, new RenderRailroadSign());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySignBoard.class, new RenderSignBoard());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEffect.class, new RenderEffect());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMarker.class, new RenderMarkerBlock());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityStation.class, new RenderStation());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMovingMachine.class, new RenderMovingMachine());

		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTurnstile.class, RenderMachine.INSTANCE);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPoint.class, RenderMachine.INSTANCE);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCrossingGate.class, RenderMachine.INSTANCE);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTicketVendor.class, RenderMachine.INSTANCE);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLight.class, RenderMachine.INSTANCE);

		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPipe.class, new RenderPipe());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityConverterCore.class, new RenderConverter());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityScaffoldStairs.class, RenderBlockScaffoldStairs.INSTANCE);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPaint.class, new RenderPaint());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFlag.class, new RenderFlag());

		RenderingRegistry.registerBlockHandler(new RenderVariableBlock());
		RenderingRegistry.registerBlockHandler(new RenderBlockLinePole());
		RenderingRegistry.registerBlockHandler(new RenderBlockLiquid());
		RenderingRegistry.registerBlockHandler(new RenderBlockScaffold());
		RenderingRegistry.registerBlockHandler(RenderBlockScaffoldStairs.INSTANCE);
		RenderingRegistry.registerBlockHandler(new RenderBlockLargeRail());

		MinecraftForge.EVENT_BUS.register(new RTMEventHandlerClient(Minecraft.getMinecraft()));
		MinecraftForge.EVENT_BUS.register(new RTMParticles());

		RTMKeyHandlerClient.init();

		ModelPackManager.INSTANCE.registerType("ModelFirearm", FirearmConfig.class, ModelSetFirearmClient.class);
		ModelPackManager.INSTANCE.registerType("ModelRail", RailConfig.class, ModelSetRailClient.class);
		ModelPackManager.INSTANCE.registerType("ModelSignal", SignalConfig.class, ModelSetSignalClient.class);
		ModelPackManager.INSTANCE.registerType("ModelTrain", TrainConfig.class, ModelSetTrainClient.class);
		ModelPackManager.INSTANCE.registerType("ModelContainer", ContainerConfig.class, ModelSetContainerClient.class);
		ModelPackManager.INSTANCE.registerType("ModelVehicle", VehicleConfig.class, ModelSetVehicleClient.class);
		ModelPackManager.INSTANCE.registerType("ModelNPC", NPCConfig.class, ModelSetNPC.class);
		ModelPackManager.INSTANCE.registerType("ModelMachine", MachineConfig.class, ModelSetMachineClient.class);
		ModelPackManager.INSTANCE.registerType("ModelWire", WireConfig.class, ModelSetWireClient.class);
		ModelPackManager.INSTANCE.registerType("ModelConnector", ConnectorConfig.class, ModelSetConnectorClient.class);

		ModelPackLoadThread thread = new ModelPackLoadThread(Side.CLIENT);
		thread.start();
	}

	private void versionCheck() {
		if (!RTMCore.versionCheck) {
			return;
		}

		List<File> fileList = NGTFileLoader.findFile("pack", ".json", "", null);
		for (File file : fileList) {
			String json = NGTJson.readFromJson(file);
			try {
				PackInfo info = (PackInfo) NGTJson.getObjectFromJson(json, PackInfo.class);
				if (info!=null) {
					VersionChecker.addToCheckList(info);
				}
			} catch (NGTFileLoadException e) {
				e.printStackTrace();
			}
		}

		VersionChecker.addToCheckList(new PackInfo(RTMCore.metadata.name, RTMCore.metadata.url, RTMCore.metadata.updateUrl, RTMCore.metadata.version));
	}

	@Override
	public void init() {
		//preInitではMC.renderEngineが初期化されてない
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMirror.class, RenderMirror.INSTANCE);

		FMLCommonHandler.instance().bus().register(RTMKeyHandlerClient.INSTANCE);
		FMLCommonHandler.instance().bus().register(new RTMTickHandlerClient());
	}

	@Override
	public IUpdateVehicle getSoundUpdater(EntityVehicleBase vehicle) {
		if (vehicle instanceof EntityTrainBase) {
			return new SoundUpdaterTrain(NGTUtilClient.getMinecraft().getSoundHandler(), (EntityTrainBase) vehicle);
		} else {
			return new SoundUpdaterVehicle(NGTUtilClient.getMinecraft().getSoundHandler(), vehicle);
		}
	}

	@Override
	public byte getConnectionState() {
		return this.connectionState;
	}

	@Override
	public void setConnectionState(byte par1) {
		this.connectionState = par1;
		NGTLog.debug("[RTM](Client) Set connection state : "+par1);
	}

	@Override
	public void spawnModParticle(World world, double x, double y, double z, double mX, double mY, double mZ) {
		EntityMeltedMetalFX entityFX = new EntityMeltedMetalFX(world, x, y, z, mX, mY, mZ);
		entityFX.setParticleIcon(RTMParticles.getInstance().getIIcon(0));
		FMLClientHandler.instance().getClient().effectRenderer.addEffect(entityFX);
	}

	@Override
	public void renderMissingModel() {
		NGTUtilClient.getMinecraft().renderEngine.bindTexture(texture);
		this.missing.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
	}

	public class ModelMissing extends ModelBase {
		ModelRenderer shape1;

		public ModelMissing() {
			this.textureWidth = 64;
			this.textureHeight = 32;

			this.shape1 = new ModelRenderer(this, 0, 0);
			this.shape1.addBox(-8F, -8F, -8F, 16, 16, 16);
			this.shape1.setRotationPoint(0F, 0F, 0F);
			this.shape1.setTextureSize(64, 32);
			this.shape1.mirror = true;
			this.setRotation(this.shape1, 0F, 0F, 0F);
		}

		@Override
		public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
			super.render((Entity) null, f, f1, f2, f3, f4, f5);
			this.setRotationAngles(f, f1, f2, f3, f4, f5);
			this.shape1.render(f5);
		}

		private void setRotation(ModelRenderer model, float x, float y, float z) {
			model.rotateAngleX = x;
			model.rotateAngleY = y;
			model.rotateAngleZ = z;
		}

		public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5) {
			super.setRotationAngles(f, f1, f2, f3, f4, f5, null);
		}
	}

	@Override
	public float getFov(EntityPlayer player, float fov) {
		switch (getViewMode(player)) {
			case ViewMode_Artillery:
				return 0.1F;
			case ViewMode_SR:
				return 0.25F;
			case ViewMode_AMR:
				return 0.1F;
			default:
				return fov;
		}
	}

	/**0:火砲, 1:狙撃銃, 2:AMR, 3:NVD*/
	public static byte getViewMode(EntityPlayer player) {
		if (NGTUtilClient.getMinecraft().gameSettings.thirdPersonView==0) {
			ItemStack helmet = NGTUtilClient.getMinecraft().thePlayer.inventory.armorItemInSlot(3);
			if (helmet!=null&&helmet.getItem()==RTMItem.nvd) {
				return ViewMode_NVD;
			}

			if (player.isRiding()&&player.ridingEntity instanceof EntityArtillery) {
				ModelSetFirearm set = ((EntityArtillery) player.ridingEntity).getModelSet();
				if (set.getConfig().fpvMode) {
					return ViewMode_Artillery;
				}
			}

			if (player.getCurrentEquippedItem()!=null) {
				if (player.getCurrentEquippedItem().getItem()==RTMItem.sniper_rifle) {
					return ViewMode_SR;
				} else if (player.getCurrentEquippedItem().getItem()==RTMItem.amr) {
					return ViewMode_AMR;
				}
			}
		}
		return -1;
	}

	@Override
	public void playSound(Entity entity, ResourceLocation sound, float vol, float pitch) {
		if (sound!=null) {
			if (NGTUtil.isServer()) {
				super.playSound(entity, sound, vol, pitch);
			} else {
				MovingSoundEntity ms = new MovingSoundEntity(entity, sound, false);
				ms.setVolume(vol);
				ms.setPitch(pitch);
				NGTUtilClient.playSound(ms);
			}
		}
	}

	@Override
	public void playSound(TileEntity entity, ResourceLocation sound, float vol, float pitch) {
		if (sound!=null) {
			if (NGTUtil.isServer()) {
				;
			} else {
				MovingSoundTileEntity ms = new MovingSoundTileEntity(entity, sound, false);
				ms.setVolume(vol);
				ms.setPitch(pitch);
				NGTUtilClient.playSound(ms);
			}
		}
	}

	@Override
	public FormationManager getFormationManager() {
		return NGTUtil.isServer() ? super.getFormationManager() : this.fmClient;
	}
}