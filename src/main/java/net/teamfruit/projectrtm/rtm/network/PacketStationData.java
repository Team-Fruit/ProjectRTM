package net.teamfruit.projectrtm.rtm.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.teamfruit.projectrtm.ngtlib.network.PacketTileEntity;
import net.teamfruit.projectrtm.rtm.block.tileentity.TileEntityStation;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketStationData extends PacketTileEntity implements IMessageHandler<PacketStationData, IMessage> {
	private NBTTagCompound nbt;

	public PacketStationData() {
	}

	public PacketStationData(TileEntityStation par1) {
		super(par1);
		this.nbt = new NBTTagCompound();
		par1.writeToNBT(this.nbt);
	}

	@Override
	protected void write(ByteBuf buffer) {
		ByteBufUtils.writeTag(buffer, this.nbt);
	}

	@Override
	protected void read(ByteBuf buffer) {
		this.nbt = ByteBufUtils.readTag(buffer);
	}

	@Override
	public IMessage onMessage(PacketStationData message, MessageContext ctx) {
		World world = ctx.getServerHandler().playerEntity.worldObj;
		TileEntityStation tile = (TileEntityStation) message.getTileEntity(world);
		tile.setData(message.nbt);
		return null;
	}
}