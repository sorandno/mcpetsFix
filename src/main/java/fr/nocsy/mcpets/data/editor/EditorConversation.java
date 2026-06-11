package fr.nocsy.mcpets.data.editor;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class EditorConversation {

    private static HashMap<UUID, EditorConversation> conversations = new HashMap<>();

    @Getter
    private Player player;

    @Getter
    private EditorItems editorItem;

    public EditorConversation(Player p, EditorItems editorItem) {
        this.player = p;
        this.editorItem = editorItem;
    }

    public static EditorConversation getConversation(Player p) {
        return conversations.get(p.getUniqueId());
    }

    public void start() {
        conversations.put(player.getUniqueId(), this);
        player.sendMessage("§aチャットに \"§e" + editorItem.getId().replace("_", " ").toLowerCase() + "§a\" に設定したい値を入力してください。");

        if (editorItem.getType().equals(EditorExpectationType.STRING_LIST)) {
            player.sendMessage("§e各要素をカンマ §c, §e で区切ってください。例: §6SPELL,SHIELD");
        }
        player.sendMessage("§a値を変更しない場合は §cQuit§a と入力してください。");
    }

    public boolean entryMatch(String entry) {
        return editorItem.getType().matches(entry);
    }

    public Object output(String entry) {
        return editorItem.getType().parse(entry);
    }

    public void quit() {
        conversations.remove(player.getUniqueId());
        player.sendMessage("§c値は変更されませんでした。");
    }

    public void end() {
        conversations.remove(player.getUniqueId());
        player.sendMessage("§a\"§e" + editorItem.getId().replace("_", " ").toLowerCase() + "§a\" の値が正常に変更されました！");
        player.sendMessage("§a変更を反映するには §nMCPets をリロード§a することを忘れずに。");
    }

    /**
     * アクティブなエディタ会話をすべてクリアする。
     * プラグイン無効化時に呼び出し、アンロード済みJARからクラスを読み込もうとする
     * リスナーによる {@link IllegalStateException}（zip file closed）を防ぐ。
     */
    public static void clearAll() {
        for (EditorConversation conversation : conversations.values()) {
            if (conversation.getPlayer() != null && conversation.getPlayer().isOnline()) {
                conversation.getPlayer().sendMessage("§cプラグインがリロードされたため、エディタ会話が中断されました。");
            }
        }
        conversations.clear();
    }
}
