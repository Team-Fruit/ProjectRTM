package net.teamfruit.projectrtm.ngtlib;

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
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.teamfruit.projectrtm.ngtlib.event.NGTEventHandler;
import net.teamfruit.projectrtm.ngtlib.item.ItemProtectionKey;
import net.teamfruit.projectrtm.ngtlib.network.PacketNBT;
import net.teamfruit.projectrtm.ngtlib.network.PacketNBTHandlerClient;
import net.teamfruit.projectrtm.ngtlib.network.PacketNBTHandlerServer;
import net.teamfruit.projectrtm.ngtlib.network.PacketNotice;
import net.teamfruit.projectrtm.ngtlib.network.PacketNoticeHandlerClient;
import net.teamfruit.projectrtm.ngtlib.network.PacketNoticeHandlerServer;
import net.teamfruit.projectrtm.ngtlib.network.PacketProtection;

@Mod(modid = NGTCore.MODID, name = "NGTLib", version = NGTCore.VERSION)
public class NGTCore {
	public static final String MODID = "NGTLib";
	public static final String VERSION = "1.7.10.27";

	@Instance(MODID)
	public static NGTCore instance;

	@Metadata(MODID)
	public static ModMetadata metadata;

	@SidedProxy(clientSide = "net.teamfruit.projectrtm.ngtlib.ClientProxy", serverSide = "net.teamfruit.projectrtm.ngtlib.CommonProxy")
	public static CommonProxy proxy;
	public static final SimpleNetworkWrapper NETWORK_WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

	/**
	 * 開発環境のパスに含まれる文字列。<br>
	 * <br>
	 * Modの開発環境のパスに含まれる文字列を設定してください。（Configファイルからも設定できます。
	 * ここに直接代入する場合は、下のConfigから設定する処理を消しておいてください。）<br>
	 * 例. "F:\Minecraft\Develop\1.7.2\forge\"で開発している場合は、"Develop"という文字列を指定すればよいです。<br>
	 * <br>
	 * Forgeの開発環境では通常とassetsの場所が異なるため、デフォルトのassetsのパスからはRTMのモデルなどをロードすることができません。<br>
	 * なので、当Modでは、このStringがデフォルトのassetsのパスに含まれている場合は、現在の環境をForgeの開発環境であると判断し、
	 * 開発環境専用のパスを設定し直すことで、モデルなどをロードできるようにしています。
	 */
	public static String developmentPathContainedText;
	public static String shaderModName;
	public static boolean versionCheck;

	public static ItemProtectionKey protection_key;

	@EventHandler
	public void preInit(final FMLPreInitializationEvent event) {
		final Configuration cfg = new Configuration(event.getSuggestedConfigurationFile());
		try {
			cfg.load();
			final Property developmentPro1 = cfg.get("Development", "development path contained text", "Develop");
			developmentPro1.comment = "This option is intended for developer. Specify the text that is included in the path of development environment.";
			final Property modPro1 = cfg.get("Mod", "version check", true);
			modPro1.comment = "";
			final Property modPro2 = cfg.get("Mod", "shadersmod name", "ShadersModCore");
			modPro2.comment = "File name of ShadersMod";

			developmentPathContainedText = developmentPro1.getString();
			versionCheck = modPro1.getBoolean();
			shaderModName = modPro2.getString();
		} catch (final Exception e) {
			FMLLog.log(Level.ERROR, e, "Error Message");
		} finally {
			cfg.save();
		}

		protection_key = (ItemProtectionKey) new ItemProtectionKey().setUnlocalizedName("protection_key").setTextureName("ngtlib:protection_key");

		GameRegistry.registerItem(protection_key, "protection_key");

		proxy.preInit();
		NETWORK_WRAPPER.registerMessage(PacketNoticeHandlerClient.class, PacketNotice.class, 0, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(PacketNoticeHandlerServer.class, PacketNotice.class, 1, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(PacketNBTHandlerClient.class, PacketNBT.class, 2, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(PacketNBTHandlerServer.class, PacketNBT.class, 3, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(PacketProtection.class, PacketProtection.class, 4, Side.CLIENT);
	}

	@EventHandler
	public void init(final FMLInitializationEvent event) {
		proxy.init();
		final NGTEventHandler handler = new NGTEventHandler();
		FMLCommonHandler.instance().bus().register(handler);
		MinecraftForge.EVENT_BUS.register(handler);
	}

	@EventHandler
	public void postInit(final FMLPostInitializationEvent event) {
		proxy.postInit();
	}

	@EventHandler
	public void serverStarting(final FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandNGT());
		event.registerServerCommand(new CommandProtection());
	}

	@EventHandler
	public void serverStarted(final FMLServerStartedEvent event) {
		//NGTStructureBuilder.init();
	}
}