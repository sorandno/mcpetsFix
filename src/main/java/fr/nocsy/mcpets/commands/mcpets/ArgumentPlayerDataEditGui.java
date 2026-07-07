package fr.nocsy.mcpets.commands.mcpets;

import fr.nocsy.mcpets.PPermission;
import fr.nocsy.mcpets.commands.AArgument;
import fr.nocsy.mcpets.data.inventories.PlayerDataEditPlayerMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /mcpets playerdataeditgui : GUIでプレイヤーのペットデータ（レベル・経験値・権限）を編集する管理者用サブコマンド。
 */
public class ArgumentPlayerDataEditGui extends AArgument {

    public ArgumentPlayerDataEditGui(final CommandSender sender, final String[] args) {
        super("playerdataeditgui", new int[]{1}, sender, args, "/mcpets playerdataeditgui");
    }

    @Override
    public boolean additionalConditions() {
        return sender instanceof Player && sender.hasPermission(PPermission.ADMIN.getPermission());
    }

    @Override
    public void commandEffect() {
        final Player admin = (Player) sender;
        PlayerDataEditPlayerMenu.open(admin, 0);
    }
}
