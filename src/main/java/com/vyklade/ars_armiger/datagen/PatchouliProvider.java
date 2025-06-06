package com.vyklade.ars_armiger.datagen;

import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.api.spell.AbstractCastMethod;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.common.datagen.patchouli.*;
import com.vyklade.ars_armiger.ArsArmiger;
import com.vyklade.ars_armiger.ArsNouveauRegistry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;
import se.mickelus.tetra.items.modular.ModularItem;

import java.io.IOException;
import java.nio.file.Path;

import static com.hollingsworth.arsnouveau.setup.registry.RegistryHelper.getRegistryName;

public class PatchouliProvider extends com.hollingsworth.arsnouveau.common.datagen.PatchouliProvider {
    static String root = ArsArmiger.MODID;
    public PatchouliProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    public void collectJsons(CachedOutput cache) {

        for (AbstractSpellPart spell : ArsNouveauRegistry.registeredSpells) {
            addGlyphPage(spell);
        }

        //check the superclass for examples

        var builder = new PatchouliBuilder(EQUIPMENT,"ars_armiger.page.tetra_weapons")
                .withIcon("tetra:modular_sword{\"sword/basic_blade_material\":\"basic_blade/source_gem\",\"sword/basic_hilt_material\":\"basic_hilt/stick\",\"sword/blade\":\"sword/basic_blade\",\"sword/decorative_pommel\":\"decorative_pommel/gold\",\"sword/guard\":\"sword/makeshift_guard\",\"sword/hilt\":\"sword/basic_hilt\",\"sword/makeshift_guard_material\":\"makeshift_guard/gold\",\"sword/pommel\":\"sword/decorative_pommel\",\"sword/blade:spellstrike\":1}")
                .withPage(new SpotlightPage("tetra:holo{\"id\":\"9503de92-046c-4d61-8afe-8c0d71c6afae\",\"holo/core\":\"holo/core\",\"holo/core_material\":\"core/dim\",\"holo/frame\":\"holo/frame\",\"holo/frame_material\":\"frame/ancient\"}").withTitle("ars_armiger.page.tetra_weapons").withText("ars_armiger.page.tetra_weapons.page1.body"))
                .withPage(new SpotlightPage("tetra:modular_sword{\"sword/hilt:hilt/wrap/end_fiber\":0,\"sword/basic_blade_material\":\"basic_blade/source_gem\",\"sword/basic_hilt_material\":\"basic_hilt/archwood\",\"sword/binding_material\":\"sword_binding/blaze_fiber\",\"sword/blade\":\"sword/basic_blade\",\"sword/decorative_pommel_material\":\"decorative_pommel/diamond\",\"sword/guard\":\"sword/binding\",\"sword/hilt\":\"sword/basic_hilt\",\"sword/pommel\":\"sword/decorative_pommel\"}").withTitle("ars_armiger.page.tetra_weapons.page2.title").withText("ars_armiger.page.tetra_weapons.page2.body"))
                .withPage(new SpotlightPage("tetra:modular_single{\"single/head:single/spellstrikefocus_arcane\":0,\"single/handle\":\"single/long_handle\",\"single/head\":\"single/spearhead\",\"single/long_handle_material\":\"long_handle/archwood\",\"single/spearhead_material\":\"spearhead/gold\"}").withTitle("ars_armiger.page.tetra_weapons.page3.title").withText("ars_armiger.page.tetra_weapons.page3.body"))
                .withTextPage("ars_armiger.page.tetra_weapons.page4.body")
                .withPage(new SpotlightPage("tetra:modular_double{\"double/head_right:double/spellstrikefocus_fire\":0,\"double/adze_left_material\":\"adze/gold\",\"double/basic_handle_material\":\"basic_handle/stick\",\"double/butt_right_material\":\"butt/iron\",\"double/handle\":\"double/basic_handle\",\"double/head_left\":\"double/adze_left\",\"double/head_right\":\"double/butt_right\",\"ars_nouveau:caster\":{\"is_hidden\":0,\"current_slot\":0,\"spell_count\":1,\"flavor\":\"\",\"hidden_redipe\":\"\",\"spells\":{\"spell0\":{\"name\":\"\",\"recipe\":{\"size\":3,\"part0\":\"ars_nouveau:glyph_touch\",\"part1\":\"ars_nouveau:glyph_ignite\",\"part2\":\"ars_nouveau:glyph_freeze\"},\"sound\":{\"pitch\":1.0,\"volume\":1.0,\"soundTag\":{}},\"spellColor\":{\"b\":180,\"g\":25,\"r\":255}}}}}").withTitle("ars_armiger.page.tetra_weapons.page5.title").withText("ars_armiger.page.tetra_weapons.page5.body"))
                .withPage(new SpotlightPage("tetra:modular_shield{\"shield/plate:shield/studs/iron\":0,\"shield/plate:shield/trim/iron\":0,\"shield/plate:spellguard\":1,\"shield/basic_grip_material\":\"basic_grip/netherite\",\"shield/boss\":\"shield/sturdy_boss\",\"shield/grip\":\"shield/basic_grip\",\"shield/plate\":\"shield/tower\",\"shield/sturdy_boss_material\":\"sturdy_boss/gold\",\"shield/tower_material\":\"tower/oak\"}").withTitle("ars_armiger.page.tetra_weapons.page6.title").withText("ars_armiger.page.tetra_weapons.page6.body"));


        addPage(new PatchouliPage(builder,getPath(EQUIPMENT,"tetra_weapons")));

        for (PatchouliPage patchouliPage : pages) {
            DataProvider.saveStable(cache, patchouliPage.build(), patchouliPage.path());
        }

    }

