package fr.nocsy.mcpets.data.inventories;

import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.livingpets.PetStats;
import fr.nocsy.mcpets.utils.PDCTag;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.UUID;

/**
 * playerdataeditgui コマンド用の共有アイテム生成ヘルパー。
 */
public final class PlayerDataEditItems {

    private PlayerDataEditItems() {}

    static ItemStack button(final Material mat, final String name, final List<String> lore, final String tag) {
        final ItemStack it = new ItemStack(mat);
        final ItemMeta meta = it.getItemMeta();
        if (meta == null)
            return it;
        if (name != null)
            meta.setDisplayName(name);
        if (lore != null)
            meta.setLore(lore);
        if (tag != null)
            PDCTag.set(meta, tag);
        it.setItemMeta(meta);
        return it;
    }

    static ItemStack playerHead(final OfflinePlayer op, final String displayName, final List<String> lore, final String tag) {
        final ItemStack it = new ItemStack(Material.PLAYER_HEAD);
        final ItemMeta meta = it.getItemMeta();
        if (meta instanceof final SkullMeta skull) {
            skull.setOwningPlayer(op);
            skull.setDisplayName(displayName);
            if (lore != null)
                skull.setLore(lore);
            if (tag != null)
                PDCTag.set(skull, tag);
            it.setItemMeta(skull);
        }
        return it;
    }

    static ItemStack previousPage(final String tag) {
        return button(Material.ARROW, "§a« 前のページ", null, tag);
    }

    static ItemStack nextPage(final String tag) {
        return button(Material.ARROW, "§a次のページ »", null, tag);
    }

    static String fmt(final double v) {
        if (!Double.isInfinite(v) && v == Math.floor(v))
            return String.valueOf((long) v);
        return String.format("%.2f", v);
    }

    /**
     * PetStats（レベル・経験値データ）があればそのペットを、無ければ ID からペットの
     * テンプレートを引いて owner を設定して返す。レベル定義自体を持たないペットは
     * PetStats を一切持てないため、この経路が唯一の解決手段になる。
     */
    public static Pet resolvePet(final UUID target, final String petId) {
        final PetStats st = PetStats.get(petId, target);
        if (st != null)
            return st.getPet();

        final Pet template = Pet.getFromId(petId);
        if (template == null)
            return null;
        template.setOwner(target);
        return template;
    }
}
