package crazypants.enderio.item;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.EnderIO;
import crazypants.enderio.material.Alloy;
import crazypants.enderio.material.MachinePart;
import crazypants.enderio.material.Material;

public class ItemRecipes {

  public static void addRecipes() {
    ItemStack basicGear = new ItemStack(EnderIO.itemMachinePart, 1, MachinePart.BASIC_GEAR.ordinal());
    ItemStack electricalSteel = new ItemStack(EnderIO.itemAlloy, 1, Alloy.ELECTRICAL_STEEL.ordinal());
    ItemStack conductiveIron = new ItemStack(EnderIO.itemAlloy, 1, Alloy.CONDUCTIVE_IRON.ordinal());
    ItemStack vibCry = new ItemStack(EnderIO.itemMaterial, 1, Material.VIBRANT_CYSTAL.ordinal());
    ItemStack darkSteel = new ItemStack(EnderIO.itemAlloy, 1, Alloy.DARK_STEEL.ordinal());

    // Wrench
    ItemStack wrench = new ItemStack(EnderIO.itemYetaWench, 1, 0);
    GameRegistry.addShapedRecipe(wrench, "s s", " b ", " s ", 's', electricalSteel, 'b', basicGear);
    
    ItemStack magnet = new ItemStack(EnderIO.itemMagnet, 1, 0);    
    EnderIO.itemMagnet.setEnergy(magnet, 0);
    GameRegistry.addShapedRecipe(magnet, "scc", "  v", "scc", 's', electricalSteel, 'c', conductiveIron, 'v', vibCry);

    //Dark Steel        
    GameRegistry.addShapedRecipe(EnderIO.itemDarkSteelHelmet.createItemStack(), "sss", "s s", "   ", 's', darkSteel);
    GameRegistry.addShapedRecipe(EnderIO.itemDarkSteelChestplate.createItemStack(), "s s", "sss", "sss", 's', darkSteel);
    GameRegistry.addShapedRecipe(EnderIO.itemDarkSteelLeggings.createItemStack(), "sss", "s s", "s s", 's', darkSteel);
    GameRegistry.addShapedRecipe(EnderIO.itemDarkSteelBoots.createItemStack(), "s s", "s s", "   ", 's', darkSteel);
    GameRegistry.addShapedRecipe(EnderIO.itemDarkSteelBoots.createItemStack(), "   ", "s s", "s s", 's', darkSteel);

  }
  
  public static void addOreDictionaryRecipes() {
    ItemStack darkSteel = new ItemStack(EnderIO.itemAlloy, 1, Alloy.DARK_STEEL.ordinal());
    GameRegistry.addRecipe(new ShapedOreRecipe(EnderIO.itemDarkSteelSword.createItemStack(),  " s ", " s ", " w ", 's', darkSteel, 'w', "stickWood"));
    GameRegistry.addRecipe(new ShapedOreRecipe(EnderIO.itemDarkSteelSword.createItemStack(),  " s ", " s ", " w ", 's', darkSteel, 'w', "woodStick"));
    GameRegistry.addRecipe(new ShapedOreRecipe(EnderIO.itemDarkSteelPickaxe.createItemStack(), "sss", " w ", " w ", 's', darkSteel, 'w', "stickWood"));
    GameRegistry.addRecipe(new ShapedOreRecipe(EnderIO.itemDarkSteelPickaxe.createItemStack(), "sss", " w ", " w ", 's', darkSteel, 'w', "woodStick"));
  }
}
