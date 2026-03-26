package ru.stock.middleclickchatcommand.util;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;

public final class NameUtil {
    private NameUtil() {}

    public static boolean isValidMinecraftName(String s) {
        if (s == null) return false;
        String name = s.trim();
        if (name.length() < 3 || name.length() > 16) return false;
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            boolean ok = (c >= 'a' && c <= 'z')
                    || (c >= 'A' && c <= 'Z')
                    || (c >= '0' && c <= '9')
                    || c == '_';
            if (!ok) return false;
        }
        return true;
    }

    public static String applyTemplate(String template, String name) {
        String t = (template == null || template.isBlank()) ? "/duel {name}" : template;
        return t.replace("{name}", name)
                .replace("{player}", name)
                .replace("{nick}", name);
    }

    public static String extractName(Style style) {
        if (style == null) return null;

        String insertion = sanitizePotentialName(style.getInsertion());
        if (isValidMinecraftName(insertion)) {
            return insertion;
        }

        ClickEvent clickEvent = style.getClickEvent();
        if (clickEvent == null) {
            return null;
        }

        if (clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND
                || clickEvent.getAction() == ClickEvent.Action.SUGGEST_COMMAND) {
            return extractNameFromCommand(clickEvent.getValue());
        }

        return null;
    }

    public static String extractNameFromCommand(String command) {
        if (command == null) return null;

        String trimmed = command.trim();
        if (trimmed.isEmpty()) return null;

        String[] parts = trimmed.split("\\s+");
        int start = trimmed.startsWith("/") ? 1 : 0;
        for (int i = start; i < parts.length; i++) {
            String candidate = sanitizePotentialName(parts[i]);
            if (isValidMinecraftName(candidate)) {
                return candidate;
            }
        }

        String candidate = sanitizePotentialName(trimmed);
        return isValidMinecraftName(candidate) ? candidate : null;
    }

    public static String toPlainString(OrderedText text) {
        if (text == null) return "";

        StringBuilder builder = new StringBuilder();
        text.accept((index, style, codePoint) -> {
            builder.appendCodePoint(codePoint);
            return true;
        });
        return builder.toString();
    }

    public static String extractNameAtX(String text, double x, TextRenderer textRenderer) {
        if (text == null || text.isBlank() || textRenderer == null || x < 0.0D) {
            return null;
        }

        int index = getCharacterIndexAtX(text, x, textRenderer);
        if (index < 0) {
            return null;
        }

        return extractNameAroundIndex(text, index);
    }

    private static int getCharacterIndexAtX(String text, double x, TextRenderer textRenderer) {
        int width = 0;
        for (int i = 0; i < text.length(); i++) {
            width = textRenderer.getWidth(text.substring(0, i + 1));
            if (x < width) {
                return i;
            }
        }

        return x <= width ? Math.max(0, text.length() - 1) : -1;
    }

    private static String extractNameAroundIndex(String text, int index) {
        if (index < 0 || index >= text.length()) {
            return null;
        }

        if (!isNameChar(text.charAt(index))) {
            int right = index + 1;
            while (right < text.length() && !isNameChar(text.charAt(right))) {
                right++;
            }
            if (right < text.length()) {
                index = right;
            } else {
                int left = index - 1;
                while (left >= 0 && !isNameChar(text.charAt(left))) {
                    left--;
                }
                if (left < 0) {
                    return null;
                }
                index = left;
            }
        }

        int start = index;
        while (start > 0 && isNameChar(text.charAt(start - 1))) {
            start--;
        }

        int end = index + 1;
        while (end < text.length() && isNameChar(text.charAt(end))) {
            end++;
        }

        String candidate = text.substring(start, end);
        return isValidMinecraftName(candidate) ? candidate : null;
    }

    private static String sanitizePotentialName(String value) {
        if (value == null) return null;

        String trimmed = value.trim();
        if (trimmed.isEmpty()) return null;

        int start = 0;
        int end = trimmed.length();
        while (start < end && !isNameChar(trimmed.charAt(start))) {
            start++;
        }
        while (end > start && !isNameChar(trimmed.charAt(end - 1))) {
            end--;
        }

        return start >= end ? null : trimmed.substring(start, end);
    }

    private static boolean isNameChar(char c) {
        return (c >= 'a' && c <= 'z')
                || (c >= 'A' && c <= 'Z')
                || (c >= '0' && c <= '9')
                || c == '_';
    }
}

