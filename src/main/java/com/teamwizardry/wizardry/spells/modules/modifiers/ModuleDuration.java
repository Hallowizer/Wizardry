package com.teamwizardry.wizardry.spells.modules.modifiers;

import com.teamwizardry.wizardry.api.modules.Module;
import com.teamwizardry.wizardry.api.modules.attribute.Attribute;
import com.teamwizardry.wizardry.api.modules.attribute.AttributeMap;
import com.teamwizardry.wizardry.api.modules.attribute.AttributeModifier;
import com.teamwizardry.wizardry.spells.modules.ModuleType;

public class ModuleDuration extends Module implements IModifier {
    public ModuleDuration() {
        canHaveChildren = false;
    }

    @Override
    public ModuleType getType() {
        return ModuleType.MODIFIER;
    }

    @Override
    public void apply(AttributeMap map) {
        map.putModifier(Attribute.DURATION, new AttributeModifier(AttributeModifier.Operation.ADD, 1));

        map.putModifier(Attribute.COST, new AttributeModifier(AttributeModifier.Operation.MULTIPLY, 1.1));
        map.putModifier(Attribute.BURNOUT, new AttributeModifier(AttributeModifier.Operation.MULTIPLY, 1.1));
    }
}