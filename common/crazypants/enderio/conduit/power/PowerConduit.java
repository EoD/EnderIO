package crazypants.enderio.conduit.power;

import java.util.HashMap;
import java.util.Map;

import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.AbstractConduit;
import crazypants.enderio.conduit.AbstractConduitNetwork;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.conduit.geom.CollidableComponent;
import crazypants.enderio.power.BasicCapacitor;
import crazypants.enderio.power.ICapacitor;
import crazypants.enderio.power.PowerHandlerUtil;
import crazypants.render.BoundingBox;
import crazypants.render.IconUtil;
import crazypants.vecmath.Vector3d;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.api.power.IPowerEmitter;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.power.PowerHandler.Type;

public class PowerConduit extends AbstractConduit implements IPowerConduit {

  static final Map<String, Icon> ICONS = new HashMap<String, Icon>();

  static final ICapacitor[] CAPACITORS = new BasicCapacitor[] {
    new BasicCapacitor(0, 64, 128, 0, 0, 0, 256),
    new BasicCapacitor(0, 128, 256, 0, 0, 0, 1024),
    new BasicCapacitor(0, 1024, 1024, 0, 0, 0, 2048)
  }; 
  
  static final String[] POSTFIX = new String[] { "", "Enhanced", "Ender" };
  
  static ItemStack createItemStackForSubtype(int subtype) {
    ItemStack result = new ItemStack(ModObject.itemPowerConduit.actualId, 1, subtype);    
    return result;
    
  }

  public static void initIcons() {
    IconUtil.addIconProvider(new IconUtil.IIconProvider() {

      @Override
      public void registerIcons(IconRegister register) {
        for (String pf : POSTFIX) {
          ICONS.put(ICON_KEY + pf, register.registerIcon(ICON_KEY + pf));
          ICONS.put(ICON_CORE_KEY + pf, register.registerIcon(ICON_CORE_KEY + pf));
        }
        ICONS.put(ICON_TRANSMISSION_KEY, register.registerIcon(ICON_TRANSMISSION_KEY));
      }

      @Override
      public int getTextureType() {       
        return 0;
      }

    });
  }

  public static final float WIDTH = 0.075f;
  public static final float HEIGHT = 0.075f;

  public static final Vector3d MIN = new Vector3d(0.5f - WIDTH, 0.5 - HEIGHT, 0.5 - WIDTH);
  public static final Vector3d MAX = new Vector3d(MIN.x + WIDTH, MIN.y + HEIGHT, MIN.z + WIDTH);

  public static final BoundingBox BOUNDS = new BoundingBox(MIN, MAX);

  protected PowerConduitNetwork network;
  private PowerHandler powerHandler;

  private int subtype;
  
  private float energyStored;

  public PowerConduit() {
  }

  public PowerConduit(int meta) {
    this.subtype = meta;
    powerHandler = createPowerHandlerForType();
  }
  
  @Override
  public ICapacitor getCapacitor() {
    return CAPACITORS[subtype];
  }

  private PowerHandler createPowerHandlerForType() {
    return PowerHandlerUtil.createHandler(CAPACITORS[subtype], this, Type.PIPE);
  }

  public float getEnergyStored() {
    return energyStored;
  }

  public void setEnergyStored(float energyStored) {
    this.energyStored = energyStored;
  }

  @Override
  public void writeToNBT(NBTTagCompound nbtRoot) {
    super.writeToNBT(nbtRoot);
    nbtRoot.setShort("subtype", (short) subtype);
    nbtRoot.setFloat("energyStored", energyStored);
  }

  @Override
  public void readFromNBT(NBTTagCompound nbtRoot) {
    super.readFromNBT(nbtRoot);
    subtype = nbtRoot.getShort("subtype");
    if (powerHandler == null) {
      powerHandler = createPowerHandlerForType();
    }
    energyStored = nbtRoot.getFloat("energyStored");
    if(energyStored < 0) {
      energyStored = 0;
    }    
  }

  @Override
  public PowerReceiver getPowerReceiver(ForgeDirection side) {
    return powerHandler.getPowerReceiver();
  }

  @Override
  public PowerHandler getPowerHandler() {
    return powerHandler;
  }

  @Override
  public void applyPerdition() {
  }

  @Override
  public void doWork(PowerHandler workProvider) {
  }

  @Override
  public World getWorld() {
    return getBundle().getEntity().worldObj;
  }

  @Override
  public AbstractConduitNetwork<?> getNetwork() {
    return network;
  }

  @Override
  public boolean setNetwork(AbstractConduitNetwork<?> network) {
    this.network = (PowerConduitNetwork) network;
    return true;
  }

  @Override
  public boolean canConnectToExternal(ForgeDirection direction) {    
    IPowerReceptor rec = getExternalPowerReceptor(direction);
    if(rec instanceof IPowerEmitter) {
      return ((IPowerEmitter)rec).canEmitPowerFrom(direction.getOpposite());
    }
    return rec != null;
  }

  @Override
  public void externalConnectionAdded(ForgeDirection direction) {
    super.externalConnectionAdded(direction);
    if (network != null) {
      TileEntity te = bundle.getEntity();
      network.powerReceptorAdded(this, direction, te.xCoord + direction.offsetX, te.yCoord + direction.offsetY, te.zCoord + direction.offsetZ,
          getExternalPowerReceptor(direction));
    }
  }

  @Override
  public void externalConnectionRemoved(ForgeDirection direction) {
    super.externalConnectionRemoved(direction);
    if (network != null) {
      TileEntity te = bundle.getEntity();
      network.powerReceptorRemoved(te.xCoord + direction.offsetX, te.yCoord + direction.offsetY, te.zCoord + direction.offsetZ);
    }
  }

  @Override
  public IPowerReceptor getExternalPowerReceptor(ForgeDirection direction) {
    TileEntity te = bundle.getEntity();
    World world = te.worldObj;
    if (world == null) {
      return null;
    }
    TileEntity test = world.getBlockTileEntity(te.xCoord + direction.offsetX, te.yCoord + direction.offsetY, te.zCoord + direction.offsetZ);
    if (test instanceof IConduitBundle) {
      return null;
    }    
    if (test instanceof IPowerReceptor) {
      return (IPowerReceptor) test;
    }
    return null;
  }

  @Override
  public ItemStack createItem() {
    return createItemStackForSubtype(subtype); 
  }

  @Override
  public Class<? extends IConduit> getBaseConduitType() {
    return IPowerConduit.class;
  }

  // Rendering
  @Override
  public Icon getTextureForState(CollidableComponent component) {
    if (component.dir == ForgeDirection.UNKNOWN) {
      return ICONS.get(ICON_CORE_KEY + POSTFIX[subtype]);
    }
    return ICONS.get(ICON_KEY + POSTFIX[subtype]);
  }

  @Override
  public Icon getTransmitionTextureForState(CollidableComponent component) {
    //return ICONS.get(ICON_TRANSMISSION_KEY);
    return null;
  }

}