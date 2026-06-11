package fr.nocsy.mcpets.data.editor;

import fr.nocsy.mcpets.data.Category;
import fr.nocsy.mcpets.utils.PDCTag;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.PetSkin;
import fr.nocsy.mcpets.data.config.CategoryConfig;
import fr.nocsy.mcpets.data.config.ItemsListConfig;
import fr.nocsy.mcpets.data.config.PetConfig;
import fr.nocsy.mcpets.data.config.PetFoodConfig;
import fr.nocsy.mcpets.data.livingpets.PetFood;
import fr.nocsy.mcpets.data.livingpets.PetLevel;
import fr.nocsy.mcpets.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum EditorItems {

    UNKNOWN(UNKNOWN(), null, null, null, null, false),
    FILLER(FILLER(), null, null, null, null, false),

    BACK_TO_GLOBAL_SELECTION(BACK_TO_ITEM("グローバルメニュー"), null, null, null, EditorState.GLOBAL_EDITOR, false),
    BACK_TO_PET_SELECTION(BACK_TO_ITEM("ペット選択メニュー"), null, null, null, EditorState.PET_EDITOR, false),
    BACK_TO_PET_EDIT(BACK_TO_ITEM("ペットエディタ"), null, null, null, EditorState.PET_EDITOR_EDIT, false),
    BACK_TO_PET_LEVELS_EDIT(BACK_TO_ITEM("ペットレベル"), null, null, null, EditorState.PET_EDITOR_LEVELS, false),
    BACK_TO_PET_SKINS_EDIT(BACK_TO_ITEM("ペットスキン"), null, null, null, EditorState.PET_EDITOR_SKINS, false),
    BACK_TO_CATEGORIES_EDIT(BACK_TO_ITEM("カテゴリ"), null, null, null, EditorState.CATEGORY_EDITOR, false),
    BACK_TO_ITEM_EDITOR(BACK_TO_ITEM("アイテム"), null, null, null, EditorState.ITEM_EDITOR, false),
    BACK_TO_PETFOOD_EDITOR(BACK_TO_ITEM("ペットフード"), null, null, null, EditorState.PETFOOD_EDITOR, false),

    // デフォルト選択メニュー
    CONFIG_EDITOR(CONFIG_EDITOR(), null, null, null, EditorState.CONFIG_EDITOR, false),
    PET_EDITOR(PET_EDITOR(), null, null, null, EditorState.PET_EDITOR, false),
    CATEGORY_EDITOR(CATEGORY_EDITOR(), null, null, null, EditorState.CATEGORY_EDITOR, false),
    ITEM_EDITOR(ITEM_EDITOR(), null, null, null, EditorState.ITEM_EDITOR, false),
    PETFOOD_EDITOR(PETFOOD_EDITOR(), null, null, null, EditorState.PETFOOD_EDITOR, false),

    // コンフィグエディタ
    CONFIG_EDITOR_PREFIX(CONFIG_EDITOR_PREFIX(), "Prefix", "config", EditorExpectationType.STRING, null, true),
    CONFIG_EDITOR_DEFAULT_NAME(CONFIG_EDITOR_DEFAULT_NAME(), "DefaultName", "config", EditorExpectationType.STRING, null, true),
    CONFIG_EDITOR_USE_DEFAULT_MYTHICMOBS_NAMES(CONFIG_EDITOR_USE_DEFAULT_MYTHICMOBS_NAMES(), "UseDefaultMythicMobsNames", "config", EditorExpectationType.BOOLEAN, null, true),
    CONFIG_EDITOR_OVERRIDE_DEFAULT_NAME(CONFIG_EDITOR_OVERRIDE_DEFAULT_NAME(), "OverrideDefaultName", "config", EditorExpectationType.BOOLEAN, null, true),
    CONFIG_EDITOR_RIGHT_CLICK_TO_OPEN_MENU(CONFIG_EDITOR_RIGHT_CLICK_TO_OPEN_MENU(), "RightClickToOpenMenu", "config", EditorExpectationType.BOOLEAN, null, true),
    CONFIG_EDITOR_LEFT_CLICK_TO_OPEN_MENU(CONFIG_EDITOR_LEFT_CLICK_TO_OPEN_MENU(), "LeftClickToOpenMenu", "config", EditorExpectationType.BOOLEAN, null, true),
    CONFIG_EDITOR_SNEAKMODE(CONFIG_EDITOR_SNEAKMODE(), "SneakMode", "config", EditorExpectationType.BOOLEAN, null, true),
    CONFIG_EDITOR_NAMEABLE(CONFIG_EDITOR_NAMEABLE(), "Nameable", "config", EditorExpectationType.BOOLEAN, null, true),
    CONFIG_EDITOR_MOUNTABLE(CONFIG_EDITOR_MOUNTABLE(), "Mountable", "config", EditorExpectationType.BOOLEAN, null, true),
    CONFIG_EDITOR_DISTANCE_TELEPORT(CONFIG_EDITOR_DISTANCE_TELEPORT(), "DistanceTeleport", "config", EditorExpectationType.FLOAT, null, true),
    CONFIG_EDITOR_MAX_NAME_LENGTH(CONFIG_EDITOR_MAX_NAME_LENGTH(), "MaxNameLenght", "config", EditorExpectationType.INT, null, true),
    CONFIG_EDITOR_INVENTORY_SIZE(CONFIG_EDITOR_INVENTORY_SIZE(), "InventorySize", "config", EditorExpectationType.INT, null, true),
    CONFIG_EDITOR_ENABLE_CLICK_BACK_TO_MENU(CONFIG_EDITOR_ENABLE_CLICK_BACK_TO_MENU(), "EnableClickBackToMenu", "config", EditorExpectationType.BOOLEAN, null, true),
    CONFIG_EDITOR_ACTIVATE_BACK_MENU_ICON(CONFIG_EDITOR_ACTIVATE_BACK_MENU_ICON(), "ActivateBackMenuIcon", "config", EditorExpectationType.BOOLEAN, null, true),
    CONFIG_EDITOR_DISMOUNT_ON_DAMAGED(CONFIG_EDITOR_DISMOUNT_ON_DAMAGED(), "DismountOnDamaged", "config", EditorExpectationType.BOOLEAN, null, true),
    CONFIG_EDITOR_DISABLE_INVENTORY_WHILE_SIGNAL_STICK(CONFIG_EDITOR_DISABLE_INVENTORY_WHILE_SIGNAL_STICK(), "DisableInventoryWhileHoldingSignalStick", "config", EditorExpectationType.BOOLEAN, null, true),
    CONFIG_EDITOR_PERCENT_HEALTH_ON_RESPAWN(CONFIG_EDITOR_PERCENT_HEALTH_ON_RESPAWN(), "PercentHealthOnRespawn", "config", EditorExpectationType.FLOAT, null, true),
    CONFIG_EDITOR_AUTO_SAVE_DELAY(CONFIG_EDITOR_AUTO_SAVE_DELAY(), "AutoSaveDelay", "config", EditorExpectationType.INT, null, true),
    CONFIG_EDITOR_DEFAULT_RESPAWN_COOLDOWN(CONFIG_EDITOR_DEFAULT_RESPAWN_COOLDOWN(), "DefaultRespawnCooldown", "config", EditorExpectationType.INT, null, true),
    CONFIG_EDITOR_GLOBAL_RESPAWN_COOLDOWN(CONFIG_EDITOR_GLOBAL_RESPAWN_COOLDOWN(), "GlobalRespawnCooldown", "config", EditorExpectationType.BOOLEAN, null, true),
    CONFIG_EDITOR_GLOBAL_AUTORESPAWN(CONFIG_EDITOR_GLOBAL_AUTORESPAWN(), "AutoRespawn", "config", EditorExpectationType.BOOLEAN, null, true),

    // ペットエディタ
    PET_EDITOR_EDIT_PET(UNKNOWN(), null, null, EditorExpectationType.PET, null, false),
    PET_EDITOR_CREATE_NEW(CREATE_NEW_ITEM("ペット", Material.MAGMA_CUBE_SPAWN_EGG), null, null, EditorExpectationType.PET_CREATE, null, false),
    PAGE_SELECTOR(PAGE_SELECTOR(), null, null, EditorExpectationType.PAGE_SELECTOR, null, false),

    PET_EDITOR_DELETE(DELETE("ペット"), null, null, EditorExpectationType.PET_DELETE, null, false),
    PET_EDITOR_LEVELS(PET_EDITOR_LEVELS(), null, null, null, EditorState.PET_EDITOR_LEVELS, false),
    PET_EDITOR_SKINS(PET_EDITOR_SKINS(), null, null, null, EditorState.PET_EDITOR_SKINS, false),

    PET_EDITOR_ICON(PET_EDITOR_ICON(), "Icon.Raw", null, EditorExpectationType.ITEM, null, false),
    PET_EDITOR_MYTHICMOB(PET_EDITOR_MYTHICMOB(), "MythicMob", null, EditorExpectationType.MYTHICMOB, null, false),
    PET_EDITOR_PERMISSION(PET_EDITOR_PERMISSION(), "Permission", null, EditorExpectationType.STRING, null, true),
    PET_EDITOR_MOUNTABLE(PET_EDITOR_MOUNTABLE(), "Mountable", null, EditorExpectationType.BOOLEAN, null, true),
    PET_EDITOR_MOUNT_TYPE(PET_EDITOR_MOUNT_TYPE(), "MountType", null, EditorExpectationType.MOUNT_TYPE, null, true),
    PET_EDITOR_DESPAWN_ON_DISMOUNT(PET_EDITOR_DESPAWN_ON_DISMOUNT(), "DespawnOnDismount", null, EditorExpectationType.BOOLEAN, null, true),
    PET_EDITOR_AUTORIDE(PET_EDITOR_AUTORIDE(), "AutoRide", null, EditorExpectationType.BOOLEAN, null, true),
    PET_EDITOR_MOUNT_PERMISSION(PET_EDITOR_MOUNT_PERMISSION(), "MountPermission", null, EditorExpectationType.STRING, null, true),
    PET_EDITOR_DESPAWN_SKILL(PET_EDITOR_DESPAWN_SKILL(), "DespawnSkill", null, EditorExpectationType.SKILL, null, true),
    PET_EDITOR_DISTANCE(PET_EDITOR_DISTANCE(), "Distance", null, EditorExpectationType.FLOAT, null, true),
    PET_EDITOR_SPAWN_RANGE(PET_EDITOR_SPAWN_RANGE(), "SpawnRange", null, EditorExpectationType.FLOAT, null, true),
    PET_EDITOR_COMING_BACK_RANGE(PET_EDITOR_COMING_BACK_RANGE(), "ComingBackRange", null, EditorExpectationType.FLOAT, null, true),
    PET_EDITOR_INVENTORY_SIZE(PET_EDITOR_INVENTORY_SIZE(), "InventorySize", null, EditorExpectationType.INT, null, true),
    PET_EDITOR_TAMING_PROGRESS_SKILL(PET_EDITOR_TAMING_PROGRESS_SKILL(), "Taming.TamingProgressSkill", null, EditorExpectationType.STRING, null, true),
    PET_EDITOR_TAMING_FINISHED_SKILL(PET_EDITOR_TAMING_FINISHED_SKILL(), "Taming.TamingFinishedSkill", null, EditorExpectationType.STRING, null, true),
    PET_EDITOR_SIGNALS(PET_EDITOR_SIGNALS(), "Signals.Values", null, EditorExpectationType.STRING_LIST, null, true),
    PET_EDITOR_SIGNAL_STICK(PET_EDITOR_SIGNAL_STICK(), "Signals.Item.Raw", null, EditorExpectationType.ITEM, null, true),
    PET_EDITOR_GET_SIGNAL_STICK_FROM_MENU(PET_EDITOR_GET_SIGNAL_STICK_FROM_MENU(), "Signals.Item.GetFromMenu", null, EditorExpectationType.BOOLEAN, null, true),
    // ペットエディタ - レベル
    PET_EDITOR_EDIT_LEVEL(UNKNOWN(), null, null, EditorExpectationType.PET_LEVEL_EDIT, null, false),
    PET_EDITOR_EDIT_LEVEL_DELETE(DELETE("レベル"), null, null, EditorExpectationType.PET_LEVEL_DELETE, null, false),
    PET_EDITOR_LEVEL_CREATE_NEW(CREATE_NEW_ITEM("レベル", Material.EXPERIENCE_BOTTLE), null, null, EditorExpectationType.PET_LEVEL_CREATE, null, false),
    PET_EDITOR_EDIT_LEVEL_NAME(PET_EDITOR_LEVEL_NAME(), "Levels.%path%.Name", null, EditorExpectationType.STRING, null, false),
    PET_EDITOR_EDIT_LEVEL_EXP_THRESHOLD(PET_EDITOR_LEVEL_EXP_THRESHOLD(), "Levels.%path%.ExperienceThreshold", null, EditorExpectationType.POSITIVE_INT, null, false),
    PET_EDITOR_EDIT_LEVEL_MAX_HEALTH(PET_EDITOR_LEVEL_MAX_HEALTH(), "Levels.%path%.MaxHealth", null, EditorExpectationType.POSITIVE_INT, null, false),
    PET_EDITOR_EDIT_LEVEL_REGENERATION(PET_EDITOR_LEVEL_REGENERATION(), "Levels.%path%.Regeneration", null, EditorExpectationType.POSITIVE_FLOAT, null, false),
    PET_EDITOR_EDIT_LEVEL_RESISTANCE_MODIFIER(PET_EDITOR_LEVEL_RESISTANCE_MODIFIER(), "Levels.%path%.ResistanceModifier", null, EditorExpectationType.FLOAT, null, false),
    PET_EDITOR_EDIT_LEVEL_DAMAGE_MODIFIER(PET_EDITOR_LEVEL_DAMAGE_MODIFIER(), "Levels.%path%.DamageModifier", null, EditorExpectationType.FLOAT, null, false),
    PET_EDITOR_EDIT_LEVEL_POWER(PET_EDITOR_LEVEL_POWER(), "Levels.%path%.Power", null, EditorExpectationType.FLOAT, null, false),
    PET_EDITOR_EDIT_LEVEL_COOLDOWN_RESPAWN(PET_EDITOR_LEVEL_COOLDOWN_RESPAWN(), "Levels.%path%.Cooldowns.Respawn", null, EditorExpectationType.POSITIVE_INT, null, true),
    PET_EDITOR_EDIT_LEVEL_COOLDOWN_REVOKE(PET_EDITOR_LEVEL_COOLDOWN_REVOKE(), "Levels.%path%.Cooldowns.Revoke", null, EditorExpectationType.POSITIVE_INT, null, true),
    PET_EDITOR_EDIT_LEVEL_INVENTORY_EXTENSION(PET_EDITOR_LEVEL_INVENTORY_EXTENSION(), "Levels.%path%.InventoryExtension", null, EditorExpectationType.INVENTORY_SIZE, null, true),
    PET_EDITOR_EDIT_LEVEL_ANNOUNCEMENT_TEXT(PET_EDITOR_LEVEL_ANNOUNCEMENT_TEXT(), "Levels.%path%.Announcement.Text", null, EditorExpectationType.STRING, null, true),
    PET_EDITOR_EDIT_LEVEL_ANNOUNCEMENT_TYPE(PET_EDITOR_LEVEL_ANNOUNCEMENT_TYPE(), "Levels.%path%.Announcement.Type", null, EditorExpectationType.ANNOUNCEMENT_TYPE, null, true),
    PET_EDITOR_EDIT_LEVEL_ANNOUNCEMENT_SKILL(PET_EDITOR_LEVEL_ANNOUNCEMENT_SKILL(), "Levels.%path%.Announcement.Skill", null, EditorExpectationType.STRING, null, true),
    PET_EDITOR_EDIT_LEVEL_EVOLUTION_PET_ID(PET_EDITOR_LEVEL_EVOLUTION_PET_ID(), "Levels.%path%.Evolution.PetId", null, EditorExpectationType.PET_ID, null, true),
    PET_EDITOR_EDIT_LEVEL_EVOLUTION_DELAY(PET_EDITOR_LEVEL_EVOLUTION_DELAY(), "Levels.%path%.Evolution.DelayBeforeEvolution", null, EditorExpectationType.POSITIVE_INT, null, true),
    PET_EDITOR_EDIT_LEVEL_EVOLUTION_REMOVE_ACCESS(PET_EDITOR_LEVEL_EVOLUTION_REMOVE_ACCESS(), "Levels.%path%.Evolution.RemoveAccess", null, EditorExpectationType.BOOLEAN, null, true),
    // ペットエディタ - スキン
    PET_EDITOR_EDIT_SKIN(UNKNOWN(), null, null, EditorExpectationType.PET_SKIN_EDIT, null, false),
    PET_EDITOR_EDIT_SKIN_DELETE(DELETE("スキン"), null, null, EditorExpectationType.PET_SKIN_DELETE, null, false),
    PET_EDITOR_SKIN_CREATE_NEW(CREATE_NEW_ITEM("スキン", Material.LEATHER), null, null, EditorExpectationType.PET_SKIN_CREATE, null, false),
    PET_EDITOR_EDIT_SKIN_ICON(UNKNOWN(), "%path%.Icon.Raw", null, EditorExpectationType.ITEM, null, false),
    PET_EDITOR_EDIT_SKIN_MYTHICMOB(PET_EDITOR_SKIN_MYTHICMOB(), "%path%.MythicMob", null, EditorExpectationType.MYTHICMOB, null, false),
    PET_EDITOR_EDIT_SKIN_PERMISSION(PET_EDITOR_SKIN_PERMISSION(), "%path%.Permission", null, EditorExpectationType.STRING, null, true),

    // カテゴリエディタ
    CATEGORY_EDITOR_EDIT_CATEGORY(UNKNOWN(), null, null, EditorExpectationType.CATEGORY_EDIT, null, false),

    CATEGORY_EDITOR_CATEGORY_CREATE(CREATE_NEW_ITEM("カテゴリ", Material.KNOWLEDGE_BOOK), null, null, EditorExpectationType.CATEGORY_CREATE, null, false),
    CATEGORY_EDITOR_CATEGORY_DELETE(DELETE("カテゴリ"), null, null, EditorExpectationType.CATEGORY_DELETE, null, false),

    CATEGORY_EDITOR_CATEGORY_EDIT_ID(CATEGORY_EDITOR_CATEGORY_EDIT_ID(), "Id", null, EditorExpectationType.STRING, null, false),
    CATEGORY_EDITOR_CATEGORY_EDIT_ICON(UNKNOWN(), "Icon", null, EditorExpectationType.ITEM, null, false),
    CATEGORY_EDITOR_CATEGORY_EDIT_ICON_NAME(CATEGORY_EDITOR_CATEGORY_EDIT_ICON_NAME(), "IconName", null, EditorExpectationType.STRING, null, false),
    CATEGORY_EDITOR_CATEGORY_EDIT_TITLE_NAME(CATEGORY_EDITOR_CATEGORY_EDIT_TITLE_NAME(), "DisplayName", null, EditorExpectationType.STRING, null, false),
    CATEGORY_EDITOR_CATEGORY_EDIT_DEFAULT_CATEGORY(CATEGORY_EDITOR_CATEGORY_EDIT_DEFAULT_CATEGORY(), "DefaultCategory", null, EditorExpectationType.BOOLEAN, null, true),
    CATEGORY_EDITOR_CATEGORY_EDIT_EXCLUDED_CATEGORIES(CATEGORY_EDITOR_CATEGORY_EDIT_EXCLUDED_CATEGORIES(), "ExcludedCategories", null, EditorExpectationType.STRING_LIST, null, true),
    CATEGORY_EDITOR_CATEGORY_EDIT_PET_ADD(CATEGORY_EDITOR_CATEGORY_EDIT_PET_ADD(), "Pets", null, EditorExpectationType.CATEGORY_PET_LIST_ADD, null, false),
    CATEGORY_EDITOR_CATEGORY_EDIT_PET_REMOVE(CATEGORY_EDITOR_CATEGORY_EDIT_PET_REMOVE(), "Pets", null, EditorExpectationType.CATEGORY_PET_LIST_REMOVE, null, false),

    // アイテムエディタ
    ITEMS_EDIT(UNKNOWN(), "%path%", ItemsListConfig.getInstance().getFullPath(), EditorExpectationType.ITEM_EDIT, null, false),
    ITEMS_DELETE(DELETE("アイテム"), null, null, EditorExpectationType.ITEM_DELETE, null, false),
    ITEMS_CREATE(CREATE_NEW_ITEM("アイテム", Material.EMERALD), "%path%", ItemsListConfig.getInstance().getFullPath(), EditorExpectationType.ITEM_CREATE, null, false),
    ITEMS_EDIT_ID(ITEMS_EDIT_ID(), "%path%", ItemsListConfig.getInstance().getFullPath(), EditorExpectationType.ITEM_SECTION_ID, null, false),
    ITEMS_EDIT_ITEM(UNKNOWN(), "%path%", ItemsListConfig.getInstance().getFullPath(), EditorExpectationType.ITEM, null, false),

    // ペットフード
    PETFOOD_EDITOR_EDIT(UNKNOWN(), null, "petfoods", EditorExpectationType.PETFOOD_EDIT, null, false),
    PETFOOD_EDITOR_EDIT_CREATE(CREATE_NEW_ITEM("ペットフード", Material.COOKED_CHICKEN), null, "petfoods", EditorExpectationType.PETFOOD_CREATE, null, false),

    PETFOOD_EDITOR_EDIT_DELETE(DELETE("ペットフード"), null, "petfoods", EditorExpectationType.PETFOOD_DELETE, null, false),
    PETFOOD_EDITOR_EDIT_ID(PETFOOD_EDITOR_EDIT_ID(), "%path%", "petfoods", EditorExpectationType.PETFOOD_ID, null, false),
    PETFOOD_EDITOR_EDIT_ITEM_ID(UNKNOWN(), "%path%.ItemId", "petfoods", EditorExpectationType.ITEM_ID_OR_MATERIAL, null, false),
    PETFOOD_EDITOR_EDIT_TYPE(PETFOOD_EDITOR_EDIT_TYPE(), "%path%.Type", "petfoods", EditorExpectationType.PETFOOD_TYPE, null, false),
    PETFOOD_EDITOR_EDIT_POWER(PETFOOD_EDITOR_EDIT_POWER(), "%path%.Power", "petfoods", EditorExpectationType.FLOAT, null, false),
    PETFOOD_EDITOR_EDIT_DURATION(PETFOOD_EDITOR_EDIT_DURATION(), "%path%.Duration", "petfoods", EditorExpectationType.INT, null, false),
    PETFOOD_EDITOR_EDIT_OPERATOR(PETFOOD_EDITOR_EDIT_OPERATOR(), "%path%.Operator", "petfoods", EditorExpectationType.OPERATOR_TYPE, null, true),
    PETFOOD_EDITOR_EDIT_SIGNAL(PETFOOD_EDITOR_EDIT_SIGNAL(), "%path%.Signal", "petfoods", EditorExpectationType.STRING, null, true),
    PETFOOD_EDITOR_EDIT_PETS_ADD(PETFOOD_EDITOR_EDIT_PETS_ADD(), "%path%.Pets", "petfoods", EditorExpectationType.PETFOOD_PET_LIST_ADD, null, false),
    PETFOOD_EDITOR_EDIT_PETS_REMOVE(PETFOOD_EDITOR_EDIT_PETS_REMOVE(), "%path%.Pets", "petfoods", EditorExpectationType.PETFOOD_PET_LIST_REMOVE, null, false),

    PETFOOD_EDITOR_EDIT_EVOLUTION(PETFOOD_EDITOR_EDIT_EVOLUTION(), "%path%.Evolution", "petfoods", EditorExpectationType.PET_ID, null, true),
    PETFOOD_EDITOR_EDIT_EXP_THRESHOLD(PETFOOD_EDITOR_EDIT_EXP_THRESHOLD(), "%path%.ExperienceThreshold", "petfoods", EditorExpectationType.POSITIVE_INT, null, true),
    PETFOOD_EDITOR_EDIT_DELAY(PETFOOD_EDITOR_EDIT_DELAY(), "%path%.DelayBeforeEvolution", "petfoods", EditorExpectationType.POSITIVE_INT, null, true),

    PETFOOD_EDITOR_EDIT_PERMISSION(PETFOOD_EDITOR_EDIT_PERMISSION(), "%path%.Permission", "petfoods", EditorExpectationType.STRING, null, true),
    PETFOOD_EDITOR_EDIT_UNLOCKED_PET(PETFOOD_EDITOR_EDIT_UNLOCKED_PET(), "%path%.UnlockPet", "petfoods", EditorExpectationType.PET_ID, null, true);

    private final static String editorTag = "MCPets:Editor:";
    @Getter
    private static ArrayList<String> cachedDeleted = new ArrayList<>();

    public static String RESET_VALUE_TAG = "ResetValue°897698575";

    @Getter
    private String id;
    @Setter
    private ItemStack item;
    @Getter
    private String filePath;
    @Getter
    private String inputFilePath;
    @Getter
    private String variablePath;
    private String variablePathPlaceholder;
    @Getter
    private Object value;
    @Getter
    private EditorExpectationType type;
    @Getter
    private EditorState nextState;
    @Getter
    private boolean resetable;

    EditorItems(final ItemStack item, final String variablePath, final String filePath, final EditorExpectationType type, final EditorState nextState, final boolean resetable) {
        this.id = this.name().toUpperCase();
        this.item = item;
        this.inputFilePath = filePath;
        this.filePath = "./plugins/MCPets/" + filePath + ".yml";
        this.variablePath = variablePath;
        this.variablePathPlaceholder = "";
        this.type = type;
        this.nextState = nextState;
        this.resetable = resetable;

        refreshData();
    }

    public void refreshData() {
        if (filePath != null && variablePath != null) {
            final File file = new File(this.filePath);
            final FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            this.value = config.get(variablePath.replace("%path%", variablePathPlaceholder));
        }
    }

    public boolean is(final EditorItems other) {
        return other.getId().equals(this.getId());
    }

    public EditorItems setFilePath(final String path) {
        this.filePath = path;
        refreshData();
        return this;
    }

    public EditorItems replaceVariablePath(final String pathPlaceholder) {
        this.variablePathPlaceholder = pathPlaceholder;
        refreshData();
        return this;
    }

    public boolean save(final Player creator) {
        if (this.value == null)
            return false;

        if (this.getType().equals(EditorExpectationType.PET_CREATE)) {
            final String illegalCharacters = "#%<>&*{}?/\\$§+!`|'\"=:@.";
            for (final char character : illegalCharacters.toCharArray()) {
                this.value = this.value.toString().replace(""+character, "");
            }
            this.value = this.value.toString().replace(" ", "_");
            // ペット設定を作成してペットオブジェクトに追加する
            final PetConfig petConfig = new PetConfig("Pets/", this.value.toString() + ".yml");
            Pet.getObjectPets().add(petConfig.getPet());
            return true;
        }
        else if (this.getType().equals(EditorExpectationType.CATEGORY_PET_LIST_ADD) ||
                this.getType().equals(EditorExpectationType.CATEGORY_PET_LIST_REMOVE)) {
            final Pet pet = Pet.getFromId(this.value + "");
            if (pet == null)
                return false;

            final EditorEditing editing = EditorEditing.get(creator);
            final CategoryConfig config = CategoryConfig.getMapping().get(editing.getMappedId());
            if (this.getType().equals(EditorExpectationType.CATEGORY_PET_LIST_ADD)) {
                config.addPet(pet);
            }
            else {
                config.removePet(pet);
            }
            return true;
        }
        else if (this.getType().equals(EditorExpectationType.PETFOOD_PET_LIST_ADD) ||
                this.getType().equals(EditorExpectationType.PETFOOD_PET_LIST_REMOVE)) {
            final Pet pet = Pet.getFromId(this.value + "");
            if (pet == null)
                return false;

            final EditorEditing editing = EditorEditing.get(creator);
            final String key = editing.getMappedId();
            final PetFoodConfig config = PetFoodConfig.getInstance();
            if (this.getType().equals(EditorExpectationType.PETFOOD_PET_LIST_ADD)) {
                config.addPet(key, pet.getId());
            }
            else {
                config.removePet(key, pet.getId());
            }
            return true;
        }

        final File file = new File(filePath);
        final FileConfiguration config = YamlConfiguration.loadConfiguration(file);


        if (this.getType().equals(EditorExpectationType.ITEM_SECTION_ID)) {
            final EditorEditing editing = EditorEditing.get(creator);
            final String itemId = editing.getMappedId();

            final ItemStack item = config.getItemStack(itemId);

            config.set(itemId, null);
            config.set(this.value.toString(), item);

            try {
                config.save(file);

                editing.setMappedId(this.value.toString());
                ItemsListConfig.reloadInstance();
                return true;
            }
            catch (final IOException ex) {
                return false;
            }
        }
        else if (this.getType().equals(EditorExpectationType.PETFOOD_ID)) {
            final EditorEditing editing = EditorEditing.get(creator);
            final PetFood petFood = PetFood.getFromId(editing.getMappedId());

            PetFoodConfig.getInstance().changePetFoodKey(petFood, this.value.toString());
            editing.setMappedId(this.value.toString());
            return true;
        }
        else {
            if (this.value.equals(RESET_VALUE_TAG) && resetable)
                this.value = null;
            config.set(variablePath.replace("%path%", variablePathPlaceholder), this.value);
        }

        try {
            config.save(file);
        }
        catch (final IOException ignored) {
            return false;
        }
        return true;
    }


    public void toggleBooleanValue() {
        if (this.type == null)
            return;
        if (value == null && this.type.equals(EditorExpectationType.BOOLEAN)) {
            value = true;
        }
        else if (value != null && this.type.equals(EditorExpectationType.BOOLEAN)) {
            value = !(Boolean) value;
        }
    }

    public ItemStack getItem() {
        final ItemStack it = item.clone();
        final ItemMeta meta = it.getItemMeta();
        if (it.getType().equals(Material.FILLED_MAP))
            it.setType(Material.MAP);
        PDCTag.set(meta, editorTag + getId());

        // ロア内の%value%プレースホルダーを実際の値に置き換える
        final List<String> lores = meta.getLore();
        final ArrayList<String> newLores = new ArrayList<>();
        if (lores != null) {
            for (final String lore : lores) {
                if (lore.contains("%value%") && value != null && value instanceof List) {
                    newLores.add(lore.replace("%value%", ""));
                    final List<String> valueAsList = (List<String>) value;
                    if (!valueAsList.isEmpty()) {
                        for (final String entry : valueAsList) {
                            newLores.add("§7 - §e" + entry);
                        }
                    }
                    else {
                        newLores.add("§c空");
                    }

                }
                else {
                    String valueStr = value == null ? "§6デフォルト（未設定）" : value.toString();

                    if (value == null && this.type == EditorExpectationType.BOOLEAN)
                        valueStr = "false";

                    // ロアの整形
                    if (valueStr.equalsIgnoreCase("true"))
                        valueStr = "§a" + value;
                    else if (valueStr.equalsIgnoreCase("false"))
                        valueStr = "§c" + false;

                    if (Utils.isNumeric(valueStr))
                        valueStr = "§b" + valueStr;

                    newLores.add(lore.replace("%value%", valueStr));
                }
            }
        }

        if (resetable) {
            newLores.add(" ");
            newLores.add("§cSHIFT + クリック§7 で値を §cリセット§7 する。");
        }

        meta.setLore(newLores);

        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
        meta.addItemFlags(ItemFlag.HIDE_DYE);
        meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
        it.setItemMeta(meta);
        return it;
    }

    public static EditorItems getFromItemstack(final ItemStack it) {
        if (it == null || !it.hasItemMeta())
            return null;

        final String localName = PDCTag.get(it.getItemMeta());
        // エディタタグがないアイテムはエディタアイテムではない
        if (localName == null || !localName.contains(editorTag))
            return null;

        final String id = localName.replace(editorTag, "");

        return Arrays.stream(EditorItems.values()).filter(editorItems -> editorItems.getId().equals(id)).findFirst().orElse(null);
    }

    public EditorItems setValue(final Object any) {
        this.value = any;
        return this;
    }

    /**
     * アイテムビルダーメソッド
     */
    private static ItemStack UNKNOWN() {
        final ItemStack it = new ItemStack(Material.BARRIER);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§c不明なアイテム");
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack FILLER() {
        final ItemStack it = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§0");
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack BACK_TO_ITEM(final String where) {
        final ItemStack it = new ItemStack(Material.PAPER);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§c" + where + "に戻る");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7クリックして" + where + "に戻る。");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    /**
     * メニュー選択アイコン
     */
    private static ItemStack CONFIG_EDITOR() {
        final ItemStack it = new ItemStack(Material.MOJANG_BANNER_PATTERN);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6設定を編集");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7クリックしてコンフィグオプションを編集する。");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR() {
        final ItemStack it = new ItemStack(Material.MAGMA_CUBE_SPAWN_EGG);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6ペットを編集");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7クリックしてペットを編集・作成する。");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CATEGORY_EDITOR() {
        final ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6カテゴリを編集");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7クリックしてカテゴリを編集・作成する。");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack ITEM_EDITOR() {
        final ItemStack it = new ItemStack(Material.EMERALD);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6アイテムを編集");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7クリックしてアイテムを編集・追加する。");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PETFOOD_EDITOR() {
        final ItemStack it = new ItemStack(Material.COOKED_CHICKEN);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6ペットフードを編集");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7クリックしてペットフードを編集・追加する。");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    /**
     * コンフィグ編集アイコン
     */
    private static ItemStack CONFIG_EDITOR_PREFIX() {
        final ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6プレフィックス");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7プラグインのプレフィックスを編集する。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_DEFAULT_NAME() {
        final ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6デフォルトペット名");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7名前が設定されていない場合の");
        lores.add("§7ペットのデフォルト名を設定する。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_USE_DEFAULT_MYTHICMOBS_NAMES() {
        final ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6デフォルトMythicMobs名を使用");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7MythicMobsの名前をデフォルト名として");
        lores.add("§7表示するかどうかを設定する。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_OVERRIDE_DEFAULT_NAME() {
        final ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6デフォルト名を上書き");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7名前が空の場合にペットをリネームすると");
        lores.add("§7デフォルト名を上書きするかどうか。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_RIGHT_CLICK_TO_OPEN_MENU() {
        final ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6右クリックでメニューを開く");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7右クリックでメニューを開くかどうか。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_LEFT_CLICK_TO_OPEN_MENU() {
        final ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6左クリックでメニューを開く");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7左クリックでメニューを開くかどうか。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_SNEAKMODE() {
        final ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6スニークモードでメニューを開く");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7インタラクション時にスニークが必要かどうか。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_NAMEABLE() {
        final ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6名前変更可否");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7すべてのペットにカスタム名を設定できるかどうか。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_MOUNTABLE() {
        final ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6デフォルト名を上書き");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7機能が有効でペットに設定済みの場合、");
        lores.add("§7すべてのペットをデフォルトでマウント可能にするかどうか。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_DISTANCE_TELEPORT() {
        final ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6テレポートまでの距離");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7ペットがオーナーのもとへテレポートするまでの");
        lores.add("§7最小距離を設定する。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_MAX_NAME_LENGTH() {
        final ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6最大名前文字数");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7カスタムペット名の最大文字数を設定する。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_INVENTORY_SIZE() {
        final ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6召喚インベントリサイズ");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7インベントリサイズを指定値に設定する。");
        lores.add("§7-1 で自動サイズ調整。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_ENABLE_CLICK_BACK_TO_MENU() {
        final ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6カテゴリメニューでクリックして戻る機能を有効化");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7カテゴリメニュー外をクリックすると");
        lores.add("§7カテゴリ選択画面に戻るかどうか。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_ACTIVATE_BACK_MENU_ICON() {
        final ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6メニューに戻るアイコンを表示");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7インタラクションメニューに");
        lores.add("§7「メニューに戻る」アイコンを表示するかどうか。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_DISMOUNT_ON_DAMAGED() {
        final ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6ダメージ時に下馬");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7ダメージを受けたときに");
        lores.add("§7プレイヤーをマウントから降ろすかどうか。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_PERCENT_HEALTH_ON_RESPAWN() {
        final ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6リスポーン時の体力割合");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7死亡後リスポーン時に回復する");
        lores.add("§7体力の割合（リビングペット）。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_AUTO_SAVE_DELAY() {
        final ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6データベース自動保存遅延");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7データベースを定期保存するまでの");
        lores.add("§7間隔を設定する。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_DEFAULT_RESPAWN_COOLDOWN() {
        final ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6デフォルトリスポーンクールダウン");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7デフォルトでペットが復活するまでの");
        lores.add("§7時間を設定する（リビングペット）。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_GLOBAL_RESPAWN_COOLDOWN() {
        final ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6グローバルリスポーンクールダウン");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7クールダウン中はどのペットも");
        lores.add("§7召喚できないようにするかどうか。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_GLOBAL_AUTORESPAWN() {
        final ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6自動リスポーン");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7ペットが死亡後に復活したとき、");
        lores.add("§7自動的にプレイヤーのそばにリスポーンするかどうか。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_DISABLE_INVENTORY_WHILE_SIGNAL_STICK() {
        final ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6シグナルスティック中インベントリ無効");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7シグナルスティックでペットをクリックした際に");
        lores.add("§7インベントリにアクセスできるかどうか。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    /**
     * ペットエディタアイコン
     */
    public EditorItems setupPetIcon(final String petId) {
        final Pet pet = PetConfig.loadConfigPet(petId);

        ItemStack it = pet.getIcon().clone();
        if (value != null && value instanceof ItemStack) {
            it = ((ItemStack) value).clone();
        }
        final ItemMeta meta = it.getItemMeta();

        List<String> og_lores = it.getItemMeta().getLore();
        if (og_lores == null)
            og_lores = new ArrayList<>();

        final ArrayList<String> lores = (ArrayList<String>) og_lores;
        lores.add(" ");
        lores.add("§eクリックしてこのペットを編集する。");

        meta.setLore(lores);
        it.setItemMeta(meta);

        this.item = it;

        return this;
    }

    public EditorItems setupPetIconEdit(final String petId) {
        final Pet pet = PetConfig.loadConfigPet(petId);
        ItemStack it = pet.getIcon().clone();
        if (value != null && value instanceof ItemStack) {
            it = (ItemStack) ((ItemStack) value).clone();
        }
        final ItemMeta meta = it.getItemMeta();

        List<String> og_lores = it.getItemMeta().getLore();
        if (og_lores == null)
            og_lores = new ArrayList<>();

        if (og_lores.contains("§eアイテムをクリックしてアイコンを変更する"))
            return this;

        final ArrayList<String> lores = (ArrayList<String>) og_lores;
        lores.add(" ");
        lores.add("§eアイテムをクリックしてアイコンを変更する");
        lores.add("§eペットアイコンを置き換える。");

        meta.setLore(lores);
        it.setItemMeta(meta);

        this.item = it;

        return this;
    }

    public EditorItems setupSignalStickItem(final String petId) {
        final Pet pet = PetConfig.loadConfigPet(petId);
        ItemStack it = pet.getSignalStick().clone();
        if (value != null && value instanceof ItemStack) {
            it = (ItemStack) ((ItemStack) value).clone();
        }
        final ItemMeta meta = it.getItemMeta();
        if (meta.getDisplayName().equals("§c未定義"))
            meta.setDisplayName("§6シグナルスティック");

        List<String> og_lores = it.getItemMeta().getLore();
        if (og_lores == null)
            og_lores = new ArrayList<>();

        final ArrayList<String> lores = (ArrayList<String>) og_lores;
        lores.add(" ");
        lores.add("§eアイテムをクリックして");
        lores.add("§eシグナルスティックを置き換える。");

        meta.setLore(lores);
        it.setItemMeta(meta);

        this.item = it;

        return this;
    }

    private static ItemStack PAGE_SELECTOR() {
        final ItemStack it = new ItemStack(Material.ARROW);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§aページ選択");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§c左§7クリックで前のページへ");
        lores.add("§a右§7クリックで次のページへ");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CREATE_NEW_ITEM(final String what, final Material type) {
        final ItemStack it = new ItemStack(type);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§a新しい" + what + "を作成");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7クリックして新しい" + what + "を作成する。");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack DELETE(final String what) {
        final ItemStack it = new ItemStack(Material.BARRIER);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§c" + what + "を削除");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§cSHIFT§7 + クリックして" + what + "を削除する。");
        lores.add(" ");
        lores.add("§c§l警告: この操作は取り消せません。");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_MYTHICMOB() {
        final ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6MythicMob");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7ペットを管理するMythicMobを設定する。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_PERMISSION() {
        final ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6権限");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7ペットをアンロックするための");
        lores.add("§7権限を設定する。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_MOUNTABLE() {
        final ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6マウント可否");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7ペットにマウント可能かどうか。");
        lores.add("§7Blockbenchファイルに \"mount\" ボーンが");
        lores.add("§7必要です（wikiを確認）。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_MOUNT_TYPE() {
        final ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6マウントタイプ");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7ペットのマウントタイプを設定する。");
        lores.add("§7マウント可能なペットのみ有効。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_DESPAWN_ON_DISMOUNT() {
        final ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6下馬時にデスポーン");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7ペットがデスポーンしたときに");
        lores.add("§7プレイヤーを下馬させるかどうか。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_AUTORIDE() {
        final ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6自動乗車");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7ペットがスポーンしたときに");
        lores.add("§7プレイヤーが自動的に乗るかどうか。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_MOUNT_PERMISSION() {
        final ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6マウント権限");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7マウントを許可する権限を設定する");
        lores.add("§7（マウント有効時）");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_DESPAWN_SKILL() {
        final ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6デスポーンスキル");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7オプションのデスポーンスキルを設定する。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_DISTANCE() {
        final ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6帰還距離");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7ペットがオーナーのもとへ戻り始める");
        lores.add("§7最小距離を設定する。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_SPAWN_RANGE() {
        final ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6スポーン範囲");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7ペットがスポーンされる");
        lores.add("§7範囲の半径を設定する。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_COMING_BACK_RANGE() {
        final ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6接近帰還距離");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7オーナーに十分近づいたときに");
        lores.add("§7ペットが止まる距離を設定する。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_INVENTORY_SIZE() {
        final ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6インベントリサイズ");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7ペットインベントリのサイズを設定する。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_TAMING_PROGRESS_SKILL() {
        final ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6テイム - 進捗スキル");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7プレイヤーがペットをテイムしているときの");
        lores.add("§7スキルを設定する。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_TAMING_FINISHED_SKILL() {
        final ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6テイム - 完了スキル");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7プレイヤーがペットのテイムを");
        lores.add("§7完了したときのスキルを設定する。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_ICON() {
        final ItemStack it = new ItemStack(Material.END_CRYSTAL);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6MythicMob");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7ペットを表すアイコンを設定する。");
        lores.add(" ");
        lores.add("§7インベントリのアイテムをクリックして");
        lores.add("§7現在のアイテムを変更する。");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_SIGNALS() {
        final ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6シグナル");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7シグナルスティックで発動できる");
        lores.add("§7シグナルの一覧を設定する。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_SIGNAL_STICK() {
        final ItemStack it = new ItemStack(Material.BLAZE_ROD);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6シグナルスティック");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7シグナルスティックのアイテムを設定する。");
        lores.add(" ");
        lores.add("§7インベントリのアイテムをクリックして");
        lores.add("§7現在のアイテムを変更する。");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_GET_SIGNAL_STICK_FROM_MENU() {
        final ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6メニューからシグナルスティック");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7インタラクションメニューから直接");
        lores.add("§7シグナルスティックにアクセスできるかどうか。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_SKINS() {
        final ItemStack it = new ItemStack(Material.LEATHER);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6スキン");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7このペットのスキンを追加・削除する。");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_LEVELS() {
        final ItemStack it = new ItemStack(Material.EXPERIENCE_BOTTLE);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6リビングペット機能");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7リビングペット機能を追加・編集する。");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    /**
     * ペットエディタ レベルアイコン
     */
    public EditorItems setupPetLevelIcon(final String petId, final String levelId) {
        final Pet pet = PetConfig.loadConfigPet(petId);
        final PetLevel level = pet.getPetLevels().stream().filter(petLevel -> petLevel.getLevelId().equals(levelId)).findFirst().orElse(null);

        final ItemStack it = new ItemStack(Material.EXPERIENCE_BOTTLE);
        final ItemMeta meta = it.getItemMeta();

        meta.setDisplayName("§a" + level.getLevelName());
        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7経験値閾値: §a" + level.getExpThreshold());
        lores.add(" ");
        lores.add("§eクリックしてこのレベルを編集する。");

        meta.setLore(lores);
        it.setItemMeta(meta);

        this.item = it;

        this.value = level;

        return this;
    }

    private static ItemStack PET_EDITOR_LEVEL_NAME() {
        final ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6レベル名");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7レベルの表示名を設定する。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_LEVEL_EXP_THRESHOLD() {
        final ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6経験値閾値");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7そのレベルに到達するための");
        lores.add("§7最小経験値を設定する。");
        lores.add("§c最初のレベルは 0 XP から始まる");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_LEVEL_MAX_HEALTH() {
        final ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6最大体力");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7ペットの体力を設定する。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_LEVEL_REGENERATION() {
        final ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6再生");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7時間経過による体力の再生量を設定する。");
        lores.add("§c（体力/秒）");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_LEVEL_RESISTANCE_MODIFIER() {
        final ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6耐性補正");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7受けるダメージを何で割るかを設定する。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_LEVEL_DAMAGE_MODIFIER() {
        final ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6ダメージ補正");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7ペットが与えるダメージの倍率を設定する。");
        lores.add("§c自動適用ではなく MythicMobs の");
        lores.add("§cプレースホルダーとして使用する（wiki参照）");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_LEVEL_POWER() {
        final ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6パワー補正");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7ペットのスペルパワーの倍率を設定する。");
        lores.add("§c自動適用ではなく MythicMobs の");
        lores.add("§cプレースホルダーとして使用する（wiki参照）");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_LEVEL_COOLDOWN_RESPAWN() {
        final ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6クールダウン - リスポーン");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7死亡後にペットを再召喚できるまでの");
        lores.add("§7時間を設定する。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_LEVEL_COOLDOWN_REVOKE() {
        final ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6クールダウン - 呼び戻し");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7呼び戻し後にペットを再召喚できるまでの");
        lores.add("§7時間を設定する。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_LEVEL_INVENTORY_EXTENSION() {
        final ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6インベントリ拡張");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7そのレベルでペットインベントリに");
        lores.add("§7追加されるスロット数を設定する。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_LEVEL_ANNOUNCEMENT_TEXT() {
        final ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6アナウンス - テキスト");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7ペットが進化したときに");
        lores.add("§7アナウンスするテキストを設定する。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_LEVEL_ANNOUNCEMENT_TYPE() {
        final ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6アナウンス - タイプ");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7アナウンスのタイプを設定する。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_LEVEL_ANNOUNCEMENT_SKILL() {
        final ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6アナウンス - スキル");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7ペットが進化したときに");
        lores.add("§7発動するスキルを設定する。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_LEVEL_EVOLUTION_PET_ID() {
        final ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6進化 - ペットID");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7進化先のペットIDを設定する。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_LEVEL_EVOLUTION_DELAY() {
        final ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6進化 - 遅延");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7進化が発動するまでの遅延を設定する");
        lores.add("§7（スキルの持続時間と同様、tick単位）。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_LEVEL_EVOLUTION_REMOVE_ACCESS() {
        final ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6進化 - 旧アクセス削除");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7進化時に前のペットの権限を");
        lores.add("§7削除するかどうか（trueを推奨）");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    /**
     * ペットエディタ スキンアイコン
     */
    public EditorItems setupSkinIcon(final String petId, final String skinId) {

        final Pet pet = PetConfig.loadConfigPet(petId);
        final PetSkin skin = PetSkin.getSkins(pet).stream().filter(petSkin -> petSkin.getPathId().equals(skinId)).findFirst().orElse(null);

        ItemStack it = new ItemStack(Material.LEATHER);
        if (skin.getIcon() != null)
            it = skin.getIcon().clone();

        final ItemMeta meta = it.getItemMeta();

        meta.setDisplayName("§6スキン: §e" + skin.getMythicMobId());
        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§eクリックしてこのスキンを編集する。");

        meta.setLore(lores);

        it.setItemMeta(meta);

        this.item = it;

        this.value = skin;

        return this;
    }

    public EditorItems setupEditSkinIcon(final String petId, final String skinId) {

        final Pet pet = PetConfig.loadConfigPet(petId);
        final PetSkin skin = PetSkin.getSkins(pet).stream().filter(petSkin -> petSkin.getPathId().equals(skinId)).findFirst().orElse(null);

        ItemStack it = new ItemStack(Material.LEATHER);
        if (skin.getIcon() != null)
            it = skin.getIcon().clone();

        final ItemMeta meta = it.getItemMeta();

        meta.setDisplayName("§6スキン: §e" + skin.getMythicMobId());
        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§eアイテムをクリックして");
        lores.add("§eスキンのアイコンを変更する。");

        meta.setLore(lores);
        it.setItemMeta(meta);

        this.item = it;

        this.value = skin;

        return this;
    }

    private static ItemStack PET_EDITOR_SKIN_MYTHICMOB() {
        final ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6スキン - MythicMob");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7スキンとして適用するMythicMobを設定する");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_SKIN_PERMISSION() {
        final ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6スキン - 権限");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7スキンをアンロックするための");
        lores.add("§7権限を設定する。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }


    /**
     * カテゴリアイコン
     */
    public EditorItems setupCategoryIcon(final String categoryId) {
        final Category category = CategoryConfig.loadConfigCategory(categoryId);
        ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        if (category.getIcon() != null)
            it = category.getIcon().clone();

        final ItemMeta meta = it.getItemMeta();

        meta.setDisplayName("§6カテゴリ: §e" + category.getIconName());
        final ArrayList<String> lores = new ArrayList<>();

        lores.add("§eクリックしてカテゴリを編集する。");

        meta.setLore(lores);
        it.setItemMeta(meta);

        this.item = it;

        this.value = category;

        return this;
    }


    public EditorItems setupEditCategoryIcon(final String categoryId) {
        final Category category = CategoryConfig.loadConfigCategory(categoryId);
        ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        if (category.getIcon() != null)
            it = category.getIcon().clone();

        final ItemMeta meta = it.getItemMeta();

        meta.setDisplayName("§6カテゴリ: §e" + category.getIconName());
        final ArrayList<String> lores = new ArrayList<>();

        lores.add("§a除外カテゴリ:");
        if (category.getExcludedCategoriesId().size() == 0)
            lores.add("§7- §6なし");
        for(final String excludedCategoryId : category.getExcludedCategoriesId())
            lores.add("§7- " + excludedCategoryId);

        lores.add(" ");

        if (category.isDefaultCategory()) {
            lores.add("§aすべてのペットを含む §7(デフォルトカテゴリ)");
        }
        else {
            lores.add("§a含まれるペット:");
            if (category.getPets().size() == 0)
                lores.add("§7- §6なし");
            for(final Pet pet : category.getPets())
                lores.add(" §7 - " + pet.getId());
        }

        lores.add(" ");

        lores.add("§eアイテムをクリックして");
        lores.add("§eカテゴリのアイコンを変更する。");

        meta.setLore(lores);
        it.setItemMeta(meta);

        this.item = it;

        this.value = category;

        return this;
    }

    private static ItemStack CATEGORY_EDITOR_CATEGORY_EDIT_ID() {
        final ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6カテゴリID");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7カテゴリIDを編集する。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CATEGORY_EDITOR_CATEGORY_EDIT_DEFAULT_CATEGORY() {
        final ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6デフォルトカテゴリ");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7（オプション）すべてのペットを");
        lores.add("§7デフォルトでこのカテゴリに入れるか？");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }


    private static ItemStack CATEGORY_EDITOR_CATEGORY_EDIT_EXCLUDED_CATEGORIES() {
        final ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6除外カテゴリ");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§a（オプション）§7指定したカテゴリの");
        lores.add("§7すべてのペットを除外する。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CATEGORY_EDITOR_CATEGORY_EDIT_PET_ADD() {
        final ItemStack it = new ItemStack(Material.GOLD_INGOT);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§aペットを§6追加");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7カテゴリにペットを追加する。");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CATEGORY_EDITOR_CATEGORY_EDIT_PET_REMOVE() {
        final ItemStack it = new ItemStack(Material.NETHER_BRICK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§cペットを§6削除");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7カテゴリからペットを削除する。");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CATEGORY_EDITOR_CATEGORY_EDIT_TITLE_NAME() {
        final ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6カテゴリインベントリタイトル");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7GUIに表示するカテゴリ");
        lores.add("§7インベントリのタイトルを設定する。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CATEGORY_EDITOR_CATEGORY_EDIT_ICON_NAME() {
        final ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6カテゴリアイコン名");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7GUIに表示するカテゴリの");
        lores.add("§7アイコン名を設定する。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    /**
     * アイテムエディタ
     */
    public EditorItems setupItemIcon(final String itemId) {
        ItemStack it = new ItemStack(Material.BEDROCK);
        final ItemStack loadedIt = ItemsListConfig.loadConfigItem(itemId);
        if (loadedIt != null)
            it = loadedIt.clone();

        final ItemMeta meta = it.getItemMeta();

        final ArrayList<String> lores = new ArrayList<>();

        lores.add(" ");
        lores.add("§aId: §b" + itemId);
        lores.add(" ");
        lores.add("§eクリックしてこのアイテムを編集する。");

        meta.setLore(lores);
        it.setItemMeta(meta);

        this.item = it;

        this.value = itemId;

        return this;
    }

    public EditorItems setupEditItemIcon(final String itemId) {
        ItemStack it = new ItemStack(Material.BEDROCK);
        final ItemStack loadedIt = ItemsListConfig.loadConfigItem(itemId);
        if (loadedIt != null)
            it = loadedIt.clone();

        final ItemMeta meta = it.getItemMeta();

        ArrayList<String> lores = new ArrayList<>();
        if (it.getItemMeta().hasLore() && it.getItemMeta().getLore() != null)
        {
            lores = (ArrayList<String>) it.getItemMeta().getLore();
        }

        lores.add(" ");
        lores.add("§eアイテムをクリックして変更する。");

        meta.setLore(lores);
        it.setItemMeta(meta);

        this.item = it;

        this.value = itemId;

        return this;
    }

    private static ItemStack ITEMS_EDIT_ID() {
        final ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6アイテムID");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7アイテムのIDを編集する。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    /**
     * ペットフードエディタ
     */
    public EditorItems setupPetfoodIcon(final String petFoodId) {
        ItemStack it = new ItemStack(Material.BEDROCK);
        final PetFood petFood = PetFoodConfig.loadConfigPetFood(petFoodId);
        if (petFood.getItemStack() != null)
            it = petFood.getItemStack().clone();

        final ItemMeta meta = it.getItemMeta();

        final ArrayList<String> lores = new ArrayList<>();

        lores.add(" ");
        lores.add("§aId: §b" + petFood.getId());
        lores.add(" ");
        lores.add("§eクリックしてこのペットフードを編集する。");

        meta.setLore(lores);
        it.setItemMeta(meta);

        this.item = it;

        this.value = petFood;

        return this;
    }

    public EditorItems setupEditPetFoodIcon(final String petFoodId) {
        ItemStack it = new ItemStack(Material.BEDROCK);
        final PetFood petFood = PetFoodConfig.loadConfigPetFood(petFoodId);
        if (petFood.getItemStack() != null)
            it = petFood.getItemStack().clone();

        final ItemMeta meta = it.getItemMeta();

        ArrayList<String> lores = new ArrayList<>();
        if (it.getItemMeta().hasLore() && it.getItemMeta().getLore() != null)
        {
            lores = (ArrayList<String>) it.getItemMeta().getLore();
        }

        lores.add(" ");
        lores.add("§eアイテムをクリックして変更する。");

        meta.setLore(lores);
        it.setItemMeta(meta);

        this.item = it;

        this.value = petFood;

        return this;
    }

    private static ItemStack PETFOOD_EDITOR_EDIT_ID() {
        final ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6ペットフードID");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7ペットフードのIDを編集する。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    public EditorItems setupPetFoodEditorEditItem(final String petFoodId) {
        final PetFood petFood = PetFoodConfig.loadConfigPetFood(petFoodId);
        ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        if (petFood.getItemStack() != null)
            it = petFood.getItemStack().clone();
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6フードアイテム");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7フードアイテムを設定する。");
        lores.add("§7登録済みの §aペットアイテム§7（より細かいカスタマイズ可能）");
        lores.add("§7または任意の §bMATERIAL§7 を使用できる。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);

        this.item = it;
        return this;
    }

    private static ItemStack PETFOOD_EDITOR_EDIT_TYPE() {
        final ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6ペットフードタイプ");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7ペットフードのタイプを設定する（wiki参照）。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PETFOOD_EDITOR_EDIT_POWER() {
        final ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6パワー値");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7ペットフードのパワーを設定する。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PETFOOD_EDITOR_EDIT_DURATION() {
        final ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6バフの持続時間");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7ペットフードがバフの場合、");
        lores.add("§7効果の持続時間（tick単位）を設定する。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PETFOOD_EDITOR_EDIT_OPERATOR() {
        final ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6演算子");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7フードのパワーに適用する");
        lores.add("§7数学的演算を設定する（wiki参照）。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PETFOOD_EDITOR_EDIT_SIGNAL() {
        final ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6シグナル");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7ペットにフードを与えたときに");
        lores.add("§7発動するシグナルを設定する。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PETFOOD_EDITOR_EDIT_PETS_ADD() {
        final ItemStack it = new ItemStack(Material.GOLD_INGOT);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§aペットを§6追加");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§a対応ペットを追加§7する。§a（オプション）");
        lores.add(" ");
        lores.add("§7現在の対象ペット: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PETFOOD_EDITOR_EDIT_PETS_REMOVE() {
        final ItemStack it = new ItemStack(Material.NETHER_BRICK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§cペットを§6削除");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§c対応ペットを削除§7する。§a（オプション）");
        lores.add(" ");
        lores.add("§7現在の対象ペット: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PETFOOD_EDITOR_EDIT_EVOLUTION() {
        final ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6進化");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§a（オプション）§7ペットがフードを食べたときに");
        lores.add("§7発動する進化を設定する。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PETFOOD_EDITOR_EDIT_EXP_THRESHOLD() {
        final ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6経験値閾値");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7フードをペットが食べられるようになる");
        lores.add("§7経験値の閾値を設定する。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PETFOOD_EDITOR_EDIT_DELAY() {
        final ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6進化前の遅延");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7進化フードを使用した場合、");
        lores.add("§7進化が発動するまでの時間を設定する。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PETFOOD_EDITOR_EDIT_PERMISSION() {
        final ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6権限");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7ペットフードを使用するための権限。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PETFOOD_EDITOR_EDIT_UNLOCKED_PET() {
        final ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6アンロックされるペット");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7フードタイプが §aUNLOCK§7 の場合、");
        lores.add("§7アンロックされるペットを設定する。");
        lores.add(" ");
        lores.add("§7現在の値: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }
}
