package fr.nocsy.mcpets.utils;

import fr.nocsy.mcpets.MCPets;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.HashMap;

public class PetTimer {

    @Getter
    private static HashMap<PetTimer, Integer> runningTimers = new HashMap<>();

    @Getter
    private int cooldown;
    @Getter
    private int remainingTime;
    private long frequency;

    private int task;

    private final Runnable endingRunnable;

    /**
     * コンストラクタ
     * タスクを繰り返す際のtick周波数を設定する
     */
    public PetTimer(int cooldown, long frequency, Runnable endingRunnable) {
        this.cooldown = cooldown;
        this.remainingTime = 0;
        this.frequency = frequency;
        this.endingRunnable = endingRunnable;
    }

    public void launch(Runnable runnable) {
        // プラグイン無効時（onDisable中など）はタスクを登録しない
        if (MCPets.getInstance() == null || !MCPets.getInstance().isEnabled())
            return;
        // 実行中の場合は現在のスケジューラをキャンセルする
        if (isRunning())
            stop(null);
        remainingTime = cooldown;
        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(MCPets.getInstance(), () -> {
            if (cooldown != Integer.MAX_VALUE)
                remainingTime--;
            if (remainingTime <= 0)
                stop(endingRunnable);

            if (runnable != null)
                runnable.run();
        }, 0L, frequency);
        runningTimers.put(this, task);
    }

    public void stop(Runnable runnable) {
        Bukkit.getScheduler().cancelTask(task);
        runningTimers.remove(this);
        remainingTime = 0;
        if (runnable != null)
            runnable.run();
    }

    public boolean isRunning() {
        return remainingTime > 0;
    }
}
