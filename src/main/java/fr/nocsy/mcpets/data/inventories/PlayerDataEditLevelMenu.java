package fr.nocsy.mcpets.data.inventories;

import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.livingpets.PetLevel;
import fr.nocsy.mcpets.data.livingpets.PetStats;
import fr.nocsy.mcpets.utils.PDCTag;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * [4] レベル選択メニュー。
 * pet.getPetLevels() を並べ、選んだレベルの必要経験値(expThreshold)にステータスを設定する。
 * 現在のレベルには光沢を付けて区別する。前後ページングと戻るボタンを提供する。
 */
public class PlayerDataEditLevelMenu {

    private static final int PAGE_SIZE = 45;

    public static void open(final Player admin, final UUID target, final String petId, final int page) {
        final PetStats st = PetStats.get(petId, target);
        if (st == null) {
            admin.sendMessage("§cペットデータが見つかりませんでした。");
            PlayerDataEditPetMenu.open(admin, target, 0);
            return;
        }

        final Pet pet = st.getPet();
        if (pet.getPetLevels() == null || pet.getPetLevels().isEmpty()) {
            admin.sendMessage("§cこのペットにはレベル定義がありません。");
            PlayerDataEditActionMenu.open(admin, target, petId);
            return;
        }

        admin.openInventory(build(target, petId, st, Math.max(0, page)));
    }

    private static Inventory build(final UUID target, final String petId, final PetStats st, final int page) {
        final Pet pet = st.getPet();
        final List<PetLevel> levels = pet.getPetLevels();
        final int total = levels.size();
        final int start = page * PAGE_SIZE;
        final boolean hasPrev = page > 0;
        final boolean hasNext = start + PAGE_SIZE < total;

        final Inventory inv = new PetInventoryHolder(54, "§8" + petId + " レベル選択 §7(P" + (page + 1) + ")",
                PetInventoryHolder.Type.PLAYERDATAEDIT_LEVELS).getInventory();

        for (int i = 0; i < PAGE_SIZE; i++) {
            final int idx = start + i;
            if (idx >= total)
                break;
            final PetLevel lvl = levels.get(idx);
            final boolean current = lvl.getLevelId().equals(st.getCurrentLevel().getLevelId());
            inv.setItem(i, levelItem(target, petId, idx, lvl, current));
        }

        if (hasPrev)
            inv.setItem(45, PlayerDataEditItems.previousPage("PDEG;LEVELPAGE;" + target + ";" + petId + ";" + (page - 1)));
        inv.setItem(49, PlayerDataEditItems.button(Material.BARRIER, "§c« 戻る（操作メニュー）", null,
                "PDEG;LEVELBACK;" + target + ";" + petId));
        if (hasNext)
            inv.setItem(53, PlayerDataEditItems.nextPage("PDEG;LEVELPAGE;" + target + ";" + petId + ";" + (page + 1)));

        return inv;
    }

    private static ItemStack levelItem(final UUID target, final String petId, final int index,
                                       final PetLevel lvl, final boolean current) {
        final ItemStack it = new ItemStack(Material.EXPERIENCE_BOTTLE);
        final ItemMeta meta = it.getItemMeta();
        if (meta == null)
            return it;

        final String name = lvl.getLevelName() != null ? lvl.getLevelName() : ("Lv." + (index + 1));
        meta.setDisplayName("§b" + name + " §7(Lv." + (index + 1) + ")");

        final List<String> lore = new ArrayList<>();
        lore.add("§7必要経験値: §e" + PlayerDataEditItems.fmt(lvl.getExpThreshold()));
        lore.add("§7最大体力: §c" + PlayerDataEditItems.fmt(lvl.getMaxHealth()));
        if (lvl.getEvolutionId() != null && !lvl.getEvolutionId().isEmpty())
            lore.add("§7進化先: §d" + lvl.getEvolutionId());
        lore.add("");
        if (current)
            lore.add("§a▶ 現在のレベル");
        else
            lore.add("§eクリックでこのレベルに設定");
        meta.setLore(lore);

        if (current) {
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        PDCTag.set(meta, "PDEG;LEVELSET;" + target + ";" + petId + ";" + lvl.getLevelId());
        it.setItemMeta(meta);
        return it;
    }
}
