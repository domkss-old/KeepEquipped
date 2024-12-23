package net.domkss.keepequipped.mixin;


import net.domkss.keepequipped.TempInventoryStorage;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.stream.IntStream;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity{


    @Shadow @Final private PlayerInventory inventory;


    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }



    @Inject(method = "dropInventory", at = @At(value = "HEAD"))
    private void saveEquipment(CallbackInfo ci) {
        DefaultedList<ItemStack> savedMain= DefaultedList.ofSize(36,ItemStack.EMPTY);
        DefaultedList<ItemStack> savedArmor = DefaultedList.ofSize(4,ItemStack.EMPTY);
        DefaultedList<ItemStack> savedOffHand = DefaultedList.ofSize(1,ItemStack.EMPTY);


        ItemStack mainHandItem = this.inventory.main.get(this.inventory.selectedSlot).copy();
        savedMain.set(0,mainHandItem);
        IntStream.range(0, savedArmor.size()).forEach(i -> savedArmor.set(i, this.inventory.armor.get(i).copy()));
        IntStream.range(0, savedOffHand.size()).forEach(i -> savedOffHand.set(i, this.inventory.offHand.get(i).copy()));

        TempInventoryStorage tempStorage = TempInventoryStorage.getInstance();

        tempStorage.addInventory(inventory.player.getId(),"main",savedMain);
        tempStorage.addInventory(inventory.player.getId(),"armor",savedArmor);
        tempStorage.addInventory(inventory.player.getId(),"offhand",savedOffHand);


        this.inventory.main.set(this.inventory.selectedSlot,ItemStack.EMPTY);
        this.inventory.offHand.clear();
        this.inventory.armor.clear();

    }
}