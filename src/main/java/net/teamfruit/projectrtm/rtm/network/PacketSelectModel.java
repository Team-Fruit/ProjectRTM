package net.teamfruit.projectrtm.rtm.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.teamfruit.projectrtm.rtm.modelpack.IModelSelector;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketSelectModel implements IMessage, IMessageHandler<PacketSelectModel, IMessage> {
	private int[] pos;
	private String modelName;

	public PacketSelectModel() {
	}

	public PacketSelectModel(IModelSelector selsector, String name) {
		this.pos = selsector.getPos();
		this.modelName = name;
	}

	@Override
	public void toBytes(ByteBuf buffer) {
		buffer.writeInt(this.pos[0]);
		buffer.writeInt(this.pos[1]);
		buffer.writeInt(this.pos[2]);
		ByteBufUtils.writeUTF8String(buffer, this.modelName);
	}

	@Override
	public void fromBytes(ByteBuf buffer) {
		this.pos = new int[3];
		this.pos[0] = buffer.readInt();
		this.pos[1] = buffer.readInt();
		this.pos[2] = buffer.readInt();
		this.modelName = ByteBufUtils.readUTF8String(buffer);
	}

	@Override
	public IMessage onMessage(PacketSelectModel message, MessageContext ctx) {
		EntityPlayer player = ctx.getServerHandler().playerEntity;
		World world = player.worldObj;

		if (message.pos[1]>=0) {
			TileEntity tile = world.getTileEntity(message.pos[0], message.pos[1], message.pos[2]);
			if (tile instanceof IModelSelector) {
				((IModelSelector) tile).setModelName(message.modelName);
			}
		} else {
			Entity entity = world.getEntityByID(message.pos[0]);
			if (entity instanceof IModelSelector) {
				((IModelSelector) entity).setModelName(message.modelName);
			}
		}

		return null;
	}
}