package com.teamwizardry.wizardry.common.module.shapes;

import com.teamwizardry.wizardry.api.spell.annotation.ModuleOverride;

public abstract class AbstractModuleShape {
	
	@ModuleOverride("generic_chargeup_time")
	public int modifyChargeupTime(int originalTime) {
		return originalTime;
	}
	
}