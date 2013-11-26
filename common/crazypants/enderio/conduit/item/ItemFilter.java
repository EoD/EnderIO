package crazypants.enderio.conduit.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

public class ItemFilter implements IInventory {

  boolean isBlacklist = false;
  boolean matchMeta = true;
  boolean matchNBT = true;
  boolean useOreDict = false;
  boolean sticky = true;

  ItemStack[] items;

  public ItemFilter() {
    this(0);
  }

  public ItemFilter(int numItems) {
    items = new ItemStack[numItems];
  }

  public boolean isAccepted(ItemStack item) {

    if(item == null) {
      return false;
    }
    if(!isValid()) {
      return true;
    }

    boolean matched = false;
    for (ItemStack it : items) {
      if(it != null && item.itemID == it.itemID) {
        matched = true;
        if(matchMeta && item.getItemDamage() != it.getItemDamage()) {
          matched = false;
        } else if(matchNBT && !ItemStack.areItemStackTagsEqual(item, it)) {
          matched = false;
        }
      }
      if(matched) {
        break;
      }
    }

    return isBlacklist ? !matched : matched;
  }

  public boolean isValid() {
    for (ItemStack item : items) {
      if(item != null) {
        return true;
      }
    }
    return false;
  }

  public boolean isBlacklist() {
    return isBlacklist;
  }

  public void setBlacklist(boolean isBlacklist) {
    this.isBlacklist = isBlacklist;
  }

  public boolean isMatchMeta() {
    return matchMeta;
  }

  public void setMatchMeta(boolean matchMeta) {
    this.matchMeta = matchMeta;
  }

  public boolean isMatchNBT() {
    return matchNBT;
  }

  public void setMatchNBT(boolean matchNbt) {
    this.matchNBT = matchNbt;
  }

  public boolean isUseOreDict() {
    return useOreDict;
  }

  public void setUseOreDict(boolean useOreDict) {
    this.useOreDict = useOreDict;
  }

  public boolean isSticky() {
    return sticky;
  }

  public void setSticky(boolean sticky) {
    this.sticky = sticky;
  }

  public void writeToNBT(NBTTagCompound nbtRoot) {
    nbtRoot.setBoolean("isBlacklist", isBlacklist);
    nbtRoot.setBoolean("matchMeta", matchMeta);
    nbtRoot.setBoolean("matchNBT", matchNBT);
    nbtRoot.setBoolean("useOreDict", useOreDict);
    nbtRoot.setBoolean("sticky", sticky);

    nbtRoot.setShort("numItems", (short) items.length);
    int i = 0;
    for (ItemStack item : items) {
      NBTTagCompound itemTag = new NBTTagCompound();
      if(item != null) {
        item.writeToNBT(itemTag);
        nbtRoot.setTag("item" + i, itemTag);
      }

      i++;
    }

  }

  public void readFromNBT(NBTTagCompound nbtRoot) {
    isBlacklist = nbtRoot.getBoolean("isBlacklist");
    matchMeta = nbtRoot.getBoolean("matchMeta");
    matchNBT = nbtRoot.getBoolean("matchNBT");
    useOreDict = nbtRoot.getBoolean("useOreDict");
    sticky = nbtRoot.getBoolean("sticky");

    int numItems = nbtRoot.getShort("numItems");
    items = new ItemStack[numItems];
    for (int i = 0; i < numItems; i++) {
      NBTBase tag = nbtRoot.getTag("item" + i);
      if(tag instanceof NBTTagCompound) {
        items[i] = ItemStack.loadItemStackFromNBT((NBTTagCompound) tag);
      } else {
        items[i] = null;
      }
    }
  }

  @Override
  public int getSizeInventory() {
    return items.length;
  }

  @Override
  public ItemStack getStackInSlot(int i) {
    return items[i];
  }

  @Override
  public ItemStack decrStackSize(int fromSlot, int amount) {
    ItemStack item = items[fromSlot];
    items[fromSlot] = null;
    if(item == null) {
      return null;
    }
    item.stackSize = 0;
    return item;
  }

  @Override
  public ItemStack getStackInSlotOnClosing(int i) {
    return null;
  }

  @Override
  public void setInventorySlotContents(int i, ItemStack itemstack) {
    if(itemstack != null) {
      items[i] = itemstack.copy();
      items[i].stackSize = 0;
    } else {
      items[i] = null;
    }
  }

  @Override
  public String getInvName() {
    return "Item Filter";
  }

  @Override
  public boolean isInvNameLocalized() {
    return false;
  }

  @Override
  public int getInventoryStackLimit() {
    return 0;
  }

  @Override
  public void onInventoryChanged() {
  }

  @Override
  public boolean isUseableByPlayer(EntityPlayer entityplayer) {
    return true;
  }

  @Override
  public void openChest() {
  }

  @Override
  public void closeChest() {
  }

  @Override
  public boolean isItemValidForSlot(int i, ItemStack itemstack) {
    return true;
  }

}