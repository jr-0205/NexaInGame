package com.nexaclient.ingame.ui;

import com.nexaclient.ingame.ui.NexaHomeScreen;
import com.nexaclient.ingame.NexaInGameClient;
import net.minecraft.client.gui.screen.Screen;

public final class NexaTitleScreen extends NexaHomeScreen {
    @Override protected Screen modulesScreen() { return new NexaControlCenterScreen(this, NexaInGameClient.MODULES); }
    @Override protected Screen editorScreen() { return new NexaHudEditorScreen(this, NexaInGameClient.MODULES); }
}
