package com.nexaclient.ingame.modules;

public record NexaModule(
    String id,
    String name,
    String description,
    ModuleCategory category,
    boolean defaultEnabled,
    float defaultX,
    float defaultY,
    boolean editableHud,
    boolean implemented
) {}
