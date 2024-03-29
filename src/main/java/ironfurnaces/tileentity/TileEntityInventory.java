package ironfurnaces.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Nameable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public abstract class TileEntityInventory extends BlockEntity implements ITileInventory, SidedInventory, NamedScreenHandlerFactory, Nameable {
    protected DefaultedList<ItemStack> inventory;
    protected Text name;

    public TileEntityInventory(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state, int sizeInventory) {
        super(tileEntityTypeIn, pos, state);
        this.inventory = DefaultedList.ofSize(sizeInventory, ItemStack.EMPTY);
    }

    /**
    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return new BlockEntityUpdateS2CPacket(pos, 127, this.toTag(new CompoundTag()));
    }
     **/

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        if (this.world.getBlockEntity(this.pos) != this) {
            return false;
        } else {
            return player.squaredDistanceTo((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
        }
    }


    @Override
    public boolean isValid(int index, ItemStack stack) {
        return this.IisItemValidForSlot(index, stack);
    }

    public void setCustomName(Text name) {
        this.name = name;
    }

    @Override
    public Text getName() {
        return (this.name != null ? this.name : new TranslatableText(IgetName()));
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return this.IgetSlotsForFace(side);
    }

    @Override
    public boolean canExtract(int index, ItemStack stack, Direction direction) {
        return IcanExtractItem(index, stack, direction);
    }

    @Override
    public boolean canInsert(int index, ItemStack itemStackIn, Direction direction) {
        return this.isValid(index, itemStackIn);
    }

    @Override
    public int size() {
        return this.inventory.size();
    }

    @Override
    public boolean isEmpty() {
        for(ItemStack itemstack : this.inventory) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }
        return true;
    }
    @Override
    public ItemStack getStack(int slot) {
        return (ItemStack)this.inventory.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(this.inventory, slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(this.inventory, slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        ItemStack itemStack = (ItemStack)this.inventory.get(slot);
        boolean bl = !stack.isEmpty() && stack.isItemEqualIgnoreDamage(itemStack) && ItemStack.areNbtEqual(stack, itemStack);
        this.inventory.set(slot, stack);
        if (stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(this.getMaxCountPerStack());
        }

    }



    public void readNbt(NbtCompound compound) {
        super.readNbt(compound);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        Inventories.readNbt(compound, this.inventory);
        if (compound.contains("CustomName", 8)) {
            this.name = Text.Serializer.fromJson(compound.getString("CustomName"));
        }
    }

    public NbtCompound writeNbt(NbtCompound compound) {
        super.writeNbt(compound);
        Inventories.writeNbt(compound, this.inventory);
        if (this.name != null) {
            compound.putString("CustomName", Text.Serializer.toJson(this.name));
        }
        return compound;
    }

    @Override
    public void clear() {
        this.inventory.clear();
    }

    @Override
    public boolean hasCustomName() {
        return this.name != null;
    }

    @Override
    public Text getCustomName() {
        return this.name;
    }

    @Override
    public Text getDisplayName() {
        return this.getName();
    }

    @Override
    public ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return IcreateMenu(i, playerInventory, playerEntity);
    }
}
