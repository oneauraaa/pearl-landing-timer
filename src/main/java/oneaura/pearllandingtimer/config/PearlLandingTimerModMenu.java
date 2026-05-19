package oneaura.pearllandingtimer.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.screen.Screen;
import oneaura.pearllandingtimer.screen.PearlLandingTimerSettingsScreen;

public class PearlLandingTimerModMenu implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return (Screen parent) -> new PearlLandingTimerSettingsScreen(parent);
	}
}
