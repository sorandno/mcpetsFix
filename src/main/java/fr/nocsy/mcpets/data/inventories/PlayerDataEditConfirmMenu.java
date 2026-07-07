package fr.nocsy.mcpets.data.inventories;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * [確認] ペット権限削除・ペット削除の実行前に必ず経由させる確認画面。
 * 誤操作防止のため、はい/いいえの二択のみを提供する。
 */
public class PlayerDataEditConfirmMenu {

    public static void open(final Player admin, final UUID target, final String petId, final String action) {
        admin.openInventory(build(target, petId, action));
    }

    private static Inventory build(final UUID target, final String petId, final String action) {
        final boolean isDelete = action.equals("DELETEPET");
        final String actionLabel = isDelete ? "§4ペット削除" : "§cペット権限削除";

        final Inventory inv = new PetInventoryHolder(27, "§8確認: " + actionLabel,
                PetInventoryHolder.Type.PLAYERDATAEDIT_CONFIRM).getInventory();

        final List<String> warnLore = new ArrayList<>();
        warnLore.add("§7対象ペット: §b" + petId);
        if (isDelete)
            warnLore.add("§7権限とステータスの両方を削除します。");
        else
            warnLore.add("§7召喚権限のみを削除します。");
        warnLore.add("§cこの操作は取り消せません。");

        inv.setItem(13, PlayerDataEditItems.button(Material.PAPER, actionLabel + " §7を実行しますか？", warnLore, null));

        final String tail = ";" + target + ";" + petId + ";" + action;
        inv.setItem(11, PlayerDataEditItems.button(Material.LIME_WOOL, "§aはい、実行する", null, "PDEG;CONFIRMYES" + tail));
        inv.setItem(15, PlayerDataEditItems.button(Material.RED_WOOL, "§cいいえ、戻る", null, "PDEG;CONFIRMNO" + tail));

        return inv;
    }
}
