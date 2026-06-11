package fr.nocsy.mcpets.utils;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.utils.debug.Debugger;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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

    /** Cache Bedrock detection results per UUID to avoid repeated Floodgate API calls. */
    private static final Map<UUID, Boolean> cache = new ConcurrentHashMap<>();

    private GeyserUtils() {
    }

    /** Call on player quit to prevent the cache from growing unbounded. */
    public static void invalidate(final UUID uuid) {
        if (uuid != null)
            cache.remove(uuid);
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

        final Boolean cached = cache.get(uuid);
        if (cached != null)
            return cached;

        init();

        boolean result;
        if (floodgateApi != null && isFloodgatePlayerMethod != null) {
            try {
                final Object res = isFloodgatePlayerMethod.invoke(floodgateApi, uuid);
                if (res instanceof Boolean) {
                    result = (Boolean) res;
                    Debugger.send("[GeyserUtils] " + uuid + " → Floodgate API → isBedrockPlayer=" + result);
                    cache.put(uuid, result);
                    return result;
                }
            } catch (final Throwable ignored) {
                // Fall through to the heuristic below.
            }
        }

        // Floodgate-generated UUIDs have most significant 64 bits == 0.
        result = uuid.getMostSignificantBits() == 0L;
        Debugger.send("[GeyserUtils] " + uuid + " → UUID heuristic → isBedrockPlayer=" + result);
        cache.put(uuid, result);
        return result;
    }

    public static boolean isBedrockPlayer(final Player player) {
        return player != null && isBedrockPlayer(player.getUniqueId());
    }
}
