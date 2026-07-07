package fr.nocsy.mcpets.data.inventories;

import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.livingpets.PetStats;
import fr.nocsy.mcpets.data.sql.PlayerData;
import fr.nocsy.mcpets.utils.PDCTag;
import fr.nocsy.mcpets.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * [2] playerdataeditgui のペット選択メニュー。
 * 一覧には以下の2種類のペットを表示する:
 *  - PetStats（レベル・経験値データ）を持っているペット
 *  - レベル定義自体が無く、PetStats を一切持てないが、召喚権限を持っているペット
 */
public class PlayerDataEditPetMenu {

    private static final int PAGE_SIZE = 45;

    record Entry(Pet pet, PetStats stats) {}

    public static void open(final Player admin, final UUID target, final int page) {
        PlayerData.get(target);
        admin.openInventory(build(target, collectEntries(target), Math.max(0, page)));
    }

    static List<Entry> collectEntries(final UUID target) {
        final List<Entry> entries = new ArrayList<>();
        final Set<String> seen = new HashSet<>();

        for (final PetStats st : PetStats.getPetStats(target)) {
            entries.add(new Entry(st.getPet(), st));
            seen.add(st.getPet().getId());
        }

        for (final Pet template : Pet.getObjectPets()) {
            if (seen.contains(template.getId()))
                continue;
            final boolean levelLess = template.getPetLevels() == null || template.getPetLevels().isEmpty();
            if (levelLess && Utils.hasPermission(target, template.getPermission())) {
                final Pet copy = template.copy();
                copy.setOwner(target);
                entries.add(new Entry(copy, null));
            }
        }

        entries.sort(Comparator.comparing(e -> e.pet().getId()));
        return entries;
    }

    private static Inventory build(final UUID target, final List<Entry> entries, final int page) {
        final int total = entries.size();
        final int start = page * PAGE_SIZE;
        final boolean hasPrev = page > 0;
        final boolean hasNext = start + PAGE_SIZE < total;

        final OfflinePlayer op = Bukkit.getOfflinePlayer(target);
        final String pname = op.getName() != null ? op.getName() : target.toString();

        final Inventory inv = new PetInventoryHolder(54, "§8" + pname + " のペット §7(P" + (page + 1) + ")",
                PetInventoryHolder.Type.PLAYERDATAEDIT_PETS).getInventory();

        for (int i = 0; i < PAGE_SIZE; i++) {
            final int idx = start + i;
            if (idx >= total)
                break;
            inv.setItem(i, petItem(target, entries.get(idx)));
        }

        if (hasPrev)
            inv.setItem(45, PlayerDataEditItems.previousPage("PDEG;PETPAGE;" + target + ";" + (page - 1)));
        inv.setItem(49, PlayerDataEditItems.button(Material.BARRIER, "§c« プレイヤー選択に戻る", null, "PDEG;PETBACK"));
        if (hasNext)
            inv.setItem(53, PlayerDataEditItems.nextPage("PDEG;PETPAGE;" + target + ";" + (page + 1)));

        return inv;
    }

    private static ItemStack petItem(final UUID target, final Entry entry) {
        final Pet pet = entry.pet();
        final PetStats st = entry.stats();
        final ItemStack icon = pet.getIcon() != null ? pet.getIcon().clone() : new ItemStack(Material.BONE);
        final ItemMeta meta = icon.getItemMeta();
        if (meta == null)
            return icon;

        meta.setDisplayName("§b" + pet.getId());

        final List<String> lore = new ArrayList<>();
        if (st != null) {
            lore.add("§7レベル: §e" + st.getCurrentLevelIndex() + " §7(" + st.getCurrentLevel().getLevelName() + ")");
            lore.add("§7経験値: §e" + PlayerDataEditItems.fmt(st.getExperience())
                    + " §7/ §e" + PlayerDataEditItems.fmt(st.getCurrentLevel().getExpThreshold()));
            lore.add("§7体力: §c" + PlayerDataEditItems.fmt(st.getCurrentHealth())
                    + " §7/ §c" + PlayerDataEditItems.fmt(st.getCurrentLevel().getMaxHealth()));
        } else {
            lore.add("§7レベル定義なし §8(権限のみ)");
        }
        lore.add("");
        lore.add("§eクリックして操作");
        meta.setLore(lore);

        PDCTag.set(meta, "PDEG;PETSELECT;" + target + ";" + pet.getId());
        icon.setItemMeta(meta);
        return icon;
    }
}
