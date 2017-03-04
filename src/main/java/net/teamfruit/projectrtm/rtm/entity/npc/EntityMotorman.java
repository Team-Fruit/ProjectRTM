package net.teamfruit.projectrtm.rtm.entity.npc;

import java.io.File;

import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemWritableBook;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.teamfruit.projectrtm.ngtlib.io.NGTLog;
import net.teamfruit.projectrtm.ngtlib.io.NGTText;
import net.teamfruit.projectrtm.ngtlib.util.NGTUtil;
import net.teamfruit.projectrtm.rtm.RTMCore;
import net.teamfruit.projectrtm.rtm.entity.ai.EntityAIDriveWithMacro;
import net.teamfruit.projectrtm.rtm.entity.ai.EntityAIDrivingWithDiagram;
import net.teamfruit.projectrtm.rtm.entity.ai.EntityAIDrivingWithSignal;
import net.teamfruit.projectrtm.rtm.entity.npc.macro.TrainCommand;
import net.teamfruit.projectrtm.rtm.modelpack.modelset.ModelSetNPC;
import net.teamfruit.projectrtm.rtm.network.PacketNotice;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityMotorman extends EntityNPC {
	private EntityAIDriveWithMacro aiMacro;

	public EntityMotorman(World world) {
		super(world);
		this.aiMacro = new EntityAIDriveWithMacro(this);
		this.tasks.addTask(1, new EntityAISwimming(this));
		this.tasks.addTask(2, this.aiMacro);
		this.tasks.addTask(3, new EntityAIDrivingWithDiagram(this));
		this.tasks.addTask(4, new EntityAIDrivingWithSignal(this));
		this.tasks.addTask(5, new EntityAIWander(this, SPEED));
		this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(7, new EntityAILookIdle(this));
	}

	public EntityMotorman(World world, EntityPlayer player) {
		this(world);//AI登録のため
		this.func_152115_b(player.getUniqueID().toString());
	}

	@Override
	public void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(30, new ItemStack(Items.apple));
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		if (this.getDiagram()!=null) {
			NBTTagCompound diagramNBT = new NBTTagCompound();
			ItemStack itemstack = this.getDiagram();
			itemstack.writeToNBT(diagramNBT);
			NBTTagList nbttaglist = new NBTTagList();
			nbttaglist.appendTag(diagramNBT);
			nbt.setTag("DiagramRTM", nbttaglist);
		}
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		if (nbt.hasKey("DiagramRTM")) {
			NBTTagList nbttaglist = nbt.getTagList("DiagramRTM", 10);
			NBTTagCompound diagramNBT = (NBTTagCompound) nbttaglist.getCompoundTagAt(0);
			ItemStack itemstack = ItemStack.loadItemStackFromNBT(diagramNBT);
			this.setDiagram(itemstack);
		}
	}

	@Override
	public void onDeath(DamageSource source) {
		super.onDeath(source);
		if (!this.worldObj.isRemote&&this.hasDiagram()) {
			this.entityDropItem(this.getDiagram(), 1.0F);
		}
	}

	@Override
	public boolean interact(EntityPlayer player) {
		if (player.worldObj.isRemote) {
			player.openGui(RTMCore.instance, RTMCore.guiIdMotorman, player.worldObj, this.getEntityId(), 0, 0);
		}

		ItemStack itemstack = player.inventory.getCurrentItem();
		if (itemstack!=null) {
			if (itemstack.getItem() instanceof ItemWritableBook) {
				if (!this.worldObj.isRemote) {
					this.setDiagram(itemstack.copy());
				}
				--itemstack.stackSize;
			}
		}
		return false;
	}

	@SideOnly(Side.CLIENT)
	public void setMacro(File file) {
		String[] sa = NGTText.readText(file);
		StringBuilder sb = new StringBuilder("TMacro");
		for (String s : sa) {
			sb.append(TrainCommand.SEPARATOR);
			sb.append(s);
		}
		RTMCore.NETWORK_WRAPPER.sendToServer(
				new PacketNotice(PacketNotice.Side_SERVER, this.getEntityId(), 0, 0, sb.toString()));
		NGTLog.sendChatMessage(NGTUtil.getClientPlayer(), "Set macro : "+file.getName());
	}

	public void setMacro(String[] args) {
		this.aiMacro.setMacro(args);
	}

	public boolean hasDiagram() {
		ItemStack itemstack = this.getDiagram();
		return itemstack!=null&&itemstack.getItem() instanceof ItemWritableBook;
	}

	public ItemStack getDiagram() {
		return this.dataWatcher.getWatchableObjectItemStack(30);
	}

	public void setDiagram(ItemStack par1) {
		this.dataWatcher.updateObject(30, par1);
	}

	@Override
	public boolean isMotorman() {
		return true;
	}

	@Override
	public ModelSetNPC getModelSet() {
		return null;
	}
}