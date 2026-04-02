package com.pickaid.pmmojs.kubejs.handlers.server;

import com.pickaid.pmmojs.kubejs.PMMOKubeJSEvents;
import com.pickaid.pmmojs.kubejs.events.server.api.PMMOInternalEventJS;
import com.pickaid.pmmojs.kubejs.events.server.api.PMMOInternalType;
import harmonised.pmmo.api.APIUtils;
import harmonised.pmmo.api.enums.EventType;
import harmonised.pmmo.config.Config;
import harmonised.pmmo.core.Core;
import harmonised.pmmo.features.party.PartyUtils;
import harmonised.pmmo.util.TagUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.brewing.PlayerBrewedPotionEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.event.level.PistonEvent;
import net.minecraftforge.event.level.SleepFinishedTimeEvent;
import net.minecraftforge.eventbus.api.Event;

import java.util.LinkedHashMap;
import java.util.Map;

public final class PMMOInternalHandlerBridge {
    private static final String BREWED = "brewXpAwarded";
    private static final String ALREADY_TRACKED = "alreadyTracked";
    private static final String MARK_BREWED = "markBrewed";

    private PMMOInternalHandlerBridge() {
    }

    public static boolean handleDimensionTravel(EntityTravelToDimensionEvent event) {
        if (event.getEntity().level().isClientSide()) {
            return false;
        }

        return postInternal(PMMOInternalType.DIMENSION_TRAVEL, event, null).shouldSkipPmmo();
    }

    public static boolean handleMount(EntityMountEvent event) {
        if (event.getEntityMounting().level().isClientSide()) {
            return false;
        }

        return postInternal(PMMOInternalType.MOUNT, event, null).shouldSkipPmmo();
    }

    public static boolean handlePlayerDeath(LivingDeathEvent event) {
        if (event.getEntity().level().isClientSide()) {
            return false;
        }

        return postInternal(PMMOInternalType.PLAYER_DEATH, event, null).shouldSkipPmmo();
    }

    public static boolean handleExplosion(ExplosionEvent.Detonate event) {
        if (event.getLevel().isClientSide()) {
            return false;
        }

        return postInternal(PMMOInternalType.EXPLOSION, event, internalEvent -> {
            internalEvent.putContextInt("affectedBlockCount", event.getAffectedBlocks().size());
            internalEvent.putContextInt("affectedEntityCount", event.getAffectedEntities().size());
        }).shouldSkipPmmo();
    }

    public static boolean handleLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity().level().isClientSide()) {
            return false;
        }

        return postInternal(PMMOInternalType.LOGIN, event, null).shouldSkipPmmo();
    }

    public static boolean handlePiston(PistonEvent.Pre event) {
        if (event.getLevel().isClientSide()) {
            return false;
        }

        return postInternal(PMMOInternalType.PISTON, event, null).shouldSkipPmmo();
    }

    public static boolean handleSleep(SleepFinishedTimeEvent event) {
        if (event.getLevel().isClientSide()) {
            return false;
        }

        return postInternal(PMMOInternalType.SLEEP_FINISHED, event, null).shouldSkipPmmo();
    }

    public static boolean handlePotionBrew(PlayerBrewedPotionEvent event) {
        if (event.getEntity().level().isClientSide()) {
            return false;
        }

        ItemStack stack = event.getStack();
        boolean brewingTracked = Config.BREWING_TRACKED.get();
        boolean alreadyTracked = brewingTracked && stack.getTag() != null && stack.getTag().contains(BREWED);

        if (alreadyTracked) {
            postInternal(PMMOInternalType.POTION_BREW, event, internalEvent -> {
                internalEvent.putContextBoolean(ALREADY_TRACKED, true);
                internalEvent.putContextBoolean(MARK_BREWED, brewingTracked);
            });
            return true;
        }

        Player player = event.getEntity();
        Core core = Core.get(player.level());
        CompoundTag eventHookOutput = PMMOKubeJSEvents.postTrigger(EventType.BREW, event, new CompoundTag()).getMutableContext();
        if (eventHookOutput.getBoolean(APIUtils.IS_CANCELLED)) {
            return true;
        }

        CompoundTag perkOutput = TagUtils.mergeTags(eventHookOutput, core.getPerkRegistry().executePerk(EventType.BREW, player, eventHookOutput));
        Map<String, Long> xpAward = new LinkedHashMap<>(core.getExperienceAwards(EventType.BREW, stack, player, perkOutput));
        PMMOInternalEventJS internalEvent = postInternal(PMMOInternalType.POTION_BREW, event, hookEvent -> {
            hookEvent.putContextBoolean(ALREADY_TRACKED, false);
            hookEvent.putContextBoolean(MARK_BREWED, brewingTracked);
            hookEvent.setXpAwards(xpAward);
        });

        if (internalEvent.shouldSkipPmmo()) {
            return true;
        }

        if (player instanceof ServerPlayer serverPlayer) {
            core.awardXP(PartyUtils.getPartyMembersInRange(serverPlayer), internalEvent.getXpAwards());
        }

        if (internalEvent.getContextBoolean(MARK_BREWED) && stack.getTag() != null) {
            stack.getTag().putBoolean(BREWED, true);
        }

        return true;
    }

    private static PMMOInternalEventJS postInternal(PMMOInternalType type, Event event, java.util.function.Consumer<PMMOInternalEventJS> initializer) {
        return PMMOKubeJSEvents.postInternal(type, event, initializer);
    }
}
