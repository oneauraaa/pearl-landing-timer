package oneaura.pearllandingtimer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.item.EnderPearlItem;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import oneaura.pearllandingtimer.config.PearlLandingTimerConfig;
import oneaura.pearllandingtimer.hud.PearlLandingHudRenderer;
import oneaura.pearllandingtimer.tracking.PearlTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PearlLandingTimerClient implements ClientModInitializer {
	public static final String MOD_ID = "pearl-landing-timer";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static PearlLandingTimerConfig config;
	private static PearlTracker pearlTracker;

	@Override
	public void onInitializeClient() {
		config = PearlLandingTimerConfig.load();
		pearlTracker = new PearlTracker();

		ClientTickEvents.END_CLIENT_TICK.register(client -> pearlTracker.tick(client));
		UseItemCallback.EVENT.register((player, world, hand) -> {
			if (world.isClient()
					&& player.getStackInHand(hand).isOf(Items.ENDER_PEARL)
					&& !player.getItemCooldownManager().isCoolingDown(player.getStackInHand(hand))) {
				pearlTracker.trackManualThrow(world, player.getEyePos(), player.getRotationVector().multiply(EnderPearlItem.POWER), player);
			}

			return ActionResult.PASS;
		});
		HudElementRegistry.attachElementBefore(
				VanillaHudElements.CHAT,
				Identifier.of(MOD_ID, "pearl_landing_timer"),
				new PearlLandingHudRenderer(config, pearlTracker)
		);

		LOGGER.info("Pearl Landing Timer initialized");
	}

	public static PearlLandingTimerConfig getConfig() {
		return config;
	}

	public static PearlTracker getPearlTracker() {
		return pearlTracker;
	}
}
