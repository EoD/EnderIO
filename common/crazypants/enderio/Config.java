package crazypants.enderio;

import java.io.File;
import java.io.IOException;

import net.minecraftforge.common.Configuration;

import org.apache.commons.io.FileUtils;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import crazypants.vecmath.VecmathUtil;

public final class Config {

  static int BID = 700;
  static int IID = 8524;

  public static final double DEFAULT_CONDUIT_SCALE = 0.2;

  public static boolean useAlternateBinderRecipe;

  public static boolean useAlternateTesseractModel;

  public static boolean photovoltaicCellEnabled = true;

  public static double conduitScale = DEFAULT_CONDUIT_SCALE;

  public static int numConduitsPerRecipe = 8;

  public static double transceiverEnergyLoss = 0.1;

  public static double transceiverUpkeepCost = 0.25;

  public static double transceiverBucketTransmissionCost = 1;

  public static int transceiverMaxIO = 256;

  public static File configDirectory;

  public static boolean useHardRecipes = false;

  public static boolean detailedPowerTrackingEnabled = false;

  public static double maxPhotovoltaicOutput = 1.0;

  public static boolean useSneakMouseWheelYetaWrench = true;

  public static boolean useSneakRightClickYetaWrench = false;

  public static boolean useRfAsDefault = true;

  public static boolean itemConduitUsePhyscialDistance = false;

  public static int advancedFluidConduitExtractRate = 100;

  public static int advancedFluidConduitMaxIoRate = 400;

  public static int fluidConduitExtractRate = 50;

  public static int fluidConduitMaxIoRate = 200;

  public static void load(FMLPreInitializationEvent event) {
    configDirectory = new File(event.getModConfigurationDirectory(), "enderio");
    if(!configDirectory.exists()) {
      configDirectory.mkdir();
    }

    File deprecatedFile = event.getSuggestedConfigurationFile();

    File configFile = new File(configDirectory, "EnderIO.cfg");
    if(deprecatedFile.exists()) {
      try {
        FileUtils.moveFile(deprecatedFile, configFile);
      } catch (IOException e) {
        Log.warn("Could not move old config file to new directory: " + e);
      }
    }

    Configuration cfg = new Configuration(configFile);
    try {
      cfg.load();
      Config.processConfig(cfg);
    } catch (Exception e) {
      Log.error("EnderIO has a problem loading it's configuration");
    } finally {
      if(cfg.hasChanged()) {
        cfg.save();
      }
    }
  }

