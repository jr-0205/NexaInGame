package com.nexaclient.ingame.modern;

import com.nexaclient.ingame.ui.NexaHomeScreen;
import net.minecraft.client.gui.screen.Screen;

public final class ModernNexaTitleScreen extends NexaHomeScreen {
    @Override protected Screen modulesScreen() { return new ModernNexaControlCenterScreen(this, ModernNexaInGameClient.MODULES); }
    @Override protected Screen editorScreen() { return new ModernNexaHudEditorScreen(this, ModernNexaInGameClient.MODULES); }
}
