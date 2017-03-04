package net.teamfruit.projectrtm.rtm.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.teamfruit.projectrtm.rtm.RTMBlock;
import net.teamfruit.projectrtm.rtm.rail.BlockMarker;
import net.teamfruit.projectrtm.rtm.rail.TileEntityMarker;
import net.teamfruit.projectrtm.rtm.rail.util.RailPosition;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketMarkerRPClient implements IMessage, IMessageHandler<PacketMarkerRPClient, IMessage> {
	private int x, y, z;
	private RailPosition[] railPositions;

	public PacketMarkerRPClient() {
	}

	public PacketMarkerRPClient(int par1, int par2, int par3, TileEntityMarker par4) {
		this.x = par1;
		this.y = par2;
		this.z = par3;
		this.railPositions = par4.getAllRP();
	}

	@Override
	public void toBytes(ByteBuf buffer) {
		buffer.writeInt(this.x);
		buffer.writeInt(this.y);
		buffer.writeInt(this.z);

		buffer.writeByte(this.railPositions.length);
		for (RailPosition rp : this.railPositions) {
			ByteBufUtils.writeTag(buffer, rp.writeToNBT());
		}
	}

	@Override
	public void fromBytes(ByteBuf buffer) {
		this.x = buffer.readInt();
		this.y = buffer.readInt();
		this.z = buffer.readInt();

		byte size = buffer.readByte();
		if (size>0) {
			this.railPositions = new RailPosition[size];
			for (int i = 0; i<size; ++i) {
				NBTTagCompound nbt = ByteBufUtils.readTag(buffer);
				this.railPositions[i] = RailPosition.readFromNBT(nbt);
			}
		}
	}

	@Override
	public IMessage onMessage(PacketMarkerRPClient message, MessageContext ctx) {
		World world = ctx.getServerHandler().playerEntity.worldObj;

		for (RailPosition rp : message.railPositions) {
			TileEntity tile = world.getTileEntity(rp.blockX, rp.blockY, rp.blockZ);
			if (tile instanceof TileEntityMarker) {
				((TileEntityMarker) tile).setMarkerRP(rp);
			}
		}

		((BlockMarker) RTMBlock.marker).onMarkerActivated(world, message.x, message.y, message.z, ctx.getServerHandler().playerEntity, false);
		return null;
	}
}