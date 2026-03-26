package ru.stock.middleclickchatcommand;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

public class MiddleClickChatCommandClient implements ClientModInitializer {
    public static final String MOD_ID = "middleclick-chat-command";

    @Override
    public void onInitializeClient() {
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> ModConfig.load());
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> ModConfig.save());
    }
}

