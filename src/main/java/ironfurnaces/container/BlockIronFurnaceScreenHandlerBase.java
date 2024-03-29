package ironfurnaces.container;

import ironfurnaces.tileentity.BlockIronFurnaceTileBase;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


public abstract class BlockIronFurnaceScreenHandlerBase extends AbstractRecipeScreenHandler<Inventory> {

    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;
    private final RecipeType<? extends AbstractCookingRecipe> recipeType;
    protected World world;
    public BlockPos pos;


    protected BlockIronFurnaceScreenHandlerBase(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(type, syncId, playerInventory, new SimpleInventory(4), new ArrayPropertyDelegate(4));
        pos = buf.readBlockPos();
        world = playerInventory.player.world;
    }

    protected BlockIronFurnaceScreenHandlerBase(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
        super(type, syncId);
        this.recipeType = RecipeType.SMELTING;
        checkSize(inventory, 4);
        checkDataCount(propertyDelegate, 4);
        this.inventory = inventory;
        this.propertyDelegate = propertyDelegate;
        this.addSlot(new Slot(inventory, 0, 56, 17));
        this.addSlot(new SlotIronFurnaceFuel(this, this.inventory, 1, 56, 53));
        this.addSlot(new SlotIronFurnace(playerInventory.player, this.inventory, 2, 116, 35));
        this.addSlot(new SlotIronFurnaceAugment(this, this.inventory, 3, 26, 35));

        int k;
        for(k = 0; k < 3; ++k) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + k * 9 + 9, 8 + j * 18, 84 + k * 18));
            }
        }

        for(k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
        }

        this.addProperties(propertyDelegate);

    }

    @Override
    public void addListener(ScreenHandlerListener listener) {
        super.addListener(listener);
    }

    public void populateRecipeFinder(RecipeMatcher finder) {
        if (this.inventory instanceof RecipeInputProvider) {
            ((RecipeInputProvider)this.inventory).provideRecipeInputs(finder);
        }

    }

    public void clearCraftingSlots() {
        this.getSlot(0).setStack(ItemStack.EMPTY);
        this.getSlot(2).setStack(ItemStack.EMPTY);
    }

    public boolean matches(Recipe<? super Inventory> recipe) {
        return recipe.matches(this.inventory, this.world);
    }

    public int getCraftingResultSlotIndex() {
        return 2;
    }

    public int getCraftingWidth() {
        return 1;
    }

    public int getCraftingHeight() {
        return 1;
    }

    @Environment(EnvType.CLIENT)
    public int getCraftingSlotCount() {
        return 3;
    }

    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            if (index == 2) {
                if (!this.insertItem(itemStack2, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickTransfer(itemStack2, itemStack);
            } else if (index != 1 && index != 0) {
                if (this.isSmeltable(player.world, itemStack2)) {
                    if (!this.insertItem(itemStack2, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (this.isFuel(itemStack2)) {
                    if (!this.insertItem(itemStack2, 1, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 3 && index < 30) {
                    if (!this.insertItem(itemStack2, 30, 39, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 30 && index < 39 && !this.insertItem(itemStack2, 3, 30, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(itemStack2, 3, 39, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack2.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }

            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTakeItem(player, itemStack2);
        }

        return itemStack;
    }

    protected boolean isSmeltable(World world, ItemStack itemStack) {
        return world.getRecipeManager().getFirstMatch(this.recipeType, new SimpleInventory(new ItemStack[]{itemStack}), this.world).isPresent();
    }

    protected boolean isFuel(ItemStack itemStack) {
        return BlockIronFurnaceTileBase.isItemFuel(itemStack);
    }

    @Environment(EnvType.CLIENT)
    public int getCookProgress() {
        int i = this.propertyDelegate.get(2);
        int j = this.propertyDelegate.get(3);
        return j != 0 && i != 0 ? i * 24 / j : 0;
    }

    @Environment(EnvType.CLIENT)
    public int getFuelProgress() {
        int i = this.propertyDelegate.get(1);
        if (i == 0) {
            i = 200;
        }

        return this.propertyDelegate.get(0) * 13 / i;
    }

    @Environment(EnvType.CLIENT)
    public boolean isBurning() {
        return this.propertyDelegate.get(0) > 0;
    }

    @Override
    public RecipeBookCategory getCategory() {
        return null;
    }

    public boolean canInsertIntoSlot(int index) {
        return index != 1;
    }

    public BlockPos getPos() {
        return pos;
    }

    public World getWorld() {
        return world;
    }


}
