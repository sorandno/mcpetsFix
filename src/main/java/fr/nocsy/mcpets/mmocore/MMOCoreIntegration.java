package fr.nocsy.mcpets.mmocore;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.config.GlobalConfig;
import fr.nocsy.mcpets.data.livingpets.PetLevel;
import net.Indyuce.mmocore.api.player.stats.StatType;
import org.bukkit.entity.Player;

/**
 * レベル定義を持たない「生きているペット」のステータスをMMOCoreのプレイヤーデータから算出する唯一の場所。
 * MMOCoreのAPIに直接触れるのはこのクラスのみとし、PetLevel/PetStatsをMMOCoreから疎結合に保つ。
 */
public class MMOCoreIntegration {

    /**
     * MMOCoreのプレイヤーデータを基に、指定されたレベルのステータスを再計算して適用する。
     * MMOCoreが導入されていない、またはプレイヤーがまだMMOCoreデータを読み込んでいない場合は何もしない
     * (levelの既存の値がそのまま維持される)。
     */
    public static void applyStats(final PetLevel level, final Player player) {
        if (level == null || player == null || !MCPets.isMMOCoreLoaded())
            return;

        if (!net.Indyuce.mmocore.api.player.PlayerData.has(player))
            return;

        final net.Indyuce.mmocore.api.player.PlayerData data = net.Indyuce.mmocore.api.player.PlayerData.get(player);
        final int classLevel = data.getLevel();
        final GlobalConfig config = GlobalConfig.getInstance();

        level.setMaxHealth(data.getStats().getStat(StatType.MAX_HEALTH.name()) * config.getMmoCoreMaxHealthMultiplier());
        level.setRegeneration(classLevel * config.getMmoCoreRegenerationPerLevel());
        level.setResistanceModifier(classLevel * config.getMmoCoreResistanceModifierPerLevel());
        level.setDamageModifier(classLevel * config.getMmoCoreDamageModifierPerLevel());
        level.setPower(classLevel * config.getMmoCorePowerPerLevel());
        level.setRespawnCooldown((int) Math.round(classLevel * config.getMmoCoreRespawnCooldownPerLevel()));
        level.setRevokeCooldown(config.getMmoCoreRevokeCooldown());
    }
}
