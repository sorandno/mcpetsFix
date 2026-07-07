package fr.nocsy.mcpets.data.inventories;

import fr.nocsy.mcpets.data.Pet;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.UUID;

/**
 * [3] playerdataeditgui の操作メニュー。
 * ステータス確認・レベル変更・ペット権限削除・ペット削除・戻る、を提供する。
 * レベル定義が無いペット（PetStatsを持てない）の場合はレベル変更ボタンを出さない。
 */
public class PlayerDataEditActionMenu {

    public static void open(final Player admin, final UUID target, final String petId) {
        final Pet pet = PlayerDataEditItems.resolvePet(target, petId);
        if (pet == null) {
            admin.sendMessage("§cペットデータが見つかりませんでした。");
            PlayerDataEditPetMenu.open(admin, target, 0);
            return;
        }
        admin.openInventory(build(target, pet));
    }

    private static Inventory build(final UUID target, final Pet pet) {
        final Inventory inv = new PetInventoryHolder(27, "§8" + pet.getId() + " の操作",
                PetInventoryHolder.Type.PLAYERDATAEDIT_ACTIONS).getInventory();

        final String base = "PDEG;ACTION;" + target + ";" + pet.getId() + ";";
        final boolean hasLevels = pet.getPetLevels() != null && !pet.getPetLevels().isEmpty();

        inv.setItem(10, PlayerDataEditItems.button(Material.PAPER, "§bステータス確認",
                List.of("§7クリックしてステータスを表示"), base + "STATUS"));
        if (hasLevels) {
            inv.setItem(12, PlayerDataEditItems.button(Material.EXPERIENCE_BOTTLE, "§bレベル変更",
                    List.of("§7クリックしてレベルを選択・変更"), base + "LEVEL"));
        }
        inv.setItem(14, PlayerDataEditItems.button(Material.REDSTONE, "§cペット権限削除",
                List.of("§7ペットの召喚権限のみを削除", "§c※確認画面が表示されます"), base + "REVOKEPERM"));
        inv.setItem(16, PlayerDataEditItems.button(Material.BARRIER, "§4ペット削除",
                List.of("§7権限とステータスを完全に削除", "§c※確認画面が表示されます"), base + "DELETEPET"));
        inv.setItem(22, PlayerDataEditItems.button(Material.ARROW, "§c« 戻る", null, "PDEG;ACTIONBACK;" + target));

        return inv;
    }
}
