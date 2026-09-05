package com.pikselhud.nameprotect;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class NameProtectManager {
    private static NameProtectMode mode = NameProtectMode.OFF;
    private static String replacement = "Player";
    private static String friendsRaw = "";
    private static boolean hideSelfSkin = true;

    private NameProtectManager() {}

    public static NameProtectMode getMode() { return mode; }
    public static void setMode(NameProtectMode value) { mode = value == null ? NameProtectMode.OFF : value; }
    public static String getReplacement() { return replacement; }
    public static void setReplacement(String value) { replacement = value == null ? "" : value; }
    public static String getFriendsRaw() { return friendsRaw; }
    public static void setFriendsRaw(String value) { friendsRaw = value == null ? "" : value; }
    public static boolean shouldHideSelfSkin() { return hideSelfSkin; }
    public static void setHideSelfSkin(boolean value) { hideSelfSkin = value; }

    public static Set<String> getFriends() {
        if (friendsRaw.isBlank()) return Collections.emptySet();
        Set<String> result = new LinkedHashSet<>();
        Arrays.stream(friendsRaw.split("[,;\\n\\r]+"))
                .map(String::trim).filter(s -> !s.isEmpty()).forEach(result::add);
        return result;
    }

    public static boolean isEnabled() { return mode != NameProtectMode.OFF && !replacement.isBlank(); }

    public static boolean targets(String playerName) {
        if (!isEnabled() || playerName == null) return false;
        MinecraftClient client = MinecraftClient.getInstance();
        return switch (mode) {
            case SELF -> client.player != null && playerName.equals(client.player.getGameProfile().name());
            case ALL -> true;
            case FRIENDS -> containsIgnoreCase(getFriends(), playerName);
            default -> false;
        };
    }

    public static boolean targets(PlayerEntity player) { return player != null && targets(player.getGameProfile().name()); }

    public static boolean isFriend(String playerName) {
        return containsIgnoreCase(getFriends(), playerName);
    }

    public static Text replacementText(PlayerListEntry entry) { return Text.literal(replacement); }
    public static Text replacementText(String original) { return replacement.isBlank() ? Text.literal(original == null ? "" : original) : Text.literal(replacement); }

    public static Text transformChat(Text input) {
        if (!isEnabled() || input == null) return input;
        String value = input.getString();
        if (value.isEmpty()) return input;
        String transformed = value;
        for (String name : targetNamesForChat()) {
            if (!name.isEmpty() && !name.equals(replacement)) transformed = transformed.replace(name, replacement);
        }
        return transformed.equals(value) ? input : Text.literal(transformed);
    }

    private static List<String> targetNamesForChat() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return Collections.emptyList();
        if (mode == NameProtectMode.SELF) return List.of(client.player.getGameProfile().name());
        if (mode == NameProtectMode.FRIENDS) return new ArrayList<>(getFriends());
        if (mode == NameProtectMode.ALL && client.getNetworkHandler() != null) {
            List<String> result = new ArrayList<>();
            for (PlayerListEntry entry : client.getNetworkHandler().getPlayerList()) result.add(entry.getProfile().name());
            return result;
        }
        return Collections.emptyList();
    }

    public static void syncFromConfig(com.pikselhud.config.PikselHudData data) {
        if (data == null || data.nameProtect == null) return;
        mode = data.nameProtect.mode == null ? NameProtectMode.OFF : data.nameProtect.mode;
        replacement = data.nameProtect.replacement == null ? "Player" : data.nameProtect.replacement;
        friendsRaw = data.nameProtect.friends == null ? "" : data.nameProtect.friends;
        hideSelfSkin = data.nameProtect.hideSelfSkin;
    }

    public static Text friendNameText(String original) {
        return Text.literal(isEnabled() ? replacement : original).formatted(Formatting.GREEN);
    }

    private static boolean containsIgnoreCase(Set<String> values, String value) {
        for (String candidate : values) if (candidate.equalsIgnoreCase(value)) return true;
        return false;
    }
}
