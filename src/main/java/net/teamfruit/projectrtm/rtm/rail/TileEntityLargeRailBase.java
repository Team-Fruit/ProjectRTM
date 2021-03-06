package net.teamfruit.projectrtm.rtm.rail;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.teamfruit.projectrtm.ngtlib.block.TileEntityCustom;
import net.teamfruit.projectrtm.ngtlib.protection.Lockable;
import net.teamfruit.projectrtm.rtm.RTMCore;
import net.teamfruit.projectrtm.rtm.network.PacketLargeRailBase;
import net.teamfruit.projectrtm.rtm.rail.util.RailMap;
import net.teamfruit.projectrtm.rtm.rail.util.RailProperty;

public class TileEntityLargeRailBase extends TileEntityCustom implements ILargeRail, Lockable {
	protected int[] startPoint = new int[3];

	/**ブロックの当たり判定が設定されている*/
	private boolean finishSetupBlockBounds;

	/**
	 * {xNzP, xPzP, xPzN, xNzN}<br>
	 * 描画と当たり判定の計算に使用
	 */
	private float[] blockHeights;

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		this.startPoint[0] = nbt.getInteger("spX");
		this.startPoint[1] = nbt.getInteger("spY");
		this.startPoint[2] = nbt.getInteger("spZ");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		nbt.setInteger("spX", this.startPoint[0]);
		nbt.setInteger("spY", this.startPoint[1]);
		nbt.setInteger("spZ", this.startPoint[2]);
	}

	public int[] getStartPoint() {
		return this.startPoint;
	}

	public void setStartPoint(int x, int y, int z) {
		this.startPoint[0] = x;
		this.startPoint[1] = y;
		this.startPoint[2] = z;
	}

	public boolean isTrainOnRail() {
		TileEntityLargeRailCore tile = this.getRailCore();
		if (tile!=null) {
			return tile.isCollidedTrain;
		}
		return false;
	}

	@Override
	public Packet getDescriptionPacket() {
		RTMCore.NETWORK_WRAPPER.sendToAll(new PacketLargeRailBase(this));
		return null;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();

		if (this.worldObj.isRemote) {
			if (!this.finishSetupBlockBounds&&this.getRailCore()!=null) {
				this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
				this.finishSetupBlockBounds = true;
			}
		}
	}

	@Override
	public RailMap getRailMap(Entity entity) {
		TileEntityLargeRailCore tile = this.getRailCore();
		if (tile!=null) {
			return ((ILargeRail) tile).getRailMap(entity);
		}
		return null;
	}

	public TileEntityLargeRailCore getRailCore() {
		TileEntity tile = this.worldObj.getTileEntity(this.startPoint[0], this.startPoint[1], this.startPoint[2]);
		if (tile!=null&&tile instanceof TileEntityLargeRailCore) {
			return (TileEntityLargeRailCore) tile;
		}
		return null;
	}

	public static TileEntityLargeRailBase getRailFromCoordinates(World world, double px, double py, double pz) {
		int x = MathHelper.floor_double(px);
		int y = MathHelper.floor_double(py);
		int z = MathHelper.floor_double(pz);
		while (y>0) {
			Block block = world.getBlock(x, y, z);
			if (block instanceof BlockLargeRailBase) {
				break;
			}
			--y;
		}

		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile!=null&&tile instanceof TileEntityLargeRailBase) {
			return (TileEntityLargeRailBase) tile;
		}
		return null;
	}

	/**
	 * 指定した座標の下にあるレールのRailMapを返す
	 * @param entity 列車、車止め、など
	 */
	public static RailMap getRailMapFromCoordinates(World world, Entity entity, double px, double py, double pz) {
		TileEntityLargeRailBase rail = TileEntityLargeRailBase.getRailFromCoordinates(world, px, py, pz);
		if (rail!=null) {
			return rail.getRailMap(entity);
		}
		return null;
	}

	@Override
	public Block getBlockType() {
		if (this.blockType==null) {
			Block block = this.worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord);
			if (block instanceof BlockLargeRailBase) {
				this.blockType = block;
			}
		}

		return this.blockType;
	}

	/**{xNzP, xPzP, xPzN, xNzN}*/
	public float[] getBlockHeights(int x, int y, int z, float defaultHeight, boolean useCache) {
		if (useCache&&this.blockHeights!=null) {
			return this.blockHeights;
		}

		if (this.finishSetupBlockBounds||!useCache) {
			float[] fa = this.getBlockHeights(x, y, z, defaultHeight);
			if (fa!=null) {
				if (useCache) {
					this.blockHeights = fa;
					if (!this.worldObj.isRemote) {
						this.finishSetupBlockBounds = true;
					}
				}
				return fa;
			}
		}

		return new float[] { 0.0625F, 0.0625F, 0.0625F, 0.0625F };
	}

	private float[] getBlockHeights(int x, int y, int z, float defaultHeight) {
		RailMap rm = (RailMap) this.getRailMap(null);
		if (rm!=null) {
			float[] fa = new float[] { defaultHeight, defaultHeight, defaultHeight, defaultHeight };
			for (int i = 0; i<fa.length; ++i) {
				int x0 = x+((i==1||i==2) ? 1 : 0);
				int z0 = z+((i==0||i==1) ? 1 : 0);
				int split = rm.getNearlestPoint(128, x0, z0);
				double height = rm.getRailHeight(128, split);
				//this.blockHeights[i] = (float)(height - (double)y);
				fa[i] += (float) (height-(double) y-0.0625D);
			}
			return fa;
		}
		return null;
	}

	/**音が響くレールかどうか*/
	public boolean isReberbSound() {
		TileEntityLargeRailCore core = this.getRailCore();
		if (core!=null) {
			RailProperty property = core.getProperty();
			if (!property.block.isOpaqueCube()) {
				Block block = this.worldObj.getBlock(this.xCoord, this.yCoord-1, this.zCoord);
				if (!block.isOpaqueCube()) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void setPos(int x, int y, int z, int prevX, int prevY, int prevZ) {
		int difX = x-prevX;
		int difY = y-prevY;
		int difZ = z-prevZ;
		this.startPoint[0] += difX;
		this.startPoint[1] += difY;
		this.startPoint[2] += difZ;
		super.setPos(x, y, z, prevX, prevY, prevZ);
	}

	@Override
	public Object getTarget(World world, int x, int y, int z) {
		return this.getRailCore();
	}

	@Override
	public boolean lock(EntityPlayer player, String code) {
		return true;
	}

	@Override
	public boolean unlock(EntityPlayer player, String code) {
		return true;
	}
}