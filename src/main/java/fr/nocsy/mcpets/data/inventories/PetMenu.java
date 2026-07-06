package fr.nocsy.mcpets.data.inventories;

import fr.nocsy.mcpets.data.Category;
import fr.nocsy.mcpets.data.Items;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.config.GlobalConfig;
import fr.nocsy.mcpets.data.config.Language;
import fr.nocsy.mcpets.data.sql.PlayerData;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PetMenu {

    @Getter
    private static final String title = Language.INVENTORY_PETS_MENU.getMessage();

    @Getter
    private final Inventory inventory;

    @Getter
    private final UUID owner;

    public PetMenu(final Player p, final int page) {
        // Load the data from the player
        // Mainly for the pet stats
        PlayerData.get(p.getUniqueId());
        owner = p.getUniqueId();

        final List<Pet> availablePets = Pet.getAvailablePets(p);

        // Count the amount of pets that are being selected at that page
        // One page is up to 52 pets, so the page P has already seen 52 * P pets
        // 52 pets because we have to leave one spot for the previous page button
        // and one spot for the next page button
        final ArrayList<Pet> selectedPets = new ArrayList<>();
        // We need a previous page button as soon as we are past the first page
        boolean hasPrevious = page > 0;
        // We'll know we need a next page button if there are still pets left after this page
        boolean hasNext = false;
        int pageSize = 52;
        if (GlobalConfig.getInstance().getAdaptiveInventory() > 0) {
            pageSize = GlobalConfig.getInstance().getAdaptiveInventory() - 2;
        }
        for (int i = pageSize * page; i < availablePets.size(); i++)
        {
            // We can not have more than pageSize pets selected at a given page
            if(selectedPets.size() >= pageSize)
            {
                hasNext = true;
                break;
            }
            selectedPets.add(availablePets.get(i));
        }

        final boolean addPager = hasPrevious || hasNext;

        // We can now easily compute the inventory size in the adaptive case
        // by taking the amount of pets selected and adding the pager slots needed
        // then we round it up to the nearest multiple of 9
        int invSize = GlobalConfig.getInstance().getAdaptiveInventory();
        if (invSize <= 0) {
            invSize = selectedPets.size() + (addPager ? 2 : 0);
            while (invSize % 9 != 0) {
                invSize++;
            }
        }

        // Let's fill the view with the selected pets
        // Slot 0 is reserved for the previous page button whenever a pager is present
        this.inventory = new PetInventoryHolder(invSize, title, PetInventoryHolder.Type.PET_MENU).getInventory();
        int slot = addPager ? 1 : 0;
        for (final Pet pet : selectedPets) {
            inventory.setItem(slot++, pet.buildItem(pet.getIcon(), true, null, null, null, null, 0, null, null));
        }

        // Previous page button goes in the first slot, next page button in the last slot
        if (hasPrevious) {
            inventory.setItem(0, Items.pagePrevious(page, p));
        }
        if (hasNext) {
            inventory.setItem(invSize - 1, Items.pageNext(page, p));
        }
    }

    public void open(final Player p) {
        if (p.getUniqueId().equals(owner) && !Category.getCategories().isEmpty()) {
            CategoriesMenu.open(p);
            return;
        }
        p.openInventory(inventory);
    }
}
