package fr.nocsy.mcpets.mmocore;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import net.Indyuce.mmocore.api.event.PlayerChangeClassEvent;
import net.Indyuce.mmocore.api.event.PlayerLevelChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * プレイヤーのMMOCoreレベル・クラスが変化した際に、召喚中のレベル無しペットのステータスを再計算する。
 * このリスナーはMMOCoreが導入されている場合にのみ登録される(EventListener.init()を参照)。
 */
public class MMOCoreListener implements Listener {

    @EventHandler
    public void onLevelChange(final PlayerLevelChangeEvent e) {
        refreshOwnerPetsNextTick(e.getPlayer());
    }

    @EventHandler
    public void onClassChange(final PlayerChangeClassEvent e) {
        refreshOwnerPetsNextTick(e.getPlayer());
    }

    /**
     * MMOCore側の内部ステータス再計算が同一イベント内で完了しているとは限らないため、
     * 1tick遅らせてから読み取ることで安全にMAX_HEALTH等の最新値を取得する。
     */
    private void refreshOwnerPetsNextTick(final Player player) {
        if (player == null)
            return;
        Bukkit.getScheduler().runTask(MCPets.getInstance(), () -> {
            for (final Pet pet : Pet.getActivePetsForOwner(player.getUniqueId())) {
                if (pet.getPetStats() != null)
                    pet.getPetStats().refreshDynamicLevel();
            }
        });
    }
}
