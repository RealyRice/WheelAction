package ru.stock.middleclickchatcommand.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.font.DrawnTextConsumer;
import net.minecraft.text.Style;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.stock.middleclickchatcommand.ModConfig;
import ru.stock.middleclickchatcommand.util.NameUtil;

import java.util.List;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin {
    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void middleclickchatcommand$handleMiddleClick(Click click, boolean doubleClick, CallbackInfoReturnable<Boolean> cir) {
        if (click.button() != 2) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.inGameHud == null || client.textRenderer == null) {
            return;
        }

        String name = extractName(client, click);
        if (!NameUtil.isValidMinecraftName(name)) {
            return;
        }

        String command = NameUtil.applyTemplate(ModConfig.data.commandTemplate, name).trim();
        if (command.isBlank()) {
            return;
        }

        if (command.startsWith("/")) {
            command = command.substring(1);
        }
        if (command.isBlank()) {
            return;
        }

        client.player.networkHandler.sendChatCommand(command);
        cir.setReturnValue(true);
    }

    private static String extractName(MinecraftClient client, Click click) {
        Style style = getHoveredStyle(client, click);
        String name = NameUtil.extractName(style);
        if (NameUtil.isValidMinecraftName(name)) {
            return name;
        }

        if (!ModConfig.data.fallbackWordDetection) {
            return null;
        }

        return extractNameFromVisibleChat(client, click);
    }

    private static Style getHoveredStyle(MinecraftClient client, Click click) {
        DrawnTextConsumer.ClickHandler clickHandler = new DrawnTextConsumer.ClickHandler(
                client.textRenderer,
                (int) click.x(),
                (int) click.y()
        ).insert(client.isShiftPressed());

        client.inGameHud.getChatHud().render(
                clickHandler,
                client.getWindow().getScaledHeight(),
                client.inGameHud.getTicks(),
                true
        );
        return clickHandler.getStyle();
    }

    private static String extractNameFromVisibleChat(MinecraftClient client, Click click) {
        ChatHud chatHud = client.inGameHud.getChatHud();
        ChatHudAccessor accessor = (ChatHudAccessor) chatHud;
        List<ChatHudLine.Visible> visibleMessages = accessor.middleclickchatcommand$getVisibleMessages();
        if (visibleMessages.isEmpty()) {
            return null;
        }

        double chatScale = accessor.middleclickchatcommand$invokeGetChatScale();
        if (chatScale <= 0.0D) {
            return null;
        }

        int bottomY = MathHelper.floor((client.getWindow().getScaledHeight() - 40) / chatScale);
        int lineHeight = accessor.middleclickchatcommand$invokeGetLineHeight();
        int visibleLineCount = accessor.middleclickchatcommand$invokeGetVisibleLineCount();

        double localX = click.x() / chatScale - 4.0D;
        double localY = click.y() / chatScale;
        if (localX < 0.0D || localY > bottomY || localY < bottomY - (double) (visibleLineCount * lineHeight)) {
            return null;
        }

        int line = MathHelper.floor((bottomY - localY) / lineHeight);
        if (line < 0 || line >= visibleLineCount) {
            return null;
        }

        int messageIndex = line + accessor.middleclickchatcommand$getScrolledLines();
        if (messageIndex < 0 || messageIndex >= visibleMessages.size()) {
            return null;
        }

        String text = NameUtil.toPlainString(visibleMessages.get(messageIndex).content());
        return NameUtil.extractNameAtX(text, localX, client.textRenderer);
    }
}

