package net.teamfruit.projectrtm.rtm.rail;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.teamfruit.projectrtm.ngtlib.math.NGTMath;
import net.teamfruit.projectrtm.rtm.RTMAchievement;
import net.teamfruit.projectrtm.rtm.RTMBlock;
import net.teamfruit.projectrtm.rtm.RTMCore;
import net.teamfruit.projectrtm.rtm.RTMItem;
import net.teamfruit.projectrtm.rtm.RTMRail;
import net.teamfruit.projectrtm.rtm.item.ItemRail;
import net.teamfruit.projectrtm.rtm.item.ItemWrench;
import net.teamfruit.projectrtm.rtm.rail.util.RailMaker;
import net.teamfruit.projectrtm.rtm.rail.util.RailMap;
import net.teamfruit.projectrtm.rtm.rail.util.RailMapSlope;
import net.teamfruit.projectrtm.rtm.rail.util.RailPosition;
import net.teamfruit.projectrtm.rtm.rail.util.RailProperty;
import net.teamfruit.projectrtm.rtm.rail.util.SwitchType;

public class BlockMarker extends BlockContainer {
	/**0:normal, 1:switch, 2:slope*/
	public final int markerType;
	@SideOnly(Side.CLIENT)
	private IIcon[] icons;

	public BlockMarker(final int type) {
		super(Material.glass);
		this.markerType = type;
		setLightOpacity(0);
		setLightLevel(1.0F);
		setStepSound(soundTypeGlass);
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(final World world, final int par2) {
		return new TileEntityMarker();
	}

	@Override
	public void dropBlockAsItemWithChance(final World world, final int x, final int y, final int z, final int par5, final float par6, final int par7) {
		;
	}

	/*@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
	{
		this.setBlockBoundsBasedOnState(world, x, y, z);
		return super.getCollisionBoundingBoxFromPool(world, x, y, z);
	}
	
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess blockAccess, int x, int y, int z)
	{
		TileEntity tile = blockAccess.getTileEntity(x, y, z);
		if(tile instanceof TileEntityMarker)
		{
			int height = ((TileEntityMarker)tile).getHeight();
			this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, (float)height * 0.0625F, 1.0F);
		}
	}*/

	@Override
	public void onBlockPlacedBy(final World world, final int x, final int y, final int z, final EntityLivingBase entity, final ItemStack itemStack) {
		final int meta = itemStack.getItemDamage();
		int playerFacing;
		if ((this.markerType==0||this.markerType==1)&&meta>=4)
			playerFacing = MathHelper.floor_double(NGTMath.normalizeAngle(entity.rotationYaw+180.0D)/90D)&3;//斜め
		else
			playerFacing = MathHelper.floor_double(NGTMath.normalizeAngle(entity.rotationYaw+180.0D)/90D+0.5D)&3;
		final int i = meta/4;
		world.setBlock(x, y, z, this, playerFacing+i*4, 2);
	}

	@Override
	public boolean onBlockActivated(final World world, final int x, final int y, final int z, final EntityPlayer player, final int par6, final float par7, final float par8, final float par9) {
		final ItemStack item = player.inventory.getCurrentItem();
		if (item!=null) {
			final TileEntity tile = world.getTileEntity(x, y, z);
			if (!(tile instanceof TileEntityMarker))
				return true;
			final TileEntityMarker marker = (TileEntityMarker) tile;

			if (item.getItem()==RTMItem.wrench) {
				if (!world.isRemote)
					if (marker.startY<0)
						onMarkerActivated(world, x, y, z, player, false);
					else
						onMarkerActivated(world, marker.startX, marker.startY, marker.startZ, player, false);
				((ItemWrench) RTMItem.wrench).onRightClickMarker(item, world, player, marker);
				return true;
			} else if (item.getItem()==RTMItem.paddle) {
				marker.displayDistance ^= true;
				return true;
			}
		}

		if (!world.isRemote)
			if (onMarkerActivated(world, x, y, z, player, true)) {
				player.addStat(RTMAchievement.layRail, 1);
				if (!player.capabilities.isCreativeMode)
					if (item!=null)
						--item.stackSize;
			}

		return true;
	}

	public boolean onMarkerActivated(final World world, final int x, final int y, final int z, final EntityPlayer player, final boolean makeRail) {
		final RailProperty prop = hasRail(player, makeRail);
		if (prop!=null) {
			final int type = this.markerType;
			final int dis1 = RTMCore.railGeneratingDistance;
			final int dis2 = dis1*2;
			final int hei1 = RTMCore.railGeneratingHeight;
			final int hei2 = hei1*2;
			final boolean isCreative = player.capabilities.isCreativeMode;

			if (type==0)
				for (int i = 0; i<dis2; ++i)
					for (int j = 0; j<hei2; ++j)
						for (int k = 0; k<dis2; ++k) {
							final int x0 = x-dis1+i;
							final int y0 = y-hei1+j;
							final int z0 = z-dis1+k;
							if (!(i==dis1&&k==dis1)&&world.getBlock(x0, y0, z0)==RTMBlock.marker) {
								final RailPosition rpS = getRailPosition(world, x, y, z, (byte) 0, (byte) 0);
								final RailPosition rpE = getRailPosition(world, x0, y0, z0, (byte) 0, (byte) 0);
								if (rpS==null||rpE==null)
									continue;//設置後すぐ右クリするとnullなので

								if (rpE.posY>=rpS.posY)
									return createRail0(world, x, y, z, x0, y0, z0, prop, makeRail, isCreative);
								else
									return createRail0(world, x0, y0, z0, x, y, z, prop, makeRail, isCreative);
							}
						}
			else if (type==1) {
				final List<int[]> list = new ArrayList<int[]>();
				for (int i = 0; i<dis2; ++i) //for(int j = 0; j < hei2; ++j)
				{
					for (int k = 0; k<dis2; ++k) {
						final int x0 = x-dis1+i;
						final int y0 = y;// - hei1 + j;
						final int z0 = z-dis1+k;
						final Block block = world.getBlock(x0, y0, z0);
						if (block==RTMBlock.marker||block==RTMBlock.markerSwitch)
							list.add(new int[] { x0, y0, z0 });
					}
				}

				if (list.size()>0)
					return createRail1(world, x, y, z, list, prop, makeRail, isCreative);
			} else if (type==2)
				return createRail2(world, x, y, z, prop, makeRail, isCreative);
		}
		return false;
	}

	/**
	 * 通常のレール<br>
	 * y0 <= y1でなければならない
	 */
	private boolean createRail0(final World world, final int x0, final int y0, final int z0, final int x1, final int y1, final int z1, final RailProperty prop, final boolean makeRail, final boolean isCreative) {
		final byte dir0 = getMarkerDir(RTMBlock.marker, world.getBlockMetadata(x0, y0, z0));
		final byte dir1 = getMarkerDir(RTMBlock.marker, world.getBlockMetadata(x1, y1, z1));

		final RailPosition rp0 = getRailPosition(world, x0, y0, z0, dir0, (byte) 0);
		final RailPosition rp1 = getRailPosition(world, x1, y1, z1, dir1, (byte) 0);
		final RailMap railMap = new RailMap(rp0, rp1);

		if (makeRail&&railMap.canPlaceRail(world, isCreative, prop)) {
			//railMap.setRail(world, RTMRail.largeRailBase[shape[0]], x0, y0, z0);
			railMap.setRail(world, RTMRail.largeRailBase0, x0, y0, z0, prop);

			//world.setBlock(x0, y0, z0, RTMRail.largeRailCore[shape[0]], 0, 2);
			world.setBlock(x0, y0, z0, RTMRail.largeRailCore0, 0, 2);
			final TileEntityLargeRailCore tile = (TileEntityLargeRailCore) world.getTileEntity(x0, y0, z0);
			tile.setRailPositions(new RailPosition[] { rp0, rp1 });
			tile.setProperty(prop);
			tile.setStartPoint(x0, y0, z0);

			tile.createRailMap();
			tile.sendPacket();

			if (world.getBlock(x1, y1, z1) instanceof BlockMarker)
				world.setBlockToAir(x1, y1, z1);

			return true;
		} else {
			final TileEntity tile = world.getTileEntity(x0, y0, z0);
			if (tile instanceof TileEntityMarker) {
				final List<int[]> list = new ArrayList<int[]>();
				list.add(new int[] { x0, y0, z0 });
				list.add(new int[] { x1, y1, z1 });
				((TileEntityMarker) tile).setMarkersPos(list, false);
			}
			return false;
		}
	}

	/**
	 * 分岐レール<br>
	 * @param list {x, y, z}
	 */
	private boolean createRail1(final World world, final int x, final int y, final int z, final List<int[]> list, final RailProperty prop, final boolean makeRail, final boolean isCreative) {
		final List<RailPosition> rpList = new ArrayList<RailPosition>();

		for (int i = 0; i<list.size(); ++i) {
			final int x0 = list.get(i)[0];
			final int y0 = list.get(i)[1];
			final int z0 = list.get(i)[2];
			final byte dir = getMarkerDir(world.getBlock(x0, y0, z0), world.getBlockMetadata(x0, y0, z0));
			final Block block = world.getBlock(x0, y0, z0);
			final byte b = (byte) (block==RTMBlock.markerSwitch ? 1 : 0);
			final RailPosition rp = getRailPosition(world, x0, y0, z0, dir, b);
			rpList.add(rp);
		}

		final RailMaker railMaker = new RailMaker(world, rpList);
		final SwitchType st = railMaker.getSwitch();
		if (st==null)
			return false;
		final RailMap[] railMaps = st.getAllRailMap();
		if (railMaps==null)
			return false;

		boolean flag = false;
		for (final RailMap rm : railMaps)
			if (!rm.canPlaceRail(world, isCreative, prop))
				flag = true;

		if (!makeRail||flag) {
			final TileEntity tile = world.getTileEntity(x, y, z);
			if (tile instanceof TileEntityMarker)
				((TileEntityMarker) tile).setMarkersPos(list, false);
			return false;//障害物あり
		}

		for (final RailMap rm : railMaps)
			rm.setRail(world, RTMRail.largeRailBase0, x, y, z, prop);

		for (final int[] aint : list) {
			world.setBlock(aint[0], aint[1], aint[2], RTMRail.largeRailSwitchBase0, 0, 2);
			final TileEntityLargeRailSwitchBase tile = (TileEntityLargeRailSwitchBase) world.getTileEntity(aint[0], aint[1], aint[2]);
			tile.setStartPoint(x, y, z);
		}

		world.setBlock(x, y, z, RTMRail.largeRailSwitchCore0, 0, 2);
		final TileEntityLargeRailSwitchCore tile = (TileEntityLargeRailSwitchCore) world.getTileEntity(x, y, z);
		tile.setRailPositions(rpList.toArray(new RailPosition[rpList.size()]));
		tile.setProperty(prop);
		//tile.setSwitchType(railMaker.getSwitchType());
		tile.setStartPoint(x, y, z);

		tile.createRailMap();
		tile.sendPacket();
		return true;
	}

	/**坂レール*/
	private boolean createRail2(final World world, final int x0, final int y0, final int z0, final RailProperty prop, final boolean makeRail, final boolean isCreative) {
		final int meta = world.getBlockMetadata(x0, y0, z0);
		final byte dir0 = getMarkerDir(RTMBlock.markerSlope, meta);
		final byte dir1 = (byte) (dir0+4&7);
		final double d0 = meta<4 ? 15.0D : meta<8 ? 7.0D : meta<12 ? 3.0D : 1.0D;
		final float f0 = NGTMath.toRadians(dir0*45.0F);
		final int x1 = x0+MathHelper.floor_double(MathHelper.sin(f0)*d0);
		final int z1 = z0+MathHelper.floor_double(MathHelper.cos(f0)*d0);
		final byte type = (byte) (meta<4 ? 0 : meta<8 ? 1 : meta<12 ? 2 : 3);

		final RailPosition rp0 = new RailPosition(x0, y0, z0, dir0);
		final RailPosition rp1 = new RailPosition(x1, y0, z1, dir1);
		final RailMapSlope railMap = new RailMapSlope(rp0, rp1, type);

		if (makeRail&&railMap.canPlaceRail(world, isCreative, prop)) {
			railMap.setRail(world, RTMRail.largeRailSlopeBase0, x0, y0, z0, prop);

			world.setBlock(x0, y0, z0, RTMRail.largeRailSlopeCore0, 0, 2);
			final TileEntityLargeRailSlopeCore tile = (TileEntityLargeRailSlopeCore) world.getTileEntity(x0, y0, z0);
			tile.setRailPositions(new RailPosition[] { rp0, rp1 });
			tile.setProperty(prop);
			tile.setSlopeType(type);
			tile.setStartPoint(x0, y0, z0);

			tile.createRailMap();
			tile.sendPacket();
			return true;
		} else {
			final TileEntity tile = world.getTileEntity(x0, y0, z0);
			if (tile instanceof TileEntityMarker) {
				final List<int[]> list = new ArrayList<int[]>();
				list.add(new int[] { x0, y0, z0 });
				list.add(new int[] { x1, y0, z1 });
				((TileEntityMarker) tile).setMarkersPos(list, false);
			}
			return false;
		}
	}

	public static byte getMarkerDir(final Block block, final int meta) {
		final int i0 = meta&3;
		int i1 = (6-i0&3)*2;
		if ((block==RTMBlock.marker||block==RTMBlock.markerSwitch)&&meta>=4)
			i1 = i1+7&7;
		return (byte) i1;
	}

	private RailPosition getRailPosition(final World world, final int x, final int y, final int z, final byte dir, final byte type) {
		final TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof TileEntityMarker) {
			final TileEntityMarker marker = (TileEntityMarker) tile;
			return marker.getMarkerRP();
		}
		return null;
	}

