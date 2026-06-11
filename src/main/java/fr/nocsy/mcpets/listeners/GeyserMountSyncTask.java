package fr.nocsy.mcpets.listeners;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.utils.GeyserUtils;
import fr.nocsy.mcpets.utils.debug.Debugger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Keeps Bedrock (Geyser) riders attached to flying pets.
 * <p>
 * Geyser does not reliably relay a vehicle's vertical movement to the Bedrock
 * client, so a Bedrock rider on a flying pet can be visually left behind while
 * the pet ascends. This task detects the divergence and re-mounts the driver,
 * which re-sends the passenger relationship and re-seats the client.
 */
public class GeyserMountSyncTask {

    /**
     * Pets currently being re-synced (re-mounted). {@link fr.nocsy.mcpets.modeler.listeners.ModelEngineListeners}
     * checks this to skip despawn-on-dismount and permission re-checks that the
     * re-mount would otherwise trigger.
     */
    private static final Set<UUID> resyncingPets = ConcurrentHashMap.newKeySet();

    // Squared distance thresholds.
    // Flying mounts: re-mount to resync passenger packet (Geyser vertical desync).
    private static final double DESYNC_THRESHOLD_SQUARED = 9.0;
    // Ground mounts: if the player has drifted this far while still "in vehicle",
    // Geyser silently dropped the EntityDismountEvent — force-eject to clear stale state.
    private static final double STALE_GROUND_THRESHOLD_SQ = 25.0; // 5 blocks

    private int task = -1;

    public static boolean isResyncing(final UUID petUUID) {
        return petUUID != null && resyncingPets.contains(petUUID);
    }

    public static void addResyncing(final UUID petUUID) {
        if (petUUID != null)
            resyncingPets.add(petUUID);
    }

    public static void removeResyncing(final UUID petUUID) {
        if (petUUID != null)
            resyncingPets.remove(petUUID);
    }

    public void launch() {
        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(MCPets.getInstance(), this::tick, 0L, 5L);
    }

    public void stop() {
        if (task != -1) {
            Bukkit.getScheduler().cancelTask(task);
            task = -1;
        }
    }

    private void tick() {
        if (MCPets.getMythicMobs() == null)
            return;

        for (final UUID owner : Pet.getActivePets().keySet()) {
            if (!GeyserUtils.isBedrockPlayer(owner))
                continue;

            final Player p = Bukkit.getPlayer(owner);
            if (p == null)
                continue;

            for (final Pet pet : Pet.getActivePetsForOwner(owner)) {
                if (!pet.isMountable())
                    continue;
                if (!MCPets.getModeler().isFlyingMount(pet, owner))
                    continue;
                if (!pet.hasMount(p))
                    continue;
                if (pet.getActiveMob() == null || pet.getActiveMob().getEntity() == null)
                    continue;

                final Entity petEntity = pet.getActiveMob().getEntity().getBukkitEntity();
                if (petEntity == null || !petEntity.getWorld().equals(p.getWorld()))
                    continue;

                final Location petLoc = petEntity.getLocation();
                if (p.getLocation().distanceSquared(petLoc) <= DESYNC_THRESHOLD_SQUARED)
                    continue;

                resync(pet, p);
            }
        }
    }

    private void resync(final Pet pet, final Player p) {
        final UUID petUUID = pet.getActiveMob().getEntity().getUniqueId();

        resyncingPets.add(petUUID);
        MCPets.getModeler().mountDriver(petUUID, p, pet.getMountType());
        Debugger.send("§e[Geyser] Re-synced flying mount for " + p.getName() + " on pet " + pet.getId());

        // despawnOnDismount runs on the next tick (runTask), so keep the guard set a
        // little longer before releasing it.
        new BukkitRunnable() {
            @Override
            public void run() {
                resyncingPets.remove(petUUID);
            }
        }.runTaskLater(MCPets.getInstance(), 2L);
    }
}
