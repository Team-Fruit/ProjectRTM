package net.teamfruit.projectrtm.rtm.gui;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.teamfruit.projectrtm.rtm.RTMCore;
import net.teamfruit.projectrtm.rtm.block.tileentity.TileEntityMovingMachine;
import net.teamfruit.projectrtm.rtm.block.tileentity.TileEntityStation;
import net.teamfruit.projectrtm.rtm.block.tileentity.TileEntityTrainWorkBench;
import net.teamfruit.projectrtm.rtm.electric.TileEntitySignalConverter;
import net.teamfruit.projectrtm.rtm.electric.TileEntityTicketVendor;
import net.teamfruit.projectrtm.rtm.entity.npc.EntityMotorman;
import net.teamfruit.projectrtm.rtm.entity.npc.EntityNPC;
import net.teamfruit.projectrtm.rtm.entity.train.EntityFreightCar;
import net.teamfruit.projectrtm.rtm.entity.train.EntityTrainBase;
import net.teamfruit.projectrtm.rtm.entity.train.parts.EntityContainer;
import net.teamfruit.projectrtm.rtm.modelpack.IModelSelector;
import net.teamfruit.projectrtm.rtm.modelpack.IModelSelectorWithType;
import net.teamfruit.projectrtm.rtm.modelpack.texture.ITextureHolder;
import cpw.mods.fml.common.network.IGuiHandler;

public class RTMGuiHandler implements IGuiHandler {
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID==RTMCore.guiIdFreightCar) {
			Entity entity = world.getEntityByID(x);
			if (entity instanceof EntityFreightCar) {
				return new ContainerFreightCar(player.inventory, (EntityFreightCar) entity);
			}
		} else if (ID==RTMCore.guiIdItemContainer) {
			Entity entity = world.getEntityByID(x);
			if (entity instanceof EntityContainer) {
				return new ContainerItemContainer(player.inventory, (EntityContainer) entity);
			}
		} else if (ID==RTMCore.guiIdTrainControlPanel) {
			Entity entity0 = world.getEntityByID(x);
			Entity entity1 = entity0.riddenByEntity;
			if (entity0 instanceof EntityTrainBase&&entity1 instanceof EntityPlayer) {
				return new ContainerTrainControlPanel((EntityTrainBase) entity0, (EntityPlayer) entity1);
			}
		} else if (ID==RTMCore.guiIdTrainWorkBench) {
			TileEntityTrainWorkBench tile = (TileEntityTrainWorkBench) world.getTileEntity(x, y, z);
			return new ContainerRTMWorkBench(player.inventory, world, tile, player.capabilities.isCreativeMode);
		} else if (ID==RTMCore.guiIdTicketVendor) {
			TileEntity tile = world.getTileEntity(x, y, z);
			if (tile instanceof TileEntityTicketVendor) {
				return new ContainerTicketVendor(player.inventory, (TileEntityTicketVendor) tile);
			}
		} else if (ID==RTMCore.guiIdNPC) {
			return new ContainerNPC(player, (EntityNPC) world.getEntityByID(x));
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID==RTMCore.guiIdSelectEntityModel) {
			Entity entity = world.getEntityByID(x);
			if (entity instanceof IModelSelectorWithType) {
				return new GuiSelectModel(world, (IModelSelectorWithType) entity);
			} else if (entity instanceof IModelSelector) {
				return new GuiSelectModel(world, (IModelSelector) entity);
			}
		} else if (ID==RTMCore.guiIdSelectTileEntityModel) {
			TileEntity tile = world.getTileEntity(x, y, z);
			if (tile instanceof IModelSelectorWithType) {
				return new GuiSelectModel(world, (IModelSelectorWithType) tile);
			} else if (tile instanceof IModelSelector) {
				return new GuiSelectModel(world, (IModelSelector) tile);
			}
		} else if (ID==RTMCore.guiIdSelectItemModel) {
			Item item = player.inventory.getCurrentItem().getItem();
			return new GuiSelectModel(world, (IModelSelectorWithType) item);
		} else if (ID==RTMCore.guiIdFreightCar) {
			Entity entity = world.getEntityByID(x);
			if (entity instanceof EntityFreightCar) {
				return new GuiFreightCar(player.inventory, (EntityFreightCar) entity);
			}
		} else if (ID==RTMCore.guiIdItemContainer) {
			Entity entity = world.getEntityByID(x);
			if (entity instanceof EntityContainer) {
				return new GuiItemContainer(player.inventory, (EntityContainer) entity);
			}
		} else if (ID==RTMCore.guiIdSelectTexture) {
			return new GuiSelectTexture((ITextureHolder) world.getTileEntity(x, y, z));
		} else if (ID==RTMCore.guiIdTrainControlPanel) {
			Entity entity0 = world.getEntityByID(x);
			Entity entity1 = entity0.riddenByEntity;
			if (entity0 instanceof EntityTrainBase&&entity1 instanceof EntityPlayer) {
				return new GuiTrainControlPanel(new ContainerTrainControlPanel((EntityTrainBase) entity0, (EntityPlayer) entity1));
			}
		} else if (ID==RTMCore.guiIdTrainWorkBench) {
			TileEntityTrainWorkBench tile = (TileEntityTrainWorkBench) world.getTileEntity(x, y, z);
			return new GuiRTMWorkBench(player.inventory, world, tile, player.capabilities.isCreativeMode);
		} else if (ID==RTMCore.guiIdSignalConverter) {
			return new GuiSignalConverter((TileEntitySignalConverter) world.getTileEntity(x, y, z));
		} else if (ID==RTMCore.guiIdTicketVendor) {
			TileEntity tile = world.getTileEntity(x, y, z);
			if (tile!=null&&tile instanceof TileEntityTicketVendor) {
				return new GuiTicketVendor(player.inventory, (TileEntityTicketVendor) tile);
			}
		} else if (ID==RTMCore.guiIdStation) {
			TileEntity tile = world.getTileEntity(x, y, z);
			if (tile!=null&&tile instanceof TileEntityStation) {
				return new GuiStation((TileEntityStation) tile);
			}
		} else if (ID==RTMCore.guiIdPaintTool) {
			Entity entity = world.getEntityByID(x);
			if (entity instanceof EntityPlayer) {
				return new GuiPaintTool((EntityPlayer) entity);
			}
		} else if (ID==RTMCore.guiIdMovingMachine) {
			TileEntity tile = world.getTileEntity(x, y, z);
			if (tile!=null&&tile instanceof TileEntityMovingMachine) {
				return new GuiMovingMachine((TileEntityMovingMachine) tile);
			}
		} else if (ID==RTMCore.guiIdNPC) {
			return new GuiNPC(player, (EntityNPC) world.getEntityByID(x));
		} else if (ID==RTMCore.guiIdMotorman) {
			return GuiMotorman.getGui((EntityMotorman) world.getEntityByID(x));
		}
		return null;
	}
}