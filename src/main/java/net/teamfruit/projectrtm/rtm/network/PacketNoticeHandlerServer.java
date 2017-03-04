package net.teamfruit.projectrtm.rtm.network;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.teamfruit.projectrtm.rtm.RTMCore;
import net.teamfruit.projectrtm.rtm.block.tileentity.TileEntityTrainWorkBench;
import net.teamfruit.projectrtm.rtm.entity.npc.EntityMotorman;
import net.teamfruit.projectrtm.rtm.entity.npc.macro.TrainCommand;
import net.teamfruit.projectrtm.rtm.gui.ContainerRTMWorkBench;
import net.teamfruit.projectrtm.rtm.gui.ContainerTrainControlPanel;
import net.teamfruit.projectrtm.rtm.modelpack.ModelPackUploadThread;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketNoticeHandlerServer implements IMessageHandler<PacketNotice, IMessage> {
	@Override
	public IMessage onMessage(PacketNotice message, MessageContext ctx) {
		EntityPlayer player = ctx.getServerHandler().playerEntity;
		World world = player.worldObj;

		if ((message.type&1)==PacketNotice.Side_SERVER) {
			if (message.notice.equals("isConnected")) {
				RTMCore.NETWORK_WRAPPER.sendToAll(new PacketNotice(PacketNotice.Side_CLIENT, "setConnected"));
			} else if (message.notice.startsWith("getModelPack")) {
				RTMCore.NETWORK_WRAPPER.sendToAll(new PacketNotice(PacketNotice.Side_CLIENT, "setConnected"));
				ModelPackUploadThread.startThread();
			} else if (message.notice.startsWith("StartCrafting")) {
				TileEntity tile = world.getTileEntity(message.x, message.y, message.z);
				if (tile instanceof TileEntityTrainWorkBench) {
					((TileEntityTrainWorkBench) tile).startCrafting(player, false);
				}
			} else if (message.notice.startsWith("setTrainTab")) {
				String[] sa = message.notice.split(",");
				int tabIndex = Integer.valueOf(sa[1]);
				Entity entity = world.getEntityByID(message.x);
				if (entity instanceof EntityPlayer) {
					Container container = ((EntityPlayer) entity).openContainer;
					if (container instanceof ContainerTrainControlPanel) {
						((ContainerTrainControlPanel) container).setCurrentTab(tabIndex);
					}
				}
			} else if (message.notice.startsWith("workbench")) {
				String[] sa = message.notice.split(",");
				String name = sa[1];
				float h = Float.valueOf(sa[2]);

				if (player.openContainer instanceof ContainerRTMWorkBench) {
					((ContainerRTMWorkBench) player.openContainer).setRailProp(name, h);
				}
			} else if (message.notice.startsWith("TMacro")) {
				Entity entity = world.getEntityByID(message.x);
				if (entity instanceof EntityMotorman) {
					String s2 = message.notice.replace("TMacro"+TrainCommand.SEPARATOR, "");
					String[] sa = s2.split(TrainCommand.SEPARATOR);
					((EntityMotorman) entity).setMacro(sa);
				}
			}
		}
		return null;
	}
}