package net.teamfruit.projectrtm.rtm.entity.train.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.WorldSavedData;

public class FormationData extends WorldSavedData {
	public FormationData(String name) {
		super(name);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		NBTTagList tagList = nbt.getTagList("Formations", 10);
		for (int i = 0; i<tagList.tagCount(); ++i) {
			NBTTagCompound tag = tagList.getCompoundTagAt(i);
			Formation formation = Formation.readFromNBT(tag, false);
			//登録はFormationコンストラクタで行ってる
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		NBTTagList tagList = new NBTTagList();
		for (Formation formation : FormationManager.getInstance().getFormations().values()) {
			NBTTagCompound tag = new NBTTagCompound();
			formation.writeToNBT(tag, false);
			tagList.appendTag(tag);
		}
		nbt.setTag("Formations", tagList);
	}
}