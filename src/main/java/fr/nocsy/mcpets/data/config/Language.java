package fr.nocsy.mcpets.data.config;

import fr.nocsy.mcpets.utils.Utils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public enum Language {

    INVENTORY_PETS_MENU("§0☀ §4ペット §0☀"),
    INVENTORY_PETS_MENU_INTERACTIONS("§0☀ §4ペット §0☀"),

    INVENTORY_MOUNTS_MENU("§0☀ §4マウント §0☀"),
    INVENTORY_MOUNTS_MENU_INTERACTIONS("§0☀ §4マウント §0☀"),

    MOUNT_ITEM_NAME("§6乗る"),
    MOUNT_ITEM_DESCRIPTION("§7クリックしてペットに乗る"),

    RENAME_ITEM_NAME("§6名前を変更"),
    RENAME_ITEM_DESCRIPTION("§7クリックしてペットの名前を変更する"),

    BACK_TO_PETMENU_ITEM_NAME("§cメニューに戻る"),
    BACK_TO_PETMENU_ITEM_DESCRIPTION("§7クリックしてメニューに戻る"),

    INVENTORY_ITEM_NAME("§6インベントリ"),
    INVENTORY_ITEM_DESCRIPTION("§7クリックしてペットのインベントリを開く"),

    SKINS_ITEM_NAME("§6スキン"),
    SKINS_ITEM_DESCRIPTION("§7クリックしてペットのスキンを変更する"),

    EQUIPMENT_ITEM_NAME("§6装備"),
    EQUIPMENT_DESCRIPTION("§7クリックしてペットの装備を開く"),

    TURNPAGE_ITEM_NAME("§6次のページ §7(§e%currentPage%§8/§7%maxPage%)"),
    TURNPAGE_ITEM_DESCRIPTION("§e右クリック§7 で次のページへ \n§a左クリック§7 で前のページへ"),

    PREVIOUSPAGE_ITEM_NAME("§c前のページ §7(§e%currentPage%§8/§7%maxPage%)"),
    PREVIOUSPAGE_ITEM_DESCRIPTION("§a左クリック§7 で前のページへ"),

    NEXTPAGE_ITEM_NAME("§6次のページ §7(§e%currentPage%§8/§7%maxPage%)"),
    NEXTPAGE_ITEM_DESCRIPTION("§a左クリック§7 で次のページへ"),

    NICKNAME("§9ニックネーム : §7%nickname%"),
    NICKNAME_ITEM_LORE("§cクリックしてペットを呼び戻す"),

    SUMMONED("§7ペットが召喚されました！"),
    REVOKED("§7ペットが呼び戻されました。"),
    REVOKED_FOR_NEW_ONE("§7新しいペットを召喚するため、以前のペットが呼び戻されました。"),
    REVOKED_UNKNOWN("§cペットをスポーンできませんでした。以下のいずれかが原因の可能性があります：" +
            "\n§7- ペット設定内の §cMythicMobが存在しない§7（/mm m spawn でスポーン確認してください）。" +
            "\n§7- ワールドが §cピースフルまたはイージーモード§7 になっている。" +
            "\n§7- リージョンが §cモブのスポーンを制限§7 している（アンカーが攻撃的なモブの可能性があります）。" +
            "\n§7- §cスポーン保護プラグイン§7 が導入されています。別のワールドやスポーンから離れた場所でお試しください。" +
            "\n§7- §c同じID§7 を持つペットが複数存在します。IDがユニークであることを確認してください。"),
    MYTHICMOB_NULL("§cこのペットを召喚できませんでした。関連するMythicMobエンティティまたはファイルがnullか削除されています。"),
    NO_MOB_MATCH("§cこのペットを召喚できませんでした。関連するMythicMobがMythicMobsに登録されていません。"),
    NOT_ALLOWED("§cこのペットを召喚する権限がありません。"),
    OWNER_NOT_FOUND("§cこのペットを召喚できませんでした。召喚者が見つかりませんでした。"),
    REVOKED_BEFORE_CHANGES("§c変更を適用する前にペットが呼び戻されました。"),
    NOT_MOUNTABLE("§cこのペットには乗り場がありません。"),
    ALREADY_MOUNTING("§cすでに何かに乗っています。先に降りてから再試行してください。"),
    NOT_MOUNTABLE_HERE("§cこのエリアではペットに乗れません。"),
    CANT_MOUNT_PET_YET("§cこのペットに乗る権限がありません。"),
    CANT_FOLLOW_HERE("§cこのエリアではペットはついてこられません。"),
    TYPE_NAME_IN_CHAT("§aチャットにペットの名前を入力してください。"),
    IF_WISH_TO_REMOVE_NAME("§a名前を削除したい場合は、チャットに §c%tag%§a と入力してください。"),
    NICKNAME_CHANGED_SUCCESSFULY("§aニックネームを変更しました！"),
    NICKNAME_NOT_CHANGED("§cニックネームが空のため変更できませんでした。再試行してください。"),
    TAG_TO_REMOVE_NAME("None"),
    ALREADY_INSIDE_VEHICULE("§7すでに何かに乗っています。この機能を使用するには現在のマウントから降りてください。"),
    PET_DOESNT_EXIST("§cこのペットは存在しません。IDを確認してください。"),
    PLAYER_NOT_CONNECTED("§cプレイヤー §6%player%§c はオンラインではありません。"),
    BLACKLISTED_WORD("§cリネーム操作がキャンセルされました。%word% はペット名に使用できない単語です。"),
    NO_ACTIVE_PET("§cアクティブなペットがいません。"),
    SPECIFY_PET("§c複数のアクティブなペットがいます。どのペットか指定してください: §e%pets%"),
    SIGNAL_STICK_GIVEN("§aオーダースティックを受け取りました。右クリックでオーダーを発動、左クリックでオーダーを切り替えます。"),
    SIGNAL_STICK_SIGNAL("§6アクティブなオーダー : §e%signal%"),
    LOOP_SPAWN("§cペットが多数のテレポートに問題があったため、呼び戻されました。"),
    REQUIRES_ITEM_IN_HAND("§c設定を更新するには手にアイテムを持って使用してください。"),
    ITEM_UPDATED("§aアイテムをキー §e%key% §aで正常に更新しました。"),
    ITEM_DOESNT_EXIST("§aキー §e%key%§c のアイテムは存在しません。追加する場合は §eadd§c 引数を使用してください。"),
    KEY_DOESNT_EXIST("§c指定されたキーは登録されていません。"),
    KEY_REMOVED("§aキーアイテムが正常に削除されました。"),
    KEY_ALREADY_EXISTS("§cこのキーはすでに登録されています。現在のアイテムを置き換えるために使用してください。"),
    KEY_ADDED("§aキーと対応するアイテムが正常に追加されました。"),
    KEY_LIST("§a利用可能なキー:"),

    RELOAD_SUCCESS("§a正常にリロードされました。"),
    HOW_MANY_PETS_LOADED("§a%numberofpets% 匹のペットが正常に登録されました"),

    REQUIRES_MODELENGINE("§cこのプラグインには ModelEngine R4.0.6 または BetterModel v2.0.1 が必要です。要件が満たされていないようです。"),

    USAGE("§cこのコマンドは存在しません。\n§7Wikiを確認してください: §nhttps://mcpets.gitbook.io/mcpets/tutorials/plugin-features/commands"),
    NO_PERM("§cこのコマンドを使用する権限がありません。"),
    BLACKLISTED_WORLD("§cMCPets はこのワールドでは無効です。"),

    CATEGORY_MENU_TITLE("§0☀ §4ペット §8- カテゴリを選択 §0☀"),
    CATEGORY_DOESNT_EXIST("§cこのカテゴリは存在しません。"),

    PET_INVENTORY_TITLE("§0☀ §4%pet% §8- §0インベントリ §0☀§"),

    PET_INVENTORY_COULDNOT_OPEN("§cこのインベントリは存在しないか、開けません。"),

    PET_SKINS_TITLE("§0☀ §4%pet% §8- §0スキン §0☀§"),

    SKIN_COULD_NOT_APPLY("§cスキンをペットに適用できませんでした。"),
    SKIN_APPLIED("§aスキンを変更しました！"),

    GLOBAL_RESPAWN_TIMER_RUNNING("§cこのペットをスポーンできません。%timeLeft%s/%cooldown%s 待ってください。"),
    RESPAWN_TIMER_RUNNING("§cこのペットをスポーンできません。傷が癒えていません。%timeLeft%s/%cooldown%s 待ってください。"),
    REVOKE_TIMER_RUNNING("§cこのペットをスポーンできません。傷が癒えていません。%timeLeft%s/%cooldown%s 待ってください。"),

    PLAYER_OR_PET_DOESNT_EXIST("§cこのペットは存在しないか、このプレイヤーはサーバーにログインしたことがありません。"),
    STATS_CLEARED("§aすべてのスタットが正常にクリアされました！"),
    STATS_CLEARED_FOR_PET_FOR_PLAYER("§aプレイヤー %player% のペット %petId% のスタットが正常にクリアされました。"),
    STATS_CLEARED_FOR_PET("§aペット %petId% のすべてのスタットが正常にクリアされました"),

    PET_TAMING_PROGRESS("§7テイム進捗 §a%progress%% §7- %progressbar%"),
    PET_COULD_NOT_EVOLVE("§7ペットは進化できませんでした（§c進化先を既に所持§7）。"),
    PETFOOD_DOESNT_EXIST("§cこのペットフードは存在しません。"),
    PETUNLOCK_NOPERM("§cこのアイテムでペットをアンロックする権限がありません。"),
    PETUNLOCKED("§aペット %petName% をアンロックしました。おめでとうございます！"),
    PETUNLOCKED_ALREADY("§cペット §6%petName%§c はすでに所持しています。"),

    PET_ALREADY_TAMED("§cこのペットはすでにテイム済みです。"),
    PET_DOESNT_EAT("§cこのペットはその食べ物を食べられません。"),
    PET_FOOD_ON_COOLDOWN("§cあと %timeleft% 秒後にペットがこの食べ物を食べます"),

    PET_STATUS_ALIVE("§a利用可能"),
    PET_STATUS_REVOKED("§c利用不可 §7(残り %timeleft%s)"),
    PET_STATUS_DEAD("§c死亡中 §7(残り %timeleft%s)"),

    PET_STATS("§6✦ ペット情報 ✦" +
            "\n§7状態: %status%" +
            "\n§6レベル §7- §6%levelname%" +
            "\n " +
            "\n§f%health%§7/§f%maxhealth% §c❤" +
            "\n§7再生: %regeneration% ❤/秒" +
            "\n§7ダメージ補正: §f%damagemodifier%%" +
            "\n§7耐性補正: §f%resistancemodifier%%" +
            "\n§7パワー: §f%power%%" +
            "\n " +
            "\n§7経験値: §a%experience%/%threshold% xp" +
            "\n%progressbar%"),

    PET_STATS_EVOLUTION_ALREADY_OWNED("§c進化先はすでに所持しています。"),
    PET_STATS_MAX_LEVEL("§7最大レベルに達しました。"),
    MAX_ACTIVE_PETS_REACHED("§cアクティブなペットの上限数に達しました！"),
    PET_REPLACED_BY_NEW("§e%oldpet% が %newpet% に入れ替わりました！"),
    DEBUGGER_JOINING("§aデバッガーが有効になりました。デバッグ情報を受信します。"),
    DEBUGGER_LEAVE("§aデバッガーが §7無効§a になりました。デバッグ情報を受信しません。");

    private String message;

    Language(String message) {
        this.message = message;
    }

    public void reload() {
        if (LanguageConfig.getInstance().getMap().containsKey(this.name().toLowerCase())) {
            this.message = LanguageConfig.getInstance().getMap().get(this.name().toLowerCase());
        }
    }

    public String getMessage() {
        String m = Utils.hex(message);

        m = Utils.applyPlaceholders(null, m);
        return m;
    }

    public String getMessagePAPI() {
        String m = Utils.hex(message);

        m = Utils.applyPlaceholders(null, m);
        return m;
    }

    public Component getComponent() {
        return Utils.toComponent(Utils.hex(GlobalConfig.getInstance().getPrefix() + getMessage()));
    }

    public void sendMessage(Player p) {
        if (message.isEmpty())
            return;
        ((Audience) p).sendMessage(getComponent());
    }

    public void sendMessage(CommandSender sender) {
        if (message.isEmpty())
            return;
        ((Audience) sender).sendMessage(getComponent());
    }

    public void sendMessageFormated(CommandSender sender, FormatArg... args) {
        if (message.isEmpty())
            return;

        String toSend = getMessage();
        for (FormatArg arg : args) {
            toSend = arg.applyToString(toSend);
        }
        ((Audience) sender).sendMessage(Utils.toComponent(Utils.hex(GlobalConfig.getInstance().getPrefix() + toSend)));
    }

    public String getMessageFormatted(FormatArg... args) {
        String toSend = getMessage();
        for (FormatArg arg : args) {
            toSend = arg.applyToString(toSend);
        }
        return toSend;
    }
}