  public static void processConfig(Configuration config) {

    for (ModObject e : ModObject.values()) {
      e.load(config);
    }

    useRfAsDefault = config.get("Settings", "displayPowerAsRedstoneFlux", useRfAsDefault, "If true, all power is displayed in RF, otherwise MJ is used.")
        .getBoolean(useRfAsDefault);

    useHardRecipes = config.get("Settings", "useHardRecipes", useHardRecipes, "When enabled machines cost significantly more.")
        .getBoolean(useHardRecipes);

    numConduitsPerRecipe = config.get("Settings", "numConduitsPerRecipe", numConduitsPerRecipe,
        "The number of conduits crafted per recipe.").getInt(numConduitsPerRecipe);

    photovoltaicCellEnabled = config.get("Settings", "photovoltaicCellEnabled", photovoltaicCellEnabled,
        "If set to false Photovoltaic Cells will not be craftable.")
        .getBoolean(photovoltaicCellEnabled);

    maxPhotovoltaicOutput = config.get("Settings", "maxPhotovoltaicOutput", maxPhotovoltaicOutput,
        "Maximum output in MJ/t of the Photovoltaic Panels.").getDouble(maxPhotovoltaicOutput);

    useAlternateBinderRecipe = config.get("Settings", "useAlternateBinderRecipe", false, "Create conduit binder in crafting table instead of furnace")
        .getBoolean(useAlternateBinderRecipe);

    conduitScale = config.get("Settings", "conduitScale", DEFAULT_CONDUIT_SCALE,
        "Valid values are between 0-1, smallest conduits at 0, largest at 1.\n" +
            "In SMP, all clients must be using the same value as the server.").getDouble(DEFAULT_CONDUIT_SCALE);
    conduitScale = VecmathUtil.clamp(conduitScale, 0, 1);

    fluidConduitExtractRate = config.get("Settings", "fluidConduitExtractRate", fluidConduitExtractRate,
        "Number of millibuckects per tick extract by a fluid conduits auto extract..").getInt(fluidConduitExtractRate);

    fluidConduitMaxIoRate = config.get("Settings", "fluidConduitMaxIoRate", fluidConduitMaxIoRate,
        "Number of millibuckects per tick that can pass through a single connection to a fluid conduit.").getInt(fluidConduitMaxIoRate);

    advancedFluidConduitExtractRate = config.get("Settings", "advancedFluidConduitExtractRate", advancedFluidConduitExtractRate,
        "Number of millibuckects per tick extract by advanced fluid conduits auto extract..").getInt(advancedFluidConduitExtractRate);

    advancedFluidConduitMaxIoRate = config.get("Settings", "advancedFluidConduitMaxIoRate", advancedFluidConduitMaxIoRate,
        "Number of millibuckects per tick that can pass through a single connection to an advanced fluid conduit.").getInt(advancedFluidConduitMaxIoRate);

    useAlternateTesseractModel = config.get("Settings", "useAlternateTransceiverModel", useAlternateTesseractModel,
        "Use TheKazador's alternatice model for the Dimensional Transceiver")
        .getBoolean(false);
    transceiverEnergyLoss = config.get("Settings", "transceiverEnergyLoss", transceiverEnergyLoss,
        "Amount of energy lost when transfered by Dimensional Transceiver 0 is no loss, 1 is 100% loss").getDouble(transceiverEnergyLoss);
    transceiverUpkeepCost = config.get("Settings", "transceiverUpkeepCost", transceiverUpkeepCost,
        "Number of MJ/t required to keep a Dimensional Transceiver connection open").getDouble(transceiverUpkeepCost);
    transceiverMaxIO = config.get("Settings", "transceiverMaxIO", transceiverMaxIO,
        "Maximum MJ/t sent and recieved by a Dimensional Transceiver per tick. Input and output limits are not cumulative").getInt(transceiverMaxIO);
    transceiverBucketTransmissionCost = config.get("Settings", "transceiverBucketTransmissionCost", transceiverBucketTransmissionCost,
        "The cost in MJ of trasporting a bucket of fluid via a Dimensional Transceiver.").getDouble(transceiverBucketTransmissionCost);

    detailedPowerTrackingEnabled = config
        .get(
            "Settings",
            "perInterfacePowerTrackingEnabled",
            detailedPowerTrackingEnabled,
            "Enable per tick sampling on individual power inputs and outputs. This allows slightly more detailed messages from the MJ Reader but has a negative impact on server performance.")
        .getBoolean(detailedPowerTrackingEnabled);

    useSneakMouseWheelYetaWrench = config.get("Settings", "useSneakMouseWheelYetaWrench", useSneakMouseWheelYetaWrench,
        "If true, shift-mouse wheel will change the conduit display mode when the YetaWrench is eqipped.")
        .getBoolean(useSneakMouseWheelYetaWrench);

    useSneakRightClickYetaWrench = config.get("Settings", "useSneakRightClickYetaWrench", useSneakRightClickYetaWrench,
        "If true, shift-clicking the YetaWrench on a null or non wrenchable object will change the conduit display mode.")
        .getBoolean(useSneakRightClickYetaWrench);

    itemConduitUsePhyscialDistance = config.get("Settings", "itemConduitUsePhyscialDistance", itemConduitUsePhyscialDistance, "If true, " +
        "'line of sight' distance rather than conduit path distance is used to calculate priorities.")
        .getBoolean(itemConduitUsePhyscialDistance);

    itemConduitUsePhyscialDistance = config.get("Settings", "itemConduitUsePhyscialDistance", itemConduitUsePhyscialDistance, "If true, " +
        "'line of sight' distance rather than conduit path distance is used to calculate priorities.")
        .getBoolean(itemConduitUsePhyscialDistance);

    if(!useSneakMouseWheelYetaWrench && !useSneakRightClickYetaWrench) {
      Log.warn("Both useSneakMouseWheelYetaWrench and useSneakRightClickYetaWrench are set to false. Enabling mouse wheel.");
      useSneakMouseWheelYetaWrench = true;
    }

  }

  private Config() {
  }

}