	public RailProperty hasRail(final EntityPlayer player, final boolean par2) {
		final ItemStack item = player.inventory.getCurrentItem();
		if (item!=null&&item.getItem()==RTMItem.itemLargeRail)
			/*int damage = item.getItemDamage();
			int i0 = damage / 10;
			int i1 = damage % 10;
			
			if(i0 < RTMItem.RAIL_ICON && i1 < RTMItem.RAIL_SHAPE)
			{
				return new int[]{i0, i1};
			}*/
			return ItemRail.getProperty(item);

		if (player.capabilities.isCreativeMode||!par2)
			/*int i1 = player.isSneaking() ? 2 : 0;
			return new int[]{2, i1};*/
			return ItemRail.getDefaultProperty();

		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(final Item par1, final CreativeTabs tab, final List list) {
		switch (this.markerType) {
			case 0:
				list.add(new ItemStack(par1, 1, 0));
				list.add(new ItemStack(par1, 1, 4));
				break;
			case 1:
				list.add(new ItemStack(par1, 1, 0));
				list.add(new ItemStack(par1, 1, 4));
				break;
			case 2:
				list.add(new ItemStack(par1, 1, 0));
				list.add(new ItemStack(par1, 1, 4));
				list.add(new ItemStack(par1, 1, 8));
				list.add(new ItemStack(par1, 1, 12));
				break;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(final int par1, final int par2) {
		final int i = this.markerType==0||this.markerType==1 ? 7 : 3;
		return this.icons[par2&i];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(final IIconRegister register) {
		this.icons = new IIcon[8];
		this.icons[0] = register.registerIcon("rtm:marker_0");
		this.icons[1] = register.registerIcon("rtm:marker_1");
		this.icons[2] = register.registerIcon("rtm:marker_2");
		this.icons[3] = register.registerIcon("rtm:marker_3");
		this.icons[4] = register.registerIcon("rtm:marker2_0");
		this.icons[5] = register.registerIcon("rtm:marker2_1");
		this.icons[6] = register.registerIcon("rtm:marker2_2");
		this.icons[7] = register.registerIcon("rtm:marker2_3");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderColor(final int par1) {
		switch (this.markerType) {
			//case 0: return par1 < 4 ? 0xFF0000 : 0xFFFFFF;
			case 0:
				return 0xFF0000;
			case 1:
				return 0x0000FF;
			case 2:
				switch (par1/4) {
					case 0:
						return 0xFFFF00;
					case 1:
						return 0xDDDD00;
					case 2:
						return 0xBBBB00;
					case 3:
						return 0x999900;
				}
			default:
				return 16777215;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int colorMultiplier(final IBlockAccess world, final int x, final int y, final int z) {
		final int meta = world.getBlockMetadata(x, y, z);
		return getRenderColor(meta);
	}
}