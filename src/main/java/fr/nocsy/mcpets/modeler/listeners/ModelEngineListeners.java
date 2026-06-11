package fr.nocsy.mcpets.modeler.listeners;

import com.ticxo.modelengine.api.events.ModelDismountEvent;
import com.ticxo.modelengine.api.events.ModelMountEvent;
import fr.nocsy.mcpets.PPermission;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.PetDespawnReason;
import fr.nocsy.mcpets.data.config.GlobalConfig;
import fr.nocsy.mcpets.data.config.Language;
import fr.nocsy.mcpets.data.flags.DismountPetFlag;
import fr.nocsy.mcpets.data.flags.FlagsManager;
import fr.nocsy.mcpets.listeners.GeyserMountSyncTask;
import fr.nocsy.mcpets.utils.debug.Debugger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import fr.nocsy.mcpets.MCPets;

import java.util.UUID;

public class ModelEngineListeners implements Listener {

    @EventHandler
    public void despawnOnDismount(ModelDismountEvent e) {
        if (e.getVehicle() == null || e.getVehicle().getModeledEntity() == null || e.getVehicle().getModeledEntity().getBase() == null)
            return;

        UUID baseUUID = e.getVehicle().getModeledEntity().getBase().getUUID();

        new BukkitRunnable() {
            @Override
            public void run() {
                // A Geyser re-sync re-mount briefly dismounts the driver; don't despawn the pet for it.
                if (GeyserMountSyncTask.isResyncing(baseUUID))
                    return;
                Pet pet = Pet.getFromEntity(Bukkit.getEntity(baseUUID));
                if (pet != null && pet.isDespawnOnDismount()) {
                    Debugger.send("[ModelDismountEvent] despawning pet " + pet.getId());
                    pet.despawn(PetDespawnReason.DISMOUNT);
                }
            }
        }.runTask(MCPets.getInstance());
    }

    @EventHandler
    public void mountingPet(ModelMountEvent e) {
        if (e.getPassenger() == null)
            return;

        if (e.getVehicle() == null || e.getVehicle().getModeledEntity() == null || e.getVehicle().getModeledEntity().getBase() == null)
            return;

        // Bukkit.getEntity() must be called on the main thread.
        // If this event is fired from an async thread (e.g. MythicMobs scheduler),
        // schedule the logic to run on the main thread instead.
        if (!Bukkit.isPrimaryThread()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    processMountingPet(e);
                }
            }.runTask(MCPets.getInstance());
            return;
        }

        processMountingPet(e);
    }

    private void processMountingPet(ModelMountEvent e) {
        UUID baseUUID = e.getVehicle().getModeledEntity().getBase().getUUID();
        // A Geyser re-sync re-mount must not re-run permission/region checks (which
        // could cancel the re-mount); it was already validated at the initial mount.
        if (GeyserMountSyncTask.isResyncing(baseUUID))
            return;

        Entity entity;
        try {
            entity = Bukkit.getEntity(baseUUID);
        } catch (Exception ex) {
            entity = null;
        }

        if (entity == null)
            return;
        Pet pet = Pet.getFromEntity(entity);
        Entity player = e.getPassenger();

        if (pet == null)
            return;

        // if it's not the owner or an admin mounting the pet, then we cancel it
        if (e.getSeat().isDriver() &&
                !pet.getOwner().equals(player.getUniqueId()) &&
                !player.hasPermission(PPermission.ADMIN.getPermission())) {
            e.setCancelled(true);
            Debugger.send("[ModelMountEvent] §c" + player.getName() + " can not mount model of " + pet.getId() + " as he's not the owner, nor an admin.");
        }

        if (GlobalConfig.getInstance().isWorldguardsupport() &&
                FlagsManager.getFlag(DismountPetFlag.NAME) != null &&
                FlagsManager.getFlag(DismountPetFlag.NAME).testState(player.getLocation())) {
            e.setCancelled(true);
            Debugger.send("§c" + player.getName() + " can not mount model of " + pet.getId() + " as a region is preventing mounting.");
            Language.NOT_MOUNTABLE_HERE.sendMessage(player);
            if (pet.isDespawnOnDismount())
                pet.despawn(PetDespawnReason.FLAG);
            return;
        }

        // If user doesn't have the perm to mount the pet, cancel the event
        if (pet.getMountPermission() != null
                && !player.hasPermission(pet.getMountPermission())
                && e.getSeat().isDriver()) {
            e.setCancelled(true);
            Language.CANT_MOUNT_PET_YET.sendMessage(player);
        }
    }
}
