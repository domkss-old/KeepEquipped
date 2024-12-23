package net.domkss.keepequipped;

import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

import java.util.HashMap;
import java.util.Map;

public class TempInventoryStorage {

    private static TempInventoryStorage instance;

    private static Map<String, DefaultedList<ItemStack>>  tempInventoryMap;


    private TempInventoryStorage(){
        tempInventoryMap=new HashMap<>();
    }

    public static TempInventoryStorage getInstance(){
        if(instance==null){
            instance=new TempInventoryStorage();
        }
        return instance;
    }

    public DefaultedList<ItemStack> getInventoryById(int playerID, String inventoryName){
        DefaultedList<ItemStack> inventory = tempInventoryMap.get(inventoryName+"_"+playerID);
        tempInventoryMap.remove(inventoryName+"_"+playerID);
        return inventory;
    }

    public void addInventory(int playerID, String inventoryName,DefaultedList<ItemStack> inventory){
        tempInventoryMap.put(inventoryName+"_"+playerID,inventory);
    }


}
