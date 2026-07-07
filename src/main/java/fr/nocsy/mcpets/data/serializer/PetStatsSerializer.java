package fr.nocsy.mcpets.data.serializer;

import com.google.gson.Gson;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.livingpets.PetLevel;
import fr.nocsy.mcpets.data.livingpets.PetStats;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Base64;
import java.util.UUID;

public class PetStatsSerializer {

    @Getter
    // Reference to the actual pet
    private String petId;
    @Getter
    private UUID petOwner;
    @Getter
    // Handles the health of the Pet
    private double currentHealth;
    @Getter
    // Handles the experience of the pet
    private double experience;
    @Getter
    // Handles the levels
    private String levelId;
    
    private PetStatsSerializer(String petId,
                              UUID petOwner,
                              double currentHealth,
                              double experience,
                              String levelId) {
        this.petId = petId;
        this.petOwner = petOwner;
        this.currentHealth = currentHealth;
        this.experience = experience;
        this.levelId = levelId;
    }

    /**
     * Build the serializer from the pet stats instance
     */
    public static PetStatsSerializer build(@NotNull PetStats stats) {
        return new PetStatsSerializer(stats.getPet().getId(),
                                    stats.getPet().getOwner(),
                                    stats.getCurrentHealth(),
                                    stats.getExperience(),
                                    stats.getCurrentLevel().getLevelId());
    }

    /**
     * Rebuild the PetStats from the serialized
     */
    public PetStats buildStats() {
        Pet pet = Pet.getFromId(petId);
        if (pet == null)
            return null;
        pet.setOwner(petOwner);

        final boolean hasLevels = pet.getPetLevels() != null && !pet.getPetLevels().isEmpty();
        // レベル定義を持たないペット(MMOCore駆動)は保存されたlevelIdが一致するはずが無いので、
        // 動的レベルを作り直す。実際のステータス値は次回召喚/レベル・クラス変更時に再計算される。
        PetLevel currentLevel = hasLevels
                ? pet.getPetLevels().stream()
                        .filter(level -> level.getLevelId().equals(levelId))
                        .findFirst()
                        .orElse(null)
                : PetLevel.createDefault(pet);
        if (currentLevel == null)
            return null;

        return new PetStats(pet, experience, currentHealth, currentLevel);
    }

    /**
     * Get the JSON representation of the pet stats
     */
    public String JSONformatted() {
        return new Gson().toJson(this);
    }

    /**
     * Return a string representation of the object
     */
    public String serialize() {
        String jsonStr = JSONformatted();
        jsonStr = Base64.getEncoder().encodeToString(jsonStr.getBytes());
        return jsonStr;
    }

    /**
     * Returns the object unserialized from the given serialized string
     */
    public static PetStatsSerializer unserialize(String serialized) {
        String decoded = new String(Base64.getDecoder().decode(serialized.getBytes()));
        if (decoded.isEmpty())
            return null;
        return new Gson().fromJson(decoded, PetStatsSerializer.class);
    }
}
