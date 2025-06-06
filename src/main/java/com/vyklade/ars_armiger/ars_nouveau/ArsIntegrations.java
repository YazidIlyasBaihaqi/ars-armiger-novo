package com.vyklade.ars_armiger.ars_nouveau;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.CasterUtil;
import com.hollingsworth.arsnouveau.api.event.SpellCostCalcEvent;
import com.hollingsworth.arsnouveau.common.block.tile.ScribesTile;
import com.hollingsworth.arsnouveau.common.spell.method.MethodTouch;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.vyklade.ars_armiger.ArsArmiger;
import com.vyklade.ars_armiger.tetra.TetraEventHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.items.modular.ModularItem;

import java.util.List;
@Mod.EventBusSubscriber(modid = ArsArmiger.MODID)
public class ArsIntegrations {

    @SubscribeEvent
    public static void onTooltip(@NotNull ItemTooltipEvent event){
        ItemStack item = event.getItemStack();

        if(!(item.getItem() instanceof ModularItem)) return;
        List<Component> tooltip = event.getToolTip();

        int spellstriker = ((ModularItem)item.getItem()).getEffectLevel(item, ItemEffect.get("spellstrike"));
        int sourceLeecch = ((ModularItem)item.getItem()).getEffectLevel(item, ItemEffect.get("source_leech"));
        int spellguard = ((ModularItem)item.getItem()).getEffectLevel(item, ItemEffect.get("spellguard"));

        if(sourceLeecch > 0){
            MutableComponent leech = Component.translatable("tetra.tooltips.leech", sourceLeecch);
            tooltip.add(leech);
        }
        if(spellstriker > 0 || spellguard > 0) {
            if(item.hasTag()) {
                MutableComponent inscribeable = Component.translatable("tetra.tooltips.inscribable");
                if(item.getTag().contains("ars_nouveau:caster")) {
                    int power = ((ModularItem) item.getItem()).getEffectLevel(item, ItemEffect.get("spellstrike_power"));

                    SpellCaster caster = new SpellCaster(item);
                    //SpellCaster caster = new BasicReductionCaster(item,(spell -> { spell.addDiscount(MethodTouch.INSTANCE.getCastingCost()); return  spell;}));
                    Spell spell = caster.getSpell();

                    int air = ((ModularItem) item.getItem()).getEffectLevel(item, ItemEffect.get("air_attunement"));
                    int earth = ((ModularItem) item.getItem()).getEffectLevel(item, ItemEffect.get("earth_attunement"));
                    int fire = ((ModularItem) item.getItem()).getEffectLevel(item, ItemEffect.get("fire_attunement"));
                    int water = ((ModularItem) item.getItem()).getEffectLevel(item, ItemEffect.get("water_attunement"));
                    if(air > 0)
                        TetraEventHandler.Amplify(spell,SpellSchools.ELEMENTAL_AIR, air >= 2);
                    else if (earth > 0)
                       TetraEventHandler.Amplify(spell,SpellSchools.ELEMENTAL_EARTH, earth >= 2);
                    else if (fire > 0)
                        TetraEventHandler.Amplify(spell,SpellSchools.ELEMENTAL_FIRE, fire >= 2);
                    else if (water > 0)
                        TetraEventHandler.Amplify(spell,SpellSchools.ELEMENTAL_WATER, water >= 2);

                    if(power > 0) TetraEventHandler.Power(spell, power);

                    if(spell.isEmpty()) {
                        tooltip.add(inscribeable);
                    }
                    else {
                        tooltip.add(Component.literal(spell.getDisplayString()));
                    }
                }
                else {
                    tooltip.add(inscribeable);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event){
        ItemStack item = event.getItemStack();
        Player player = event.getEntity();

        if(item == ItemStack.EMPTY || player == null) return;
        if(!item.hasTag()) return;
        if(!item.getTag().contains("ars_nouveau:caster")) return;
        if(!player.isCrouching()) return;

        Level level = event.getLevel();
        if(!(level instanceof ServerLevel)) return;
        if(level.isEmptyBlock(event.getPos())) return;

        BlockEntity block = level.getBlockEntity(event.getPos());
        if(block == null) return;
        if(!(block instanceof ScribesTile)) return;

        ScribesTile table = (ScribesTile)block;
        if(table.getStack() == ItemStack.EMPTY) return;

        ItemStack tableItem = table.getStack();

        if(!(tableItem.getItem() instanceof ModularItem)) return;

        int spellstrike = ((ModularItem)tableItem.getItem()).getEffectLevel(tableItem, ItemEffect.get("spellstrike"));
        int spellguard = ((ModularItem)tableItem.getItem()).getEffectLevel(tableItem, ItemEffect.get("spellguard"));
        if(spellstrike == 0 && spellguard == 0) return;

        ISpellCaster caster = CasterUtil.getCaster(item);
        Spell spell = caster.getSpell();
        SpellCaster itemCaster = new SpellCaster(tableItem);

        if(spell.isEmpty()) {
            itemCaster.setSpell(spell);
            PortUtil.sendMessageNoSpam(player, Component.translatable("ars_armiger.spellstrike.cleared"));
            event.setCanceled(true);
            return;
        }

        List<AbstractSpellPart> parts = spell.recipe;

        if(parts.get(0) instanceof AbstractCastMethod) {
            PortUtil.sendMessageNoSpam(player, Component.translatable("ars_armiger.spellstrike.invalid"));
            event.setCanceled(true);
            return;
        }

        parts.add(0,MethodTouch.INSTANCE);
        spell.recipe = parts;

        itemCaster.setColor(caster.getColor());
        itemCaster.setFlavorText(caster.getFlavorText());
        itemCaster.setSpellName(caster.getSpellName());
        itemCaster.setSound(caster.getCurrentSound());
        itemCaster.setSpell(spell);

        PortUtil.sendMessageNoSpam(player, Component.translatable("ars_nouveau.set_spell"));
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void SpellCostCalcEvent(SpellCostCalcEvent event) {
        if(event.context.getCasterTool().getItem() instanceof ModularItem) {
            float spellguard = ((ModularItem) event.context.getCasterTool().getItem()).getEffectEfficiency(event.context.getCasterTool(), ItemEffect.get("spellguard"));
            if(spellguard < 1){
                event.currentCost = (int) (event.currentCost * spellguard);
            } else {
                double cost = ((ModularItem) event.context.getCasterTool().getItem()).getEffectEfficiency(event.context.getCasterTool(), ItemEffect.get("spellstrike_efficiency"));
                event.currentCost = (int) (event.currentCost * cost);
            }
        };
    }
}
