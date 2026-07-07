package fr.nocsy.mcpets.listeners;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.PetDespawnReason;
import fr.nocsy.mcpets.data.inventories.PetInventoryHolder;
import fr.nocsy.mcpets.data.inventories.PlayerDataEditActionMenu;
import fr.nocsy.mcpets.data.inventories.PlayerDataEditConfirmMenu;
import fr.nocsy.mcpets.data.inventories.PlayerDataEditItems;
import fr.nocsy.mcpets.data.inventories.PlayerDataEditLevelMenu;
import fr.nocsy.mcpets.data.inventories.PlayerDataEditPetMenu;
import fr.nocsy.mcpets.data.inventories.PlayerDataEditPlayerMenu;
import fr.nocsy.mcpets.data.inventories.PlayerDataEditStatusMenu;
import fr.nocsy.mcpets.data.livingpets.PetLevel;
import fr.nocsy.mcpets.data.livingpets.PetStats;
import fr.nocsy.mcpets.data.sql.PlayerData;
import fr.nocsy.mcpets.utils.PDCTag;
import fr.nocsy.mcpets.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

/**
 * playerdataeditgui の全メニューのクリックを処理する。
 * すべてのアイテムは PDCTag に "PDEG;<動作>;<パラメータ...>" 形式のタグを持つ。
 */
public class PlayerDataEditGuiListener implements Listener {

    private static final Set<PetInventoryHolder.Type> HANDLED = Set.of(
            PetInventoryHolder.Type.PLAYERDATAEDIT_PLAYERS,
            PetInventoryHolder.Type.PLAYERDATAEDIT_PETS,
            PetInventoryHolder.Type.PLAYERDATAEDIT_ACTIONS,
            PetInventoryHolder.Type.PLAYERDATAEDIT_LEVELS,
            PetInventoryHolder.Type.PLAYERDATAEDIT_STATUS,
            PetInventoryHolder.Type.PLAYERDATAEDIT_CONFIRM
    );

    @EventHandler
    public void click(final InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof final PetInventoryHolder holder))
            return;
        if (!HANDLED.contains(holder.getType()))
            return;
        if (!(e.getWhoClicked() instanceof final Player admin))
            return;

        e.setCancelled(true);

        if (e.getClick() != ClickType.LEFT)
            return;

        final ItemStack it = e.getCurrentItem();
        if (it == null || it.getType().isAir() || !it.hasItemMeta())
            return;

        final String tag = PDCTag.get(it.getItemMeta());
        if (tag == null || !tag.startsWith("PDEG;"))
            return;

        final String[] p = tag.split(";");

        try {
            switch (p[1]) {
                case "PLAYERSELECT" -> PlayerDataEditPetMenu.open(admin, UUID.fromString(p[2]), 0);
                case "PLAYERPAGE" -> PlayerDataEditPlayerMenu.open(admin, Integer.parseInt(p[2]));

                case "PETSELECT" -> PlayerDataEditActionMenu.open(admin, UUID.fromString(p[2]), p[3]);
                case "PETPAGE" -> PlayerDataEditPetMenu.open(admin, UUID.fromString(p[2]), Integer.parseInt(p[3]));
                case "PETBACK" -> PlayerDataEditPlayerMenu.open(admin, 0);

                case "ACTION" -> handleAction(admin, UUID.fromString(p[2]), p[3], p[4]);
                case "ACTIONBACK" -> PlayerDataEditPetMenu.open(admin, UUID.fromString(p[2]), 0);

                case "LEVELPAGE" -> PlayerDataEditLevelMenu.open(admin, UUID.fromString(p[2]), p[3], Integer.parseInt(p[4]));
                case "LEVELBACK" -> PlayerDataEditActionMenu.open(admin, UUID.fromString(p[2]), p[3]);
                case "LEVELSET" -> handleLevelSet(admin, UUID.fromString(p[2]), p[3], p[4]);

                case "STATUSBACK" -> PlayerDataEditActionMenu.open(admin, UUID.fromString(p[2]), p[3]);

                case "CONFIRMYES" -> handleConfirm(admin, UUID.fromString(p[2]), p[3], p[4]);
                case "CONFIRMNO" -> PlayerDataEditActionMenu.open(admin, UUID.fromString(p[2]), p[3]);

                default -> {}
            }
        } catch (final Exception ex) {
            admin.sendMessage("§cGUI操作中にエラーが発生しました。");
            MCPets.getLog().log(Level.WARNING, "playerdataeditgui error on tag [" + tag + "]", ex);
        }
    }

    private void handleAction(final Player admin, final UUID target, final String petId, final String action) {
        switch (action) {
            case "STATUS" -> PlayerDataEditStatusMenu.open(admin, target, petId);
            case "LEVEL" -> PlayerDataEditLevelMenu.open(admin, target, petId, 0);
            case "REVOKEPERM", "DELETEPET" -> PlayerDataEditConfirmMenu.open(admin, target, petId, action);
            default -> {}
        }
    }

    private void handleLevelSet(final Player admin, final UUID target, final String petId, final String levelId) {
        final PetStats st = PetStats.get(petId, target);
        if (st == null) {
            admin.sendMessage("§cペットデータが見つかりませんでした。");
            PlayerDataEditPetMenu.open(admin, target, 0);
            return;
        }

        final PetLevel level = st.getPet().getPetLevels().stream()
                .filter(l -> l.getLevelId().equals(levelId))
                .findFirst().orElse(null);
        if (level == null) {
            admin.sendMessage("§cレベルが見つかりませんでした。");
            PlayerDataEditActionMenu.open(admin, target, petId);
            return;
        }

        st.setStats(level.getExpThreshold(), level.getMaxHealth(), level);
        PlayerData.get(target).save();
        admin.sendMessage("§aペット §b" + petId + " §aのレベルを §e" + level.getLevelName() + " §aに設定しました。");
        PlayerDataEditActionMenu.open(admin, target, petId);
    }

    private void handleConfirm(final Player admin, final UUID target, final String petId, final String action) {
        final Pet pet = PlayerDataEditItems.resolvePet(target, petId);
        if (pet == null) {
            admin.sendMessage("§cペットデータが見つかりませんでした。");
            PlayerDataEditPetMenu.open(admin, target, 0);
            return;
        }

        // 召喚中のまま権限やステータスを消すと実体が残り続けるため、先に呼び戻す
        for (final Pet active : Pet.getActivePetsForOwner(target)) {
            if (active.getId().equals(petId)) {
                active.despawn(PetDespawnReason.REVOKE);
                break;
            }
        }

        Utils.removePermission(target, pet.getPermission());

        if (action.equals("DELETEPET")) {
            PetStats.remove(petId, target);
            PlayerData.get(target).save();
            admin.sendMessage("§aペット §b" + petId + " §aの権限とステータスを削除しました。");
            PlayerDataEditPetMenu.open(admin, target, 0);
        } else {
            admin.sendMessage("§aペット §b" + petId + " §aの権限を削除しました。");
            PlayerDataEditActionMenu.open(admin, target, petId);
        }
    }
}
