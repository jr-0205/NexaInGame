package com.nexaclient.ingame.hud;

import com.nexaclient.ingame.compat.HudCompat;
import com.nexaclient.ingame.config.NexaConfig;
import com.nexaclient.ingame.modules.ModuleAccess;
import com.nexaclient.ingame.ui.NexaUi;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.scoreboard.ScoreboardEntry;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.scoreboard.number.StyledNumberFormat;
import java.util.Collection;
import java.util.Comparator;

public final class VisualHud {
    private VisualHud() { }

    public static void bosses(DrawContext context, Collection<ClientBossBar> bars) {
        var registry = ModuleAccess.registry();
        var state = registry.state("boss_bar");
        if (state.booleanSetting("hidden", false) || bars.isEmpty()) return;
        var client = MinecraftClient.getInstance();
        int count = Math.min(bars.size(), state.intSetting("limit", 3, 1, 6));
        begin(context, state, 192, count * 30);
        int row = 0;
        for (ClientBossBar bar : bars) {
            if (row >= count) break;
            int top = row++ * 30;
            NexaUi.roundedRect(context, 0, top, 192, 27, 5, alpha(0xFF0B1724, state));
            context.drawTextWithShadow(client.textRenderer,
                NexaUi.abbreviate(client.textRenderer, bar.getName().getString(), 144), 7, top + 5, alpha(NexaUi.TEXT, state));
            String percent = Math.round(bar.getPercent() * 100) + "%";
            context.drawTextWithShadow(client.textRenderer, percent, 185 - client.textRenderer.getWidth(percent), top + 5, alpha(NexaUi.TEXT_2, state));
            NexaUi.roundedRect(context, 7, top + 18, 178, 3, 1, alpha(0xFF314252, state));
            NexaUi.roundedRect(context, 7, top + 18, Math.round(178 * bar.getPercent()), 3, 1, alpha(registry.config().accentColor, state));
        }
        HudCompat.pop(context);
    }

    public static void scoreboard(DrawContext context, ScoreboardObjective objective) {
        var registry = ModuleAccess.registry();
        var state = registry.state("scoreboard");
        if (state.booleanSetting("hidden", false)) return;
        var client = MinecraftClient.getInstance();
        var board = objective.getScoreboard();
        var entries = board.getScoreboardEntries(objective).stream().filter(entry -> !entry.hidden())
            .sorted(Comparator.comparingInt(ScoreboardEntry::value).reversed().thenComparing(ScoreboardEntry::owner, String.CASE_INSENSITIVE_ORDER))
            .limit(state.intSetting("limit", 15, 3, 15)).toList();
        int logicalWidth = 176;
        int logicalHeight = 26 + entries.size() * 12;
        begin(context, state, logicalWidth, logicalHeight);
        NexaUi.roundedRect(context, 0, 0, logicalWidth, logicalHeight, 5, alpha(0xFF0B1724, state));
        context.fill(8, 20, logicalWidth - 8, 21, alpha(registry.config().accentColor, state));
        context.drawTextWithShadow(client.textRenderer, NexaUi.abbreviate(client.textRenderer, objective.getDisplayName().getString(), logicalWidth - 16), 8, 7, alpha(NexaUi.TEXT, state));
        int rowY = 26;
        for (var entry : entries) {
            var name = Team.decorateName(board.getScoreHolderTeam(entry.owner()), entry.name());
            var number = entry.formatted(objective.getNumberFormatOr(StyledNumberFormat.RED));
            int numberWidth = state.booleanSetting("scores", true) ? client.textRenderer.getWidth(number) : 0;
            var trimmed = client.textRenderer.trimToWidth(name, logicalWidth - numberWidth - 24);
            context.drawTextWithShadow(client.textRenderer, trimmed, 8, rowY, alpha(NexaUi.TEXT_2, state));
            if (numberWidth > 0) context.drawTextWithShadow(client.textRenderer, number, logicalWidth - numberWidth - 8, rowY, alpha(NexaUi.TEXT, state));
            rowY += 12;
        }
        HudCompat.pop(context);
    }

    private static void begin(DrawContext context, NexaConfig.ModuleConfig state, int width, int height) {
        state.validate();
        var window = MinecraftClient.getInstance().getWindow();
        float scale = Math.min(state.scale, Math.min((window.getScaledWidth() - 8f) / width, (window.getScaledHeight() - 8f) / height));
        int left = Math.round(state.x * Math.max(0, window.getScaledWidth() - width * scale));
        int top = Math.round(state.y * Math.max(0, window.getScaledHeight() - height * scale));
        HudCompat.push(context, left, top, scale);
    }

    private static int alpha(int color, NexaConfig.ModuleConfig state) {
        return (Math.round(state.opacity * 255) << 24) | (color & 0xFFFFFF);
    }
}
