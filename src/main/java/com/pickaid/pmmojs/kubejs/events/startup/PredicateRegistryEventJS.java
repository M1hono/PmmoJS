package com.pickaid.pmmojs.kubejs.events.startup;

import dev.latvian.mods.kubejs.event.StartupEventJS;
import dev.latvian.mods.kubejs.typings.Info;
import harmonised.pmmo.api.APIUtils;
import harmonised.pmmo.api.enums.ReqType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.function.BiPredicate;

public class PredicateRegistryEventJS extends StartupEventJS {

    @Info("""
            Registers a predicate to be used in determining if a given player is permitted
            to perform a particular action. [Except for break action.
            The ResourceLocation and ReqType parameters are
            conditions for when this check should be applied and are used by PMMO to know
            which predicates apply in which contexts.
            
            @param res the item registryKey
            @param reqType the requirement type
            @param pred what executes to determine if player is permitted to perform the action
            """)
    public void registerActionPredicate(ResourceLocation res, ReqType reqType, BiPredicate<Player, ItemStack> pred) {
        APIUtils.registerActionPredicate(res, reqType, pred);
    }

    @Info("""
            Registers a predicate to be used in determining if a given player is permitted
            to break a block. [Except for action}.
            The ResourceLocation and ReqType parameters are
            conditions for when this check should be applied and are used by PMMO to know
            which predicates apply in which contexts.
            
            @param res the block registryKey
            @param reqType the requirement type
            @param pred what executes to determine if player is permitted to perform the action
            """)
    public void registerBreakPredicate(ResourceLocation res, ReqType reqType, BiPredicate<Player, BlockEntity> pred) {
        APIUtils.registerBreakPredicate(res, reqType, pred);
    }

    @Info("""
        Registers a predicate to be used in determining if a given player is permitted
        to perform a particular action related to an entity. [Except for break action}.
        The ResourceLocation and ReqType parameters are
        conditions for when this check should be applied and are used by PMMO to know
        which predicates apply in which contexts.
        
        @param res the entity registrykey
        @param reqType the requirement type
        @param pred what executes to determine if player is permitted to perform the action
        """)
    public void registerEntityPredicate(ResourceLocation res, ReqType reqType, BiPredicate<Player, Entity> pred) {
        APIUtils.registerEntityPredicate(res, reqType, pred);
    }
}