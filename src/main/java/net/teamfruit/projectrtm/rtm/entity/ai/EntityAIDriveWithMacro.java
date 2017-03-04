package net.teamfruit.projectrtm.rtm.entity.ai;

import net.minecraft.entity.ai.EntityAIBase;
import net.teamfruit.projectrtm.rtm.entity.npc.EntityMotorman;
import net.teamfruit.projectrtm.rtm.entity.npc.macro.MacroExecutor;
import net.teamfruit.projectrtm.rtm.entity.train.EntityTrainBase;

public class EntityAIDriveWithMacro extends EntityAIBase {
	private final EntityMotorman motorman;
	private MacroExecutor executor;

	public EntityAIDriveWithMacro(EntityMotorman par1) {
		this.motorman = par1;
		this.setMutexBits(1);
	}

	public void setMacro(String[] args) {
		this.executor = new MacroExecutor(args);
	}

	@Override
	public boolean shouldExecute() {
		return this.motorman.ridingEntity instanceof EntityTrainBase&&this.executor!=null&&!this.executor.finished();
	}

	@Override
	public void startExecuting() {
		this.executor.start(this.motorman.worldObj);
	}

	@Override
	public boolean continueExecuting() {
		if (!this.shouldExecute()) {
			this.executor.stop(this.motorman.worldObj);
			return false;
		}
		return true;
	}

	@Override
	public void updateTask() {
		this.executor.tick(this.motorman.worldObj, (EntityTrainBase) this.motorman.ridingEntity);
	}
}