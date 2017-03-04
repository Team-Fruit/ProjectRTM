package net.teamfruit.projectrtm.rtm;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.Metadata;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.teamfruit.projectrtm.rtm.event.RTMEventHandler;
import net.teamfruit.projectrtm.rtm.gui.RTMGuiHandler;
import net.teamfruit.projectrtm.rtm.item.ItemBucketLiquid;
import net.teamfruit.projectrtm.rtm.network.PacketFormation;
import net.teamfruit.projectrtm.rtm.network.PacketLargeRailBase;
import net.teamfruit.projectrtm.rtm.network.PacketLargeRailCore;
import net.teamfruit.projectrtm.rtm.network.PacketMarker;
import net.teamfruit.projectrtm.rtm.network.PacketMarkerRPClient;
import net.teamfruit.projectrtm.rtm.network.PacketModelPack;
import net.teamfruit.projectrtm.rtm.network.PacketModelSet;
import net.teamfruit.projectrtm.rtm.network.PacketMoveMM;
import net.teamfruit.projectrtm.rtm.network.PacketMovingMachine;
import net.teamfruit.projectrtm.rtm.network.PacketNotice;
import net.teamfruit.projectrtm.rtm.network.PacketNoticeHandlerClient;
import net.teamfruit.projectrtm.rtm.network.PacketNoticeHandlerServer;
import net.teamfruit.projectrtm.rtm.network.PacketPlaySound;
import net.teamfruit.projectrtm.rtm.network.PacketRTMKey;
import net.teamfruit.projectrtm.rtm.network.PacketSelectModel;
import net.teamfruit.projectrtm.rtm.network.PacketSetTrainState;
import net.teamfruit.projectrtm.rtm.network.PacketSignal;
import net.teamfruit.projectrtm.rtm.network.PacketSignalConverter;
import net.teamfruit.projectrtm.rtm.network.PacketStationData;
import net.teamfruit.projectrtm.rtm.network.PacketTextureHolder;
import net.teamfruit.projectrtm.rtm.network.PacketVehicleMovement;
import net.teamfruit.projectrtm.rtm.network.PacketWire;
import net.teamfruit.projectrtm.rtm.world.RTMChunkManager;

@Mod(modid = RTMCore.MODID, name = "RealTrainMod", version = RTMCore.VERSION)
public final class RTMCore {
	public static final String MODID = "RTM";
	public static final String VERSION = "1.7.10.35";

	@Instance(MODID)
	public static RTMCore instance;

	@Metadata(MODID)
	public static ModMetadata metadata;

	@SidedProxy(clientSide = "net.teamfruit.projectrtm.rtm.ClientProxy", serverSide = "net.teamfruit.projectrtm.rtm.CommonProxy")
	public static CommonProxy proxy;

	public static final SimpleNetworkWrapper NETWORK_WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

	public static short guiIdSelectEntityModel = getNextGuiID();
	public static short guiIdSelectTileEntityModel = getNextGuiID();
	public static short guiIdSelectItemModel = getNextGuiID();
	public static short guiIdFreightCar = getNextGuiID();
	public static short guiIdItemContainer = getNextGuiID();
	public static short guiIdSelectTexture = getNextGuiID();
	public static short guiIdTrainControlPanel = getNextGuiID();
	public static short guiIdTrainWorkBench = getNextGuiID();
	public static short guiIdSignalConverter = getNextGuiID();
	public static short guiIdTicketVendor = getNextGuiID();
	public static short guiIdStation = getNextGuiID();
	public static short guiIdPaintTool = getNextGuiID();
	public static short guiIdMovingMachine = getNextGuiID();
	public static short guiIdTurnplate = getNextGuiID();
	public static short guiIdNPC = getNextGuiID();
	public static short guiIdMotorman = getNextGuiID();

	public static final byte KEY_Forward = 0;
	public static final byte KEY_Back = 1;
	public static final byte KEY_Horn = 2;
	public static final byte KEY_Chime = 3;
	public static final byte KEY_ControlPanel = 4;
	public static final byte KEY_Fire = 5;
	public static final byte KEY_ATS = 6;
	public static final byte KEY_LEFT = 7;
	public static final byte KEY_RIGHT = 8;
	public static final byte KEY_JUMP = 9;
	public static final byte KEY_SNEAK = 10;

	public static float trainSoundVol;
	public static float gunSoundVol;
	public static short railGeneratingDistance;
	public static short railGeneratingHeight;
	public static short markerDisplayDistance;
	public static byte crossingGateSoundType;
	public static boolean gunBreakBlock;
	public static boolean deleteBat;
	public static boolean useServerModelPack;//C/S両側で有効
	public static boolean versionCheck;
	public static int mirrorTextureSize;
	public static boolean smoothing;
	public static byte mirrorRenderingFrequency;

	public static final int PacketSize = 512;

	@EventHandler
	public void preInit(final FMLPreInitializationEvent event) {
		final Configuration cfg = new Configuration(event.getSuggestedConfigurationFile());
		try {
			cfg.load();
			final Property soundPro1 = cfg.get("Sound", "sound train", 100);
			soundPro1.comment = "Train sound volume. (0 ~ 100)";
			final Property soundPro2 = cfg.get("Sound", "sound crossing gate", 0);
			soundPro2.comment = "Sound type of crossing gate. (0, 1)";
			final Property soundPro3 = cfg.get("Sound", "sound gun", 100);
			soundPro3.comment = "Gun sound volume. (0 ~ 100)";

			final Property railPro1 = cfg.get("Rail", "GeneratingDistance", 64);
			railPro1.comment = "Distance for generating a rail. (default:64, recomended max value:256, It depends on server side)";
			final Property railPro2 = cfg.get("Rail", "GeneratingHeight", 8);
			railPro2.comment = "Height for generating a rail. (default:8, recomended max value:256)";
			final Property railPro3 = cfg.get("Rail", "MarkerDisplayDistance", 100);
			railPro3.comment = "(default length:100)";

			final Property itemPro1 = cfg.get("Item", "Gun Break Block", true);
			//itemPro1.comment = "Delete bat";
			final Property entityPro1 = cfg.get("Entity", "delete bat", false);
			entityPro1.comment = "Delete bat";
			final Property modelPro1 = cfg.get("Model", "use ServerModelPack", false);
			modelPro1.comment = "Download ModelPacks from Server (or Permit download ModelPacks).";
			final Property modelPro2 = cfg.get("Model", "do smoothing", true);
			//modelPro2.comment = "";
			final Property modPro1 = cfg.get("Mod", "version check", true);
			modPro1.comment = "";
			final Property blockPro1 = cfg.get("Block", "mirror texture size", 512);
			blockPro1.comment = "FrameBuffer size for mirror. (Recomended size : 256~2048)";
			final Property blockPro2 = cfg.get("Block", "mirror render frequency", 1);
			blockPro2.comment = "Frequency of rendering mirror. (1 : Full tick)";

			trainSoundVol = soundPro1.getInt()/100.0F;
			crossingGateSoundType = (byte) soundPro2.getInt();
			gunSoundVol = soundPro3.getInt()/100.0F;
			railGeneratingDistance = (short) railPro1.getInt();
			railGeneratingHeight = (short) railPro2.getInt();
			markerDisplayDistance = (short) railPro3.getInt();
			gunBreakBlock = itemPro1.getBoolean();
			deleteBat = entityPro1.getBoolean();
			useServerModelPack = modelPro1.getBoolean();
			smoothing = modelPro2.getBoolean();
			versionCheck = modPro1.getBoolean();
			mirrorTextureSize = blockPro1.getInt();
			mirrorRenderingFrequency = (byte) blockPro2.getInt();
		} catch (final Exception e) {
			FMLLog.log(Level.ERROR, e, "Error Message");
		} finally {
			cfg.save();
		}

		RTMBlock.init();
		RTMItem.init();
		RTMEntity.init(this);
		RTMRecipe.init();
		RTMAchievement.init();

		//NETWORK_WRAPPER.registerMessage(PacketAddTrain.class,			PacketAddTrain.class,		0, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(PacketLargeRailBase.class, PacketLargeRailBase.class, 1, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(PacketModelSet.class, PacketModelSet.class, 2, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(PacketPlaySound.class, PacketPlaySound.class, 3, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(PacketLargeRailCore.class, PacketLargeRailCore.class, 4, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(PacketNoticeHandlerClient.class, PacketNotice.class, 5, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(PacketNoticeHandlerServer.class, PacketNotice.class, 6, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(PacketRTMKey.class, PacketRTMKey.class, 7, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(PacketSelectModel.class, PacketSelectModel.class, 8, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(PacketSignal.class, PacketSignal.class, 9, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(PacketWire.class, PacketWire.class, 10, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(PacketTextureHolder.class, PacketTextureHolder.class, 11, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(PacketSetTrainState.class, PacketSetTrainState.class, 12, Side.SERVER);
		//NETWORK_WRAPPER.registerMessage(PacketRailroadSign.class,		PacketRailroadSign.class,	13, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(PacketModelPack.class, PacketModelPack.class, 14, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(PacketVehicleMovement.class, PacketVehicleMovement.class, 15, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(PacketMarker.class, PacketMarker.class, 16, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(PacketMarkerRPClient.class, PacketMarkerRPClient.class, 17, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(PacketFormation.class, PacketFormation.class, 18, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(PacketSignalConverter.class, PacketSignalConverter.class, 19, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(PacketStationData.class, PacketStationData.class, 20, Side.SERVER);
		//NETWORK_WRAPPER.registerMessage(PacketTextureHolder.class,		PacketTextureHolder.class,	21, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(PacketMovingMachine.class, PacketMovingMachine.class, 22, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(PacketMoveMM.class, PacketMoveMM.class, 23, Side.CLIENT);

		NetworkRegistry.INSTANCE.registerGuiHandler(this, new RTMGuiHandler());

		proxy.preInit();

		ForgeChunkManager.setForcedChunkLoadingCallback(this, RTMChunkManager.INSTANCE);
		MinecraftForge.EVENT_BUS.register(RTMChunkManager.INSTANCE);
	}

	@EventHandler
	public void init(final FMLInitializationEvent event) {
		//FMLCommonHandler.instance().bus().register(new RTMTickHandler());
		final RTMEventHandler handler = new RTMEventHandler();
		FMLCommonHandler.instance().bus().register(handler);
		MinecraftForge.EVENT_BUS.register(handler);
		MinecraftForge.EVENT_BUS.register(new ItemBucketLiquid());

		proxy.init();
	}

	@EventHandler
	public void handleServerStarting(final FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandRTM());
		event.registerServerCommand(new CommandTRec());
	}

	private static short guiId;

	private static short getNextGuiID() {
		return guiId++;
	}
}