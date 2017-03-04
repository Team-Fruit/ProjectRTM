package net.teamfruit.projectrtm.rtm.network;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.teamfruit.projectrtm.ngtlib.util.NGTUtil;
import net.teamfruit.projectrtm.rtm.RTMCore;
import net.teamfruit.projectrtm.rtm.block.tileentity.TileEntityMovingMachine;
import net.teamfruit.projectrtm.rtm.entity.npc.macro.MacroRecorder;
import net.teamfruit.projectrtm.rtm.entity.train.parts.EntityArtillery;
import net.teamfruit.projectrtm.rtm.rail.TileEntityMarker;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketNoticeHandlerClient implements IMessageHandler<PacketNotice, IMessage> {
	@Override
	public IMessage onMessage(PacketNotice message, MessageContext ctx) {
		if ((message.type&1)==PacketNotice.Side_CLIENT) {
			World world = NGTUtil.getClientWorld();

			if (message.notice.equals("setConnected")) {
				RTMCore.proxy.setConnectionState((byte) 1);
			} else if (message.notice.startsWith("changeDisplayList")) {
				;
			} else if (message.notice.startsWith("fire")) {
				Entity entity = world.getEntityByID(message.x);
				if (entity instanceof EntityArtillery) {
					((EntityArtillery) entity).recoilCount = EntityArtillery.MaxRecoilCount;
				}
			} else if (message.notice.startsWith("marker")) {
				String[] sa0 = message.notice.split(",");
				int v = Integer.parseInt(sa0[1]);
				TileEntity tile = world.getTileEntity(message.x, message.y, message.z);
				if (tile instanceof TileEntityMarker) {
					((TileEntityMarker) tile).setDisplayMode((byte) v);
				}
			} else if (message.notice.startsWith("MM")) {
				String[] sa0 = message.notice.split(",");
				int v = Integer.parseInt(sa0[1]);
				TileEntity tile = world.getTileEntity(message.x, message.y, message.z);
				if (tile instanceof TileEntityMovingMachine) {
					((TileEntityMovingMachine) tile).setMovement((byte) v);
				}
			} else if (message.notice.startsWith("TRec")) {
				if (MacroRecorder.INSTANCE.isRecording()) {
					MacroRecorder.INSTANCE.stop(world);
				} else {
					MacroRecorder.INSTANCE.start(world);
				}
			}
		}
		return null;
	}
}