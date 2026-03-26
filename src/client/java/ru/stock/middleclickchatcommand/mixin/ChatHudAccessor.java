package ru.stock.middleclickchatcommand.mixin;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(ChatHud.class)
public interface ChatHudAccessor {
    @Accessor("visibleMessages")
    List<ChatHudLine.Visible> middleclickchatcommand$getVisibleMessages();

    @Accessor("scrolledLines")
    int middleclickchatcommand$getScrolledLines();

    @Invoker("getChatScale")
    double middleclickchatcommand$invokeGetChatScale();

    @Invoker("getVisibleLineCount")
    int middleclickchatcommand$invokeGetVisibleLineCount();

    @Invoker("getLineHeight")
    int middleclickchatcommand$invokeGetLineHeight();
}
