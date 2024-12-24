package net.domkss.keepequipped.mixin;

import com.google.common.collect.ImmutableList;
import com.mojang.authlib.GameProfile;
import net.domkss.keepequipped.TempInventoryStorage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.stream.IntStream;

@Mixin(ServerPlayerEntity.class)
public abstract class PlayerRespawnMixin extends PlayerEntity {


    public PlayerRespawnMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "copyFrom", at = @At(value = "TAIL"))
    private void restoreSavedItems(ServerPlayerEntity oldPlayer, boolean alive,CallbackInfo info){
        int oldPlayerID = oldPlayer.getId();

        MinecraftServer server = oldPlayer.getServer();
        if(server == null || server.getGameRules().getBoolean(GameRules.KEEP_INVENTORY) || oldPlayer.isSpectator()) return;


        TempInventoryStorage tempStorage = TempInventoryStorage.getInstance();

        DefaultedList<ItemStack> savedMain = tempStorage.getInventoryById(oldPlayerID,"main");
        DefaultedList<ItemStack> savedArmor = tempStorage.getInventoryById(oldPlayerID,"armor");
        DefaultedList<ItemStack> savedOffHand = tempStorage.getInventoryById(oldPlayerID,"offhand");
        List<DefaultedList<ItemStack>> combinedItemList = ImmutableList.of(savedMain,savedArmor,savedOffHand);

        //Decrease equipment durability with 10% on death
        combinedItemList.forEach(itemList->itemList.forEach(
                itemStack -> {
                    Item item = itemStack.getItem();
                    if(item.isDamageable()){
                        int currentDurability = itemStack.getDamage();
                        int maxDurability = itemStack.getMaxDamage();

                        int durabilityReduction = (int) (maxDurability * 0.10);
                        int newDurability = currentDurability + durabilityReduction;
                        newDurability = Math.min(newDurability, maxDurability);
                        itemStack.setDamage(newDurability);
                    }
                }
        ));




        this.getInventory().main.set(0,savedMain.get(0));
        IntStream.range(0, savedArmor.size()).forEach(i -> this.getInventory().armor.set(i, savedArmor.get(i)));
        IntStream.range(0, savedOffHand.size()).forEach(i -> this.getInventory().offHand.set(i, savedOffHand.get(i)));

    }
}
