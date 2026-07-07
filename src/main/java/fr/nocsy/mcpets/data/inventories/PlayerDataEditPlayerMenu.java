package fr.nocsy.mcpets.data.inventories;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.config.AbstractConfig;
import fr.nocsy.mcpets.data.sql.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * [1] playerdataeditgui のプレイヤー選択メニュー。
 * ペットのステータス(PetStats)を1つ以上持っているプレイヤーのみを一覧表示する。
 * PlayerData フォルダをスキャンするのでオフラインプレイヤーも対象になる。
 */
public class PlayerDataEditPlayerMenu {

    private static final int PAGE_SIZE = 45;

    public static void open(final Player admin, final int page) {
        admin.sendMessage("§7プレイヤーデータを読み込み中...");
        Bukkit.getScheduler().runTaskAsynchronously(MCPets.getInstance(), () -> {
            final List<UUID> owners = ownersWithPets(scanCandidateOwners());
            owners.sort(Comparator.comparing(u -> {
                final String n = Bukkit.getOfflinePlayer(u).getName();
                return n != null ? n : u.toString();
            }));

            Bukkit.getScheduler().runTask(MCPets.getInstance(), () -> {
                if (!admin.isOnline())
                    return;
                admin.openInventory(build(owners, Math.max(0, page)));
            });
        });
    }

    private static Set<UUID> scanCandidateOwners() {
        final Set<UUID> uuids = new LinkedHashSet<>();

        final File folder = new File(AbstractConfig.getPath() + "PlayerData/");
        final File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".yml"));
        if (files != null) {
            for (final File f : files) {
                final String base = f.getName().substring(0, f.getName().length() - 4);
                try {
                    uuids.add(UUID.fromString(base));
                } catch (final IllegalArgumentException ignored) {
                    // Not a UUID-named file, skip it
                }
            }
        }

        uuids.addAll(PlayerData.getRegisteredData().keySet());
        for (final Player p : Bukkit.getOnlinePlayers())
            uuids.add(p.getUniqueId());

        return uuids;
    }

    private static List<UUID> ownersWithPets(final Set<UUID> candidates) {
        final List<UUID> result = new ArrayList<>();
        for (final UUID uuid : candidates) {
            PlayerData.get(uuid);
            if (!PlayerDataEditPetMenu.collectEntries(uuid).isEmpty())
                result.add(uuid);
        }
        return result;
    }

    private static Inventory build(final List<UUID> owners, final int page) {
        final int total = owners.size();
        final int start = page * PAGE_SIZE;
        final boolean hasPrev = page > 0;
        final boolean hasNext = start + PAGE_SIZE < total;

        final Inventory inv = new PetInventoryHolder(54, "§8プレイヤー選択 §7(P" + (page + 1) + ")",
                PetInventoryHolder.Type.PLAYERDATAEDIT_PLAYERS).getInventory();

        for (int i = 0; i < PAGE_SIZE; i++) {
            final int idx = start + i;
            if (idx >= total)
                break;
            final UUID uuid = owners.get(idx);
            inv.setItem(i, playerItem(uuid));
        }

        if (hasPrev)
            inv.setItem(45, PlayerDataEditItems.previousPage("PDEG;PLAYERPAGE;" + (page - 1)));
        if (hasNext)
            inv.setItem(53, PlayerDataEditItems.nextPage("PDEG;PLAYERPAGE;" + (page + 1)));

        return inv;
    }

    private static org.bukkit.inventory.ItemStack playerItem(final UUID uuid) {
        final OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
        final String name = op.getName() != null ? op.getName() : uuid.toString();
        final int count = PlayerDataEditPetMenu.collectEntries(uuid).size();

        final List<String> lore = new ArrayList<>();
        lore.add("§7所持ペット数: §e" + count);
        lore.add(op.isOnline() ? "§aオンライン" : "§8オフライン");
        lore.add("");
        lore.add("§eクリックして選択");

        return PlayerDataEditItems.playerHead(op, "§b" + name, lore, "PDEG;PLAYERSELECT;" + uuid);
    }
}
