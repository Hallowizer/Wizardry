package com.teamwizardry.wizardry.common.network;

import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.wizardry.init.ModPotions;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by LordSaad.
 */
public class PacketVanishPotion extends PacketBase {

	@Save
	private int entityID;
	@Save
	private int amplifier;
	@Save
	private int duration;
	@Save
	private boolean remove;

	public PacketVanishPotion(int entityID, int amplifier, int duration) {
		this.entityID = entityID;
		this.amplifier = amplifier;
		this.duration = duration;
	}

	public PacketVanishPotion(int entityID) {
		this.entityID = entityID;
		this.remove = true;
	}

	public PacketVanishPotion() {
	}

	@Override
	public void handle(MessageContext messageContext) {
		if (messageContext.side.isServer()) return;
		if (Minecraft.getMinecraft().player == null) return;

		World world = Minecraft.getMinecraft().player.world;

		EntityLivingBase entityLivingBase = (EntityLivingBase) world.getEntityByID(entityID);
		if (entityLivingBase == null) return;

		if (remove) entityLivingBase.removePotionEffect(ModPotions.VANISH);
		else entityLivingBase.addPotionEffect(new PotionEffect(ModPotions.VANISH, duration, amplifier, true, false));
	}
}
