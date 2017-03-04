package net.teamfruit.projectrtm.rtm.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.teamfruit.projectrtm.ngtlib.util.NGTUtilClient;
import net.teamfruit.projectrtm.rtm.RTMCore;
import net.teamfruit.projectrtm.rtm.block.tileentity.RenderMirror;
import net.teamfruit.projectrtm.rtm.entity.train.parts.EntityArtillery;

import org.lwjgl.opengl.Display;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**FMLのイベント*/
@SideOnly(Side.CLIENT)
public final class RTMTickHandlerClient {
	@SubscribeEvent
	public void onRenderTick(RenderTickEvent event)//Minecraft.runGameLoop()
	{
		if (event.phase==Phase.END) {
			if (NGTUtilClient.getMinecraft().inGameHasFocus&&Display.isActive()) {
				EntityPlayer player = NGTUtilClient.getMinecraft().thePlayer;
				if (player.isRiding()&&player.ridingEntity instanceof EntityArtillery) {
					((EntityArtillery) player.ridingEntity).updateYawAndPitch(player);
				}
			}

			RenderMirror.INSTANCE.onRenderTickEnd();
		}
	}

	@SubscribeEvent
	public void onClientTick(ClientTickEvent event)//runGameLoop()内で複数回呼ばれる
	{
		World world = NGTUtilClient.getMinecraft().theWorld;
		if (!NGTUtilClient.getMinecraft().isGamePaused()&&world!=null) {
			if (event.phase==Phase.START) {
				if (!RenderMirror.INSTANCE.finishRender) {
					RenderMirror.INSTANCE.update();
				}
				RTMCore.proxy.getFormationManager().updateFormations(world);

				RTMKeyHandlerClient.INSTANCE.onTickStart();
			} else if (event.phase==Phase.END) {
				RTMKeyHandlerClient.INSTANCE.onTickEnd();
			}
		}
	}
}