package fr.nocsy.mcpets.data.inventories;

import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.livingpets.PetStats;
import fr.nocsy.mcpets.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * [ステータス確認] 画面。読み取り専用でペットの現在の状態を表示する。
 * レベル定義が無く PetStats を持てないペットの場合は、権限情報のみを表示する。
 */
public class PlayerDataEditStatusMenu {

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
        final PetStats st = PetStats.get(pet.getId(), target);
        final OfflinePlayer op = Bukkit.getOfflinePlayer(target);
        final String pname = op.getName() != null ? op.getName() : target.toString();

        final Inventory inv = new PetInventoryHolder(27, "§8" + pet.getId() + " のステータス",
                PetInventoryHolder.Type.PLAYERDATAEDIT_STATUS).getInventory();

        final List<String> lore = new ArrayList<>();
        lore.add("§7所有者: §b" + pname);
        lore.add("§7ペットID: §b" + pet.getId());
        if (st != null) {
            lore.add("§7レベル: §e" + st.getCurrentLevelIndex() + " §7(" + st.getCurrentLevel().getLevelName() + ")");
            lore.add("§7経験値: §e" + PlayerDataEditItems.fmt(st.getExperience())
                    + " §7/ §e" + PlayerDataEditItems.fmt(st.getCurrentLevel().getExpThreshold()));
            lore.add("§7体力: §c" + PlayerDataEditItems.fmt(st.getCurrentHealth())
                    + " §7/ §c" + PlayerDataEditItems.fmt(st.getCurrentLevel().getMaxHealth()));
            lore.add("§7ダメージ倍率: §6" + PlayerDataEditItems.fmt(st.getDamageModifier()));
            lore.add("§7耐性倍率: §6" + PlayerDataEditItems.fmt(st.getResistanceModifier()));
        } else {
            lore.add("§7レベル定義なし §8(権限のみで管理されるペット)");
        }
        lore.add("§7権限: §b" + pet.getPermission());
        lore.add("§7権限所持: " + (Utils.hasPermission(target, pet.getPermission()) ? "§aあり" : "§cなし"));

        final ItemStack icon = pet.getIcon() != null ? pet.getIcon().clone() : new ItemStack(Material.PAPER);
        final ItemMeta meta = icon.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§b" + pet.getId());
            meta.setLore(lore);
            icon.setItemMeta(meta);
        }
        inv.setItem(13, icon);
        inv.setItem(22, PlayerDataEditItems.button(Material.ARROW, "§c« 戻る", null,
                "PDEG;STATUSBACK;" + target + ";" + pet.getId()));

        return inv;
    }
}
