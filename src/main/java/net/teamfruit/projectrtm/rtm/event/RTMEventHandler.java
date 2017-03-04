package net.teamfruit.projectrtm.rtm.event;

import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.teamfruit.projectrtm.ngtlib.util.NGTUtil;
import net.teamfruit.projectrtm.rtm.RTMCore;
import net.teamfruit.projectrtm.rtm.entity.train.util.FormationManager;
import net.teamfruit.projectrtm.rtm.modelpack.ModelPackManager;
import net.teamfruit.projectrtm.rtm.network.ConnectionManager;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ServerConnectionFromClientEvent;

public final class RTMEventHandler {
	//PlayerEvent.StartTracking
	//CanUpdate

	@SubscribeEvent
	public void connectedToServer(ClientConnectedToServerEvent event) {
		ConnectionManager.INSTANCE.onConnectedToServer(event.isLocal);
	}

	@SubscribeEvent
	public void connectedFromClient(ServerConnectionFromClientEvent event)//このタイミングではS->Cのパケット届かない
	{
		ConnectionManager.INSTANCE.onConnectedFromClient(event.isLocal);
	}

	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerLoggedInEvent event)//ServerConfigurationManager.initializeConnectionToPlayer()
	{
		if (NGTUtil.isSMP()||NGTUtil.openedLANWorld()) {
			ModelPackManager.INSTANCE.sendModelSetsToClient((EntityPlayerMP) event.player);
		}
	}

	@SubscribeEvent
	public void onEntitySpawn(EntityJoinWorldEvent event) {
		if (event.entity instanceof EntityBat) {
			if (RTMCore.deleteBat) {
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public void onWorldTick(WorldTickEvent event) {
		if (event.phase==Phase.END) {
			RTMCore.proxy.getFormationManager().updateFormations(event.world);//Serverしか呼ばれない
		}
	}

	@SubscribeEvent
	public void onLoadWorld(WorldEvent.Load event) {
		//StationManager.INSTANCE.loadData(event.world);
		FormationManager.getInstance().loadData(event.world);
	}
}