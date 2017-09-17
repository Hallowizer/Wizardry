package com.teamwizardry.wizardry.common.network;

import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpFadeInOut;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.interp.InterpScale;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.awt.*;

/**
 * Created by LordSaad.
 */
public class PacketDevilDustFizzle extends PacketBase {

	@Save
	private Vec3d pos;
	@Save
	private int tick;

	public PacketDevilDustFizzle() {
	}

	public PacketDevilDustFizzle(Vec3d pos, int tick) {
		this.pos = pos;
		this.tick = tick;
	}

	@Override
	public void handle(MessageContext messageContext) {
		if (messageContext.side.isServer()) return;
		if (Minecraft.getMinecraft().player == null) return;

		World world = Minecraft.getMinecraft().player.world;

		ParticleBuilder glitter = new ParticleBuilder(30);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
		glitter.setMotionCalculationEnabled(true);
		glitter.setCollision(true);
		glitter.setCanBounce(true);

		Thread ticker = new Thread(() -> {
			while (--tick > 0) {
				try {
					Thread.sleep(8);
				} catch (InterruptedException e) {
					return;
				}

				if (RandUtil.nextInt(5) == 0)
					ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), 1, 0, (i, builder) -> {
						builder.setAcceleration(new Vec3d(0, RandUtil.nextDouble(-0.05, -0.01), 0));
						builder.setScale(RandUtil.nextFloat(0.01f, 0.3f));
						builder.setColor(new Color(RandUtil.nextFloat(0.9f, 1), RandUtil.nextFloat(0, 0.25f), 0));
						builder.setAlphaFunction(new InterpFadeInOut(0.0f, 0.3f));
						builder.setScaleFunction(new InterpScale(1, 0));
						Vec3d offset = new Vec3d(RandUtil.nextDouble(-0.3, 0.3), RandUtil.nextDouble(-0.3, 0), RandUtil.nextDouble(-0.3, 0.3));
						builder.setPositionOffset(offset);
						builder.setLifetime(RandUtil.nextInt(30, 50));
						builder.setMotion(new Vec3d(RandUtil.nextDouble(-0.1, 0.1), RandUtil.nextDouble(0.1, 0.5), RandUtil.nextDouble(-0.1, 0.1)));
					});
			}
		});
		ticker.start();
	}
}
