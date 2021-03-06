package net.teamfruit.projectrtm.rtm.world.station;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.teamfruit.projectrtm.ngtlib.math.AABBInt;

public class Station {
	protected World worldObj;
	protected String name;
	protected List<AABBInt> partsList = new ArrayList<AABBInt>();

	public Station() {
		this.name = "";
	}

	public Station(World world, String par2) {
		this.worldObj = world;
		this.name = par2;
	}

	public void readFromNBT(NBTTagCompound nbt) {
		this.name = nbt.getString("Name");
		NBTTagList nbttaglist = nbt.getTagList("Parts", 10);
		for (int i = 0; i<nbttaglist.tagCount(); ++i) {
			NBTTagCompound nbt1 = nbttaglist.getCompoundTagAt(i);
			int x0 = nbt1.getInteger("MinX");
			int y0 = nbt1.getInteger("MinY");
			int z0 = nbt1.getInteger("MinZ");
			int x1 = nbt1.getInteger("MaxX");
			int y1 = nbt1.getInteger("MaxY");
			int z1 = nbt1.getInteger("MaxZ");
			this.partsList.add(new AABBInt(x0, y0, z0, x1, y1, z1));
		}
	}

	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setString("Name", this.name);
		NBTTagList nbttaglist = new NBTTagList();
		Iterator iterator = this.partsList.iterator();
		while (iterator.hasNext()) {
			AABBInt chunk = (AABBInt) iterator.next();
			NBTTagCompound nbt1 = new NBTTagCompound();
			nbt1.setInteger("MinX", chunk.minX);
			nbt1.setInteger("MinY", chunk.minY);
			nbt1.setInteger("MinZ", chunk.minZ);
			nbt1.setInteger("MaxX", chunk.maxX);
			nbt1.setInteger("MaxY", chunk.maxY);
			nbt1.setInteger("MaxZ", chunk.maxZ);
			nbttaglist.appendTag(nbt1);
		}
		nbt.setTag("Parts", nbttaglist);
	}

	public void add(AABBInt aabb) {
		this.partsList.add(aabb);
	}

	/*public class StationChunk
	{
		public final int blockX;
		public final int blockY;
		public final int blockZ;
	
		public StationChunk(int x, int y, int z)
		{
			this.blockX = x;
			this.blockY = y;
			this.blockZ = z;
		}
	
		public int getChunkX()
		{
			return this.blockX >> 4;
		}
	
		public int getChunkY()
		{
			return this.blockY >> 4;
		}
	
		public int getChunkZ()
		{
			return this.blockZ >> 4;
		}
	}*/
}