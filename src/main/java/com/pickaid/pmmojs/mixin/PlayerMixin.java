package com.pickaid.pmmojs.mixin;

import com.pickaid.pmmojs.utils.SkillHelper;
import dev.latvian.mods.rhino.util.RemapForJS;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Player.class)
public class PlayerMixin {
    Player self = (Player) (Object) this;

    @Unique
    @RemapForJS("getSkillLevel")
    public int pmmojs$getSkillLevel(String skillName) {
        return SkillHelper.getLevel(self, skillName);
    }

    @Unique
    @RemapForJS("setSkillLevel")
    public void pmmojs$setSkillLevel(String skillName, Number level) {
        SkillHelper.setLevel(self, skillName, level);
    }

    @Unique
    @RemapForJS("getSkillXp")
    public long pmmojs$getSkillXp(String skillName) {
        return SkillHelper.getXp(self, skillName);
    }

    @Unique
    @RemapForJS("setSkillXp")
    public void pmmojs$setSkillXp(String skillName, long xp) {
        SkillHelper.setXp(self, skillName, xp);
    }

    @Unique
    @RemapForJS("addSkillXp")
    public void pmmojs$addSkillXp(String skillName, Number amount) {
        SkillHelper.addXP(self, skillName, amount);
    }
}
