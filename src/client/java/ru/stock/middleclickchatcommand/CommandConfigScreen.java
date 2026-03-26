package ru.stock.middleclickchatcommand;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

public class CommandConfigScreen extends Screen {
    private static final Text TITLE = Text.literal("WheelAction");
    private static final Text COMMAND_LABEL = Text.literal("Command template");
    private static final Text COMMAND_HINT = Text.literal("Example: /duel {name}");
    private static final Text FALLBACK_LABEL = Text.literal("Fallback word detection");

    private final Screen parent;
    private TextFieldWidget commandField;
    private boolean fallbackWordDetection;

    protected CommandConfigScreen(Screen parent) {
        super(TITLE);
        this.parent = parent;
    }

    @Override
    protected void init() {
        ModConfig.load();
        this.fallbackWordDetection = ModConfig.data.fallbackWordDetection;

        int centerX = this.width / 2;
        int top = this.height / 4;

        this.commandField = new TextFieldWidget(this.textRenderer, centerX - 100, top + 24, 200, 20, COMMAND_LABEL);
        this.commandField.setMaxLength(256);
        this.commandField.setText(ModConfig.data.commandTemplate);
        this.commandField.setSuggestion(ModConfig.DEFAULT_COMMAND_TEMPLATE);
        this.addDrawableChild(this.commandField);
        this.setInitialFocus(this.commandField);

        this.addDrawableChild(ButtonWidget.builder(getFallbackButtonText(), button -> {
            this.fallbackWordDetection = !this.fallbackWordDetection;
            button.setMessage(getFallbackButtonText());
        }).dimensions(centerX - 100, top + 58, 200, 20).build());

        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> saveAndClose())
                .dimensions(centerX - 100, top + 104, 98, 20)
                .build());

        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, button -> close())
                .dimensions(centerX + 2, top + 104, 98, 20)
                .build());
    }

    @Override
    public void close() {
        if (this.client != null) {
            this.client.setScreen(this.parent);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, this.width, this.height, 0xB0101010);
        super.render(context, mouseX, mouseY, delta);

        int centerX = this.width / 2;
        int top = this.height / 4;
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, centerX, top - 20, 0xFFFFFF);
        context.drawTextWithShadow(this.textRenderer, COMMAND_LABEL, centerX - 100, top + 10, 0xA0A0A0);
        context.drawTextWithShadow(this.textRenderer, COMMAND_HINT, centerX - 100, top + 88, 0x808080);
    }

    private Text getFallbackButtonText() {
        return ScreenTexts.composeToggleText(FALLBACK_LABEL, this.fallbackWordDetection);
    }

    private void saveAndClose() {
        ModConfig.data.commandTemplate = this.commandField.getText();
        ModConfig.data.fallbackWordDetection = this.fallbackWordDetection;
        ModConfig.save();
        close();
    }
}
