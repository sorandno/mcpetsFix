package fr.nocsy.mcpets.data.livingpets;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.config.GlobalConfig;
import fr.nocsy.mcpets.data.serializer.PetStatsSerializer;
import fr.nocsy.mcpets.data.sql.Databases;
import fr.nocsy.mcpets.data.sql.PlayerData;
import fr.nocsy.mcpets.events.PetGainExperienceEvent;
import fr.nocsy.mcpets.mmocore.MMOCoreIntegration;
import fr.nocsy.mcpets.utils.PetTimer;
import fr.nocsy.mcpets.utils.Utils;
import fr.nocsy.mcpets.utils.debug.Debugger;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class PetStats {

    //------------ オブジェクトコード -------------//

    @Getter
    @Setter
    // 実際のペットへの参照
    private Pet pet;

    @Getter
    // ペットの体力を管理する
    private double currentHealth;
    @Getter
    private PetTimer regenerationTimer;

    @Getter
    // ペットの経験値を管理する
    private double experience;

    @Getter
    // レベルを管理する
    private PetLevel currentLevel;

    @Getter
    // ペットが死亡後に再召喚できるまでの時間
    // 秒単位
    // -1 は永久死亡を示す
    private PetTimer respawnTimer;

    @Getter
    // ペットが呼び戻し後に再召喚できるまでの時間
    // 秒単位
    // -1 はペット削除を示す
    private PetTimer revokeTimer;

    // ファイル初期化時にタイマーが実行されないようにするための変数
    private boolean initializingRun = true;

    /**
     * 基本パラメータを設定し、各ペットスタットスケジューラを起動する
     */
    public PetStats(Pet pet,
                    double experience,
                    double currentHealth,
                    PetLevel currentLevel) {
        this.pet = pet;
        this.experience = experience;
        this.currentHealth = currentHealth;
        this.currentLevel = currentLevel;

        updateChangingData();
        launchRegenerationTimer();
    }

    /**
     * スタット用にペットの体力を更新する
     */
    public void updateHealth() {
        if (pet.isStillHere()) {
            this.currentHealth = pet.getActiveMob().getEntity().getHealth();
        }
    }

    /**
     * レベルによって変更されるデータを更新する
     */
    private void updateChangingData() {
        refreshMaxHealth();
        updateHealth();
        respawnTimer = new PetTimer(currentLevel.getRespawnCooldown(), 20, () -> {
            // 初期化実行時はリスポーンを行わない
            if (initializingRun) {
                initializingRun = false;
                return;
            }
            // それ以外の場合、自動リスポーンが可能か確認する
            if (GlobalConfig.getInstance().isAutoRespawn()) {
                Player p = Bukkit.getPlayer(pet.getOwner());
                if (p != null && Pet.getActivePets().get(pet.getOwner()) == null) {
                    pet.spawn(p.getLocation(), true);
                    Debugger.send("§aペット §6" + pet.getId() + "§a が死亡後に自動リスポーンしました。");
                }
                else {
                    Debugger.send("§cペット §6" + pet.getId() + "§c は自動リスポーン予定でしたが、プレイヤーが既に別のペットを召喚中か、切断されています。");
                }
            }
        });
        revokeTimer = new PetTimer(currentLevel.getRevokeCooldown(), 20, null);
    }

    /**
     * 初期起動時にnullでなければタイマーを起動する
     */
    public void launchTimers() {
        launchRespawnTimer();
    }

    /**
     * ペットの再生量を制御するタイマーを起動する（複数回起動不可）
     */
    public void launchRegenerationTimer() {
        // 再生タイマーが既に動作中の場合は再実行しない
        if (regenerationTimer != null && regenerationTimer.isRunning())
            return;
        // 再生量が0以下の場合はスケジューラを起動しない
        if (currentLevel.getRegeneration() <= 0)
            return;
        regenerationTimer = new PetTimer(Integer.MAX_VALUE, 20, null);
        regenerationTimer.launch(() -> {
            if (pet.isStillHere()) {
                double value = Math.min(currentHealth + currentLevel.getRegeneration(), currentLevel.getMaxHealth());
                pet.getActiveMob().getEntity().setHealth(value);
                updateHealth();
            }
            else {
                regenerationTimer.stop(null);
            }
        });
    }

    /**
     * リスポーンタイマーを起動する
     */
    public void launchRespawnTimer() {
        if (respawnTimer != null)
            respawnTimer.launch(() -> {
                if (!isDead()) {
                    respawnTimer.stop(null);
                }
            });
    }

    /**
     * 呼び戻しタイマーを起動する
     */
    public void launchRevokeTimer() {
        if (revokeTimer != null)
            revokeTimer.launch(null);
    }

    /**
     * 保存された体力に基づいてペットが死亡しているか判定する
     * 未召喚時やスポーン時にスタットが適用されていない場合に有用
     */
    public boolean isDead() {
        return currentHealth <= 0;
    }

    /**
     * リスポーンタイマーが動作中かどうかを返す
     */
    public boolean isRespawnTimerRunning() {
        return respawnTimer != null && respawnTimer.isRunning();
    }

    /**
     * 呼び戻しタイマーが動作中かどうかを返す
     */
    public boolean isRevokeTimerRunning() {
        return revokeTimer != null && revokeTimer.isRunning();
    }

    /**
     * レベル定義を持たないペット(MMOCore駆動)専用: オーナーの現在のMMOCoreデータから
     * MaxHealth/Regeneration/ResistanceModifier/DamageModifier/Power/Cooldownsを再計算して適用する。
     * レベル定義を持つペットには影響しない。
     */
    public void refreshDynamicLevel() {
        if (pet.getPetLevels() != null && !pet.getPetLevels().isEmpty())
            return;

        MMOCoreIntegration.applyStats(currentLevel, Bukkit.getPlayer(pet.getOwner()));
        updateChangingData();
    }

    /**
     * ペットの最大体力を設定値にリセットする
     */
    public void refreshMaxHealth() {
        if (pet.isStillHere())
            pet.getActiveMob().getEntity().setMaxHealth(currentLevel.getMaxHealth());
    }

    /**
     * 体力を指定値に設定する
     */
    public void setHealth(double value) {
        if (value >= currentLevel.getMaxHealth())
            value = currentLevel.getMaxHealth();
        if (pet.isStillHere()) {
            pet.getActiveMob().getEntity().setHealth(value);
            currentHealth = value;
        }
    }

    /**
     * ペットを死亡状態に設定する
     */
    public void setDead() {
        setHealth(0);
        currentHealth = 0;
    }

    /**
     * リスポーン時の体力値（最小1%、最大100%）
     */
    public double getRespawnHealth()
    {
        double coef = Math.min(1, Math.max(0.01, GlobalConfig.getInstance().getPercentHealthOnRespawn()));
        return coef * currentLevel.getMaxHealth();
    }

    /**
     * 指定モディファイアを適用したバフ後の値を返す
     */
    private double getBuffedModifier(double originalValue, PetFoodType modifier) {
        double value = originalValue;

        for (PetFoodBuff buff : PetFoodBuff.getBuffs(pet)) {
            if (buff.getType() == modifier) {
                value = buff.getOperator().get(value, buff.getPower());
            }
        }
        return value;
    }

    public double getDamageModifier() {
        return getBuffedModifier(getCurrentLevel().getFlatDamageModifier(), PetFoodType.BUFF_DAMAGE);
    }

    public double getResistanceModifier() {
        double value = getBuffedModifier(getCurrentLevel().getFlatResistanceModifier(), PetFoodType.BUFF_RESISTANCE);
        if (value == 0)
            return value = 10E-5;
        return value;
    }

    public double getPower() {
        return getBuffedModifier(getCurrentLevel().getFlatPower(), PetFoodType.BUFF_POWER);
    }

    /**
     * 拡張インベントリサイズを返す（基本サイズ＋レベルボーナス）
     */
    public int getExtendedInventorySize() {
        return Math.min(pet.getDefaultInventorySize() + currentLevel.getInventoryExtension(), 54);
    }

    /**
     * ペットに指定量の経験値を追加する
     */
    public boolean addExperience(double value) {
        // 最大レベルに達している場合は経験値不要
        if (currentLevel.equals(pet.getPetLevels().get(pet.getPetLevels().size()-1)))
            return false;
        // オーナーがいない場合は経験値取得不可
        if (pet.getOwner() == null)
            return false;

        PetGainExperienceEvent event = new PetGainExperienceEvent(pet, value);
        Utils.callEvent(event);
        if (event.isCancelled())
            return false;

        // 経験値をペットに追加
        experience = experience + event.getExperience();
        Debugger.send("§7ペット " + pet.getId() + " に " + experience + "xp を追加");

        // レベルアップが必要か確認
        PetLevel nextLevel = getNextLevel();
        boolean levelUp = false;
        while(!nextLevel.equals(currentLevel) && nextLevel.getExpThreshold() <= experience) {
            if (nextLevel.getEvolutionId() != null && !nextLevel.canEvolve(pet.getOwner(), Pet.getFromId(nextLevel.getEvolutionId()))) {
                Debugger.send("ペット §6" + pet.getId() + "§7 は §a" + nextLevel.getEvolutionId() + "§7 に進化できません（プレイヤーが既に進化先を所持）。");
                if (experience == nextLevel.getExpThreshold()-1 + event.getExperience()) {
                    experience = nextLevel.getExpThreshold() - 1;
                    return false;
                }
                else {
                    experience = nextLevel.getExpThreshold()-1;
                    break;
                }
            }
            Debugger.send("§aペット §7" + pet.getId() + "§a が §6" + nextLevel.getLevelName() + " §aにレベルアップしました");
            // レベルアップが発生したことを記録
            levelUp = true;
            // レベルアップ時のスキル・アニメーション等を実行（currentLevel更新前に実行してoldLevelを取得可能にする）
            nextLevel.levelUp(pet.getOwner(), currentLevel);
            // 現在のレベルを次のレベルに更新
            currentLevel = nextLevel;
            // ループを継続
            nextLevel = getNextLevel();
        }

        // レベルアップが発生した場合、保存と実際のペット更新を行う
        if (levelUp) {
            updateChangingData();
        }

        // 次のレベルがない場合、経験値を上限値に設定
        if (getNextLevel().equals(currentLevel) && experience > currentLevel.getExpThreshold()) {
            Debugger.send("§7ペット " + pet.getId() + " は §cレベルアップしません§7（§c最大レベル§7 到達済み、または §c進化先を既に所持§7）。");
            experience = currentLevel.getExpThreshold();
        }

        return true;
    }

    public PetLevel getNextLevel() {
        if (currentLevel == null)
            return null;

        return pet.getPetLevels().stream()
                                    .filter(petLevel -> petLevel.getExpThreshold() > currentLevel.getExpThreshold())
                                    .findFirst().orElse(currentLevel);
    }

    /**
     * スタットのダメージモディファイアに基づいて攻撃ダメージを補正して返す
     */
    public double getModifiedAttackDamages(double value) {
        return value * getDamageModifier();
    }

    /**
     * スタットの耐性モディファイアに基づいて耐性ダメージを補正して返す
     */
    public double getModifiedResistanceDamages(double value) {
        if (getResistanceModifier() == 0)
            return Integer.MAX_VALUE;
        return value / getResistanceModifier();
    }

    /**
     * ペットスタットを文字列にシリアライズする
     */
    public String serialize() {
        PetStatsSerializer serializer = PetStatsSerializer.build(this);
        return serializer.serialize();
    }

    /**
     * PetStatsオブジェクトをデシリアライズする
     */
    public static PetStats unzerialize(String base64Str) {
        PetStatsSerializer serializer = PetStatsSerializer.unserialize(base64Str);
        if (serializer == null)
            return null;
        return serializer.buildStats();
    }

    /**
     * スタットをデータベースに保存する（SQLは非同期、YAMLは非同期非対応のため同期）
     */
    public void save() {
        if (GlobalConfig.getInstance().isDatabaseSupport()) {
            PlayerData.saveDB();
        }
        else {
            PlayerData pd = PlayerData.get(pet.getOwner());
            if (pd != null) {
                pd.save();
            }
        }
    }

    /**
     * ペットの可能なレベル一覧における現在のレベルインデックスを返す
     */
    public int getCurrentLevelIndex() {
        int i = 1;
        for(PetLevel level : pet.getPetLevels()) {
            if (level.equals(currentLevel))
                return i;
            i++;
        }
        return -1;
    }

    //------------ スタティックコード -------------//

    private static List<PetStats> petStatsList = new ArrayList<>();

    public static List<PetStats> getPetStats(UUID owner) {
        return petStatsList.stream()
                .filter(petStats -> petStats.getPet().getOwner().equals(owner))
                .collect(Collectors.toList());
    }

    /**
     * 指定されたペットIDに対応するペットスタットを削除する
     */
    public static void remove(String petId) {
        petStatsList.removeAll(petStatsList.stream()
                .filter(stat -> stat.getPet().getId().equals(petId))
                .collect(Collectors.toList()));
    }

    /**
     * 指定されたプレイヤーに対応するペットスタットを削除する
     */
    public static void remove(UUID owner) {
        petStatsList.removeIf(stat -> stat.getPet().getOwner().equals(owner));
    }

    /**
     * 指定されたオーナーに対応するペットスタットを削除する
     */
    public static void remove(String petId, UUID owner) {
        petStatsList.removeAll(petStatsList.stream()
                .filter(stat -> stat.getPet().getOwner().equals(owner)
                        && stat.getPet().getId().equals(petId))
                .collect(Collectors.toList()));
    }

    /**
     * すべてのペットスタットをDBに保存する
     */
    public static void saveAll() {
        if (GlobalConfig.getInstance().isDatabaseSupport()) {
            PlayerData.saveDB();
        }
        else {
            new ArrayList<>(petStatsList).forEach(PetStats::save);
        }
    }

    /**
     * 定期的に全ペットスタットを保存する
     */
    public static void saveStats() {
        // 自動保存遅延（秒）をtickに変換
        long delay = (long)GlobalConfig.getInstance().getAutoSave() * 20;
        // 遅延が負の場合は自動保存を無効化
        if (delay <= 0)
            return;
        // SQLの場合は非同期、YAMLは非同期非対応のため同期で実行
        if (GlobalConfig.getInstance().isDatabaseSupport()) {
            // TODO: 現時点ではMySQLユーザーの自動保存はオンラインプレイヤーのみ対象
            Bukkit.getScheduler().scheduleAsyncRepeatingTask(MCPets.getInstance(), () -> {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    Databases.savePlayerData(p.getUniqueId());
                }
            }, delay, delay);
        }
        else {
            Bukkit.getScheduler().scheduleSyncRepeatingTask(MCPets.getInstance(), () -> new ArrayList<>(petStatsList).forEach(PetStats::save), delay, delay);
        }
    }


    /**
     * 指定されたペットとオーナーに対応するペットスタットを検索する
     * 見つからない場合はnullを返す
     */
    public static PetStats get(String petId, UUID owner) {
        return petStatsList.stream()
                .filter(stat -> stat.getPet().getId().equals(petId) &&
                        stat.getPet().getOwner().equals(owner))
                .findFirst().orElse(null);
    }

    /**
     * ペットスタットを登録する
     */
    public static boolean register(PetStats petStats) {
        // ペットまたはオーナーが存在しない場合は登録しない
        if (petStats.getPet() == null || petStats.getPet().getOwner() == null)
            return false;

        // ペットスタットが既に登録されている場合は上書きする
        if (get(petStats.getPet().getId(), petStats.getPet().getOwner()) != null) {
            petStatsList.remove(get(petStats.getPet().getId(), petStats.getPet().getOwner()));
        }

        // 同一ペット・オーナーの登録がなければペットスタットを登録する
        petStatsList.add(petStats);
        return true;
    }

    /**
     * リスポーンタイマーが動作中の最初のペットスタットを取得する
     */
    public static PetStats getPetStatsOnRespawnTimerRunning(UUID uuid) {
        return petStatsList.stream().filter(petStats -> petStats.getPet().getOwner().equals(uuid) && petStats.isRespawnTimerRunning()).findFirst().orElse(null);
    }

    /**
     * ペットのスタット値を設定する
     */
    public void setStats(double experience, double currentHealth, PetLevel currentLevel) {
        this.experience = experience;
        this.currentHealth = currentHealth;
        this.currentLevel = currentLevel;

        updateChangingData();
        launchRegenerationTimer();
    }
}
