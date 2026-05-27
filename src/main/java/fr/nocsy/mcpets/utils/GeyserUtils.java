package fr.nocsy.mcpets.utils;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.utils.debug.Debugger;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Detects players connected through Geyser/Floodgate (Bedrock Edition).
 * <p>
 * Detection priority:
 * 1. Floodgate API through reflection (no compile-time dependency).
 * 2. UUID format heuristic: Floodgate-generated UUIDs have the form
 *    00000000-0000-0000-xxxx-xxxxxxxxxxxx (most significant bits == 0).
 */
public class GeyserUtils {

    private static boolean initialized = false;
    private static Object floodgateApi;
    private static Method isFloodgatePlayerMethod;

    private GeyserUtils() {
    }

    private static synchronized void init() {
        if (initialized)
            return;
        initialized = true;

        // Use Class.forName instead of a plugin-name lookup so that different
        // Floodgate builds (floodgate, Floodgate-Spigot, etc.) are all detected.
        try {
            final Class<?> apiClass = Class.forName("org.geysermc.floodgate.api.FloodgateApi");
            final Object instance = apiClass.getMethod("getInstance").invoke(null);
            final Method method = apiClass.getMethod("isFloodgatePlayer", UUID.class);
            floodgateApi = instance;
            isFloodgatePlayerMethod = method;
            MCPets.getLog().info("[GeyserUtils] Floodgate API resolved - Bedrock players will be detected via FloodgateApi.");
        } catch (final Throwable t) {
            floodgateApi = null;
            isFloodgatePlayerMethod = null;
            MCPets.getLog().info("[GeyserUtils] Floodgate API not available (" + t.getClass().getSimpleName() + ": " + t.getMessage() + "). Falling back to UUID heuristic.");
        }
    }

    public static boolean isBedrockPlayer(final UUID uuid) {
        if (uuid == null)
            return false;

        init();

        if (floodgateApi != null && isFloodgatePlayerMethod != null) {
            try {
                final Object result = isFloodgatePlayerMethod.invoke(floodgateApi, uuid);
                if (result instanceof Boolean) {
                    boolean value = (Boolean) result;
                    Debugger.send("[GeyserUtils] " + uuid + " → Floodgate API → isBedrockPlayer=" + value);
                    return value;
                }
            } catch (final Throwable ignored) {
                // Fall through to the heuristic below.
            }
        }

        // Floodgate-generated UUIDs have most significant 64 bits == 0.
        boolean heuristic = uuid.getMostSignificantBits() == 0L;
        Debugger.send("[GeyserUtils] " + uuid + " → UUID heuristic → isBedrockPlayer=" + heuristic);
        return heuristic;
    }

    public static boolean isBedrockPlayer(final Player player) {
        return player != null && isBedrockPlayer(player.getUniqueId());
    }
}