    @Override
    public PatchouliPage addBasicItem(ItemLike item, ResourceLocation category, IPatchouliPage recipePage){
        PatchouliBuilder builder = new PatchouliBuilder(category, item.asItem().getDescriptionId())
                .withIcon(item.asItem())
                .withPage(new TextPage(root + ".page." + getRegistryName(item.asItem()).getPath()))
                .withPage(recipePage);
        PatchouliPage page = new PatchouliPage(builder, getPath(category, getRegistryName(item.asItem()).getPath()));
        this.pages.add(page);
        return page;
    }

    public void addFamiliarPage(AbstractFamiliarHolder familiarHolder) {
        PatchouliBuilder builder = new PatchouliBuilder(FAMILIARS, "entity." + root + "." + familiarHolder.getRegistryName().getPath())
                .withIcon(root + ":" + familiarHolder.getRegistryName().getPath())
                .withTextPage(root + ".familiar_desc." + familiarHolder.getRegistryName().getPath())
                .withPage(new EntityPage(familiarHolder.getRegistryName().toString()));
        PatchouliPage page = new PatchouliPage(builder, getPath(FAMILIARS, familiarHolder.getRegistryName().getPath()));
        this.pages.add(page);
    }

    public void addRitualPage(AbstractRitual ritual) {
        PatchouliBuilder builder = new PatchouliBuilder(RITUALS, "item.ars_elemental." + ritual.getRegistryName().getPath())
                .withIcon(ritual.getRegistryName().toString())
                .withTextPage(ritual.getDescriptionKey())
                .withPage(new CraftingPage("ars_elemental:tablet_" + ritual.getRegistryName().getPath()));
        PatchouliPage page = new PatchouliPage(builder, getPath(RITUALS, ritual.getRegistryName().getPath()));
        this.pages.add(page);
    }

    public void addEnchantmentPage(Enchantment enchantment) {
        PatchouliBuilder builder = new PatchouliBuilder(ENCHANTMENTS, enchantment.getDescriptionId())
                .withIcon(getRegistryName(Items.ENCHANTED_BOOK).toString())
                .withTextPage(root + ".enchantment_desc." + getRegistryName(enchantment).getPath());

        for (int i = enchantment.getMinLevel(); i <= enchantment.getMaxLevel(); i++) {
            builder.withPage(new EnchantingPage("ars_nouveau:" + getRegistryName(enchantment).getPath() + "_" + i));
        }
        PatchouliPage page = new PatchouliPage(builder, getPath(ENCHANTMENTS, getRegistryName(enchantment).getPath()));
        this.pages.add(page);
    }

    public void addGlyphPage(AbstractSpellPart spellPart) {
        ResourceLocation category = switch (spellPart.defaultTier().value) {
            case 1 -> GLYPHS_1;
            case 2 -> GLYPHS_2;
            default -> GLYPHS_3;
        };
        PatchouliBuilder builder = new PatchouliBuilder(category, spellPart.getName())
                .withName(root + ".glyph_name." + spellPart.getRegistryName().getPath())
                .withIcon(spellPart.getRegistryName().toString())
                .withSortNum(spellPart instanceof AbstractCastMethod ? 1 : spellPart instanceof AbstractEffect ? 2 : 3)
                .withPage(new TextPage(root + ".glyph_desc." + spellPart.getRegistryName().getPath()))
                .withPage(new GlyphScribePage(spellPart));
        PatchouliPage page =  new PatchouliPage(builder, getPath(category, spellPart.getRegistryName().getPath()));
        this.pages.add(page);
    }

    /**
     * Gets a name for this provider, to use in logging.
     */
    @Override
    public String getName() {
        return "Example Patchouli Datagen";
    }

    @Override
    public Path getPath(ResourceLocation category, String fileName) {
        return this.generator.getPackOutput().getOutputFolder().resolve("data/"+ root +"/patchouli_books/modular/en_us/entries/" + category.getPath() + "/" + fileName + ".json");
    }

    ImbuementPage ImbuementPage(ItemLike item){
        return new ImbuementPage(root + ":imbuement_" + getRegistryName(item.asItem()).getPath());
    }
}