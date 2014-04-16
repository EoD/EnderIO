package crazypants.enderio.machine.generator;

import net.minecraft.block.Block;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.common.util.ForgeDirection;
import buildcraft.api.power.IPowerEmitter;
import buildcraft.api.power.PowerHandler.Type;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.power.Capacitors;
import crazypants.util.BlockCoord;

public class TileEntityStirlingGenerator extends AbstractMachineEntity implements ISidedInventory, IPowerEmitter {

  public static final float ENERGY_PER_TICK = 1;

  /** How many ticks left until the item is burnt. */
  private int burnTime = 0;
  private int totalBurnTime;

  private PowerDistributor powerDis;

  public TileEntityStirlingGenerator() {
    super(new SlotDefinition(1, 0), Type.ENGINE);
  }


  @Override
  public boolean canEmitPowerFrom(ForgeDirection side) {
    return true;
  }

  @Override
  public String getMachineName() {
    return ModObject.blockStirlingGenerator.unlocalisedName;
  }

  @Override
  public String getInventoryName() {
    return "Stirling Generator";
  }

  @Override
  public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
    return 0;
  }

  @Override
  public boolean isMachineItemValidForSlot(int i, ItemStack itemstack) {
    return TileEntityFurnace.isItemFuel(itemstack);
  }

  @Override
  public int[] getAccessibleSlotsFromSide(int var1) {
    return new int[] { 0 };
  }

  @Override
  public boolean canInsertItem(int i, ItemStack itemstack, int j) {
    return isItemValidForSlot(i, itemstack);
  }

  @Override
  public boolean canExtractItem(int i, ItemStack itemstack, int j) {
    return false;
  }

  @Override
  public boolean isActive() {
    return burnTime > 0 && redstoneCheckPassed;
  }

  @Override
  public float getProgress() {
    if(totalBurnTime <= 0) {
      return 0;
    }
    return (float) burnTime / (float) totalBurnTime;
  }

  @Override
  public void readCustomNBT(NBTTagCompound nbtRoot) {
    super.readCustomNBT(nbtRoot);
    burnTime = nbtRoot.getInteger("burnTime");
    totalBurnTime = nbtRoot.getInteger("totalBurnTime");
  }

  @Override
  public void writeCustomNBT(NBTTagCompound nbtRoot) {
    super.writeCustomNBT(nbtRoot);
    nbtRoot.setInteger("burnTime", burnTime);
    nbtRoot.setInteger("totalBurnTime", totalBurnTime);
  }

  @Override
  public void onNeighborBlockChange(Block blockId) {
    super.onNeighborBlockChange(blockId);
    if(powerDis != null) {
      powerDis.neighboursChanged();
    }
  }

  @Override
  public int getEnergyStored(ForgeDirection from) {
    return (int)(storedEnergy * 10);
  }



  public double getPowerPerTick() {
    return ENERGY_PER_TICK * getEnergyMultiplier();
  }

  @Override
  protected boolean processTasks(boolean redstoneCheckPassed) {
    boolean needsUpdate = false;

    if(burnTime > 0 && redstoneCheckPassed) {
      if(storedEnergy < capacitorType.capacitor.getMaxEnergyStored()) {
        storedEnergy += (ENERGY_PER_TICK * getEnergyMultiplier());
      }
      burnTime--;
      needsUpdate = true;
    }

    needsUpdate |= transmitEnergy();

    if(redstoneCheckPassed) {

      if(burnTime <= 0 && storedEnergy < capacitorType.capacitor.getMaxEnergyStored()) {
        if(inventory[0] != null && inventory[0].stackSize > 0) {
          burnTime = Math.round(TileEntityFurnace.getItemBurnTime(inventory[0]) * getBurnTimeMultiplier());
          if(burnTime > 0) {
            totalBurnTime = burnTime;
            ItemStack containedItem = inventory[0].getItem().getContainerItem(inventory[0]);
            if(containedItem != null) {
              inventory[0] = containedItem;
            } else {
              decrStackSize(0, 1);
            }
          }
          needsUpdate = true;
        }
      }
    }

    return needsUpdate;
  }

  private float getEnergyMultiplier() {
    if(capacitorType == Capacitors.ACTIVATED_CAPACITOR) {
      return 2;
    } else if(capacitorType == Capacitors.ENDER_CAPACITOR) {
      return 4;
    }
    return 1;
  }

  public float getBurnTimeMultiplier() {
    if(capacitorType == Capacitors.ACTIVATED_CAPACITOR) {
      //burn for 62.5% of the time to produce 2x the power, i.e. 1.25 the effecientcy
      return 0.625f;
    } else if(capacitorType == Capacitors.ENDER_CAPACITOR) {
      //burn for half as long to produce 4x the power, i.e. twice the effecientcy
      return 0.5f;
    }
    return 1;
  }

  //private PowerDistributor powerDis;
  private boolean transmitEnergy() {
    if(powerDis == null) {
      powerDis = new PowerDistributor(new BlockCoord(this));
    }
    double canTransmit = Math.min(storedEnergy, capacitorType.capacitor.getMaxEnergyExtracted());
    if(canTransmit <= 0) {
      return false;
    }
    float transmitted = powerDis.transmitEnergy(worldObj, (float)canTransmit);
    storedEnergy -= transmitted;
    return transmitted > 0;
  }

  @Override
  public boolean hasCustomInventoryName() {
    return false;
  }



}
