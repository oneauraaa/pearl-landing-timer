package com.selim.pearllandingtimer;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public final class PearlLandingTimerModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> PearlLandingTimerClient.config().createConfigScreen(parent);
    }
}
