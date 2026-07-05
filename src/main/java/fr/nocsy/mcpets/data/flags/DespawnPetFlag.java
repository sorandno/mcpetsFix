package fr.nocsy.mcpets.data.flags;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.PetDespawnReason;
import fr.nocsy.mcpets.data.config.Language;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class DespawnPetFlag extends AbstractFlag implements StoppableFlag {

    int task;

    public static String NAME = "mcpets-despawn";

    public DespawnPetFlag(final MCPets instance) {
        super(NAME, false, instance);
    }

    @Override
    public void register() {
        super.register();
    }

    @Override
    public void launch() {
        if (getFlag() == null) {
            MCPets.getLog().warning("Flag " + getFlagName() + " couldn't not be launched as it's null. Please contact Nocsy.");
            return;
        }
        else {
            MCPets.getLog().info("Starting flag " + getFlagName() + ".");
        }

        task = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(getMCPetsInstance(), () -> {
            if (MCPets.getMythicMobs() == null)
                return;

            // Snapshot the owner set: despawn() can remove an owner's entry entirely
            // once their last pet is gone, which would otherwise mutate this keySet
            // while we're iterating over it.
            final Set<UUID> owners = Set.copyOf(Pet.getActivePets().keySet());
            for (final UUID owner : owners) {
                // Snapshot the pet list too: despawn() removes the pet from the same
                // underlying list returned by getActivePetsForOwner().
                final List<Pet> pets = List.copyOf(Pet.getActivePetsForOwner(owner));
                for (final Pet pet : pets) {
                    final Player p = Bukkit.getPlayer(owner);

                    if (p != null) {
                        final boolean hasToBeRemoved = testState(p.getLocation());

                        if (hasToBeRemoved) {
                            pet.despawn(PetDespawnReason.FLAG);
                            Language.CANT_FOLLOW_HERE.sendMessage(p);
                        }
                    }
                }
            }
        }, 0L, 20L);
    }

    @Override
    public void stop() {
        Bukkit.getServer().getScheduler().cancelTask(task);
    }
}
