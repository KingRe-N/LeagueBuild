package home.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class itemData {
    /**
     * Reads in items from txt file, in the format of an img file where the x and y coordinates correspond to a certain item.
     * @return HashMap containing unique code(coordinates) and Item object pairs
     * @throws
     */
    public HashMap<Integer, Item> readItems() throws IOException {

        // attempted to implement gui, get input into the program from gui and output.
        Scanner s = new Scanner(new File("items.txt"));
        HashMap<Integer, Item> items= new HashMap<>();

        while (s.hasNext()) {
            String itemName = s.nextLine();
            int x = Integer.parseInt(s.nextLine()); if(x == 0) x=1;
            int y = Integer.parseInt(s.nextLine()); if(y == 0) y=1;
            int price = Integer.parseInt(s.nextLine());

            // combining the two ints by turning them into strings
            String keyString = "" + x + y;
            int key = Integer.parseInt(keyString);

            if(items.containsKey(key)) {
                System.out.println("check for duplicate or item maybe from different image: " + key);
                return null;
            }

            Item item = new Item(key, itemName, price);
            items.put(key, item);
        }
        return items;

    }

    public boolean itemExist(Item[] itemsArr, Item item){
        for(Item x : itemsArr){
            if(x == null) return false;
            if(x.equals(item)) return true;
        }
        return false;
    }

    public void readBuildPaths(HashMap<Integer, Item> items) throws IOException{
        Scanner s = new Scanner(new File("itemPaths.txt"));
        while(s.hasNext()) {
            String line = s.nextLine();
            Item parentItem = items.get(Integer.parseInt(line));
            while(s.hasNext() && !line.equals("")) {
                line = s.nextLine();
                if(line.equals("")) continue;
                parentItem.addRecipe(Integer.parseInt(line));
            }
        }
    }

    public ArrayList<Item> createRecipe(HashMap<Integer, Item> allItems, Item item){
        ArrayList<Item> recipe = new ArrayList<>();
        for(int i = 0; i < item.getRecipe().size(); i++) {
            Item tempItem = allItems.get(  item.getRecipe().get(i)  );
            recipe.add(tempItem);
            if(tempItem.hasRecipe()){
                recipe.addAll(createRecipe(allItems, tempItem));
            }
        }
        Collections.sort(recipe);
        Collections.reverse(recipe);

        return recipe;
    }

//    public void autoBuy(ArrayList<Item> fullBuild, HashMap<Integer, Item> allItems) throws IOException{
//        ArrayList<Item> inventory = new ArrayList<>();
//        int i = 0;
//
//        ArrayList<Item> recipe = createRecipe(allItems, fullBuild.get(i));
//
//        while(true) {
//            System.out.println("Enter Current Gold: ");
//            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//            String gold = reader.readLine();
//            int currentGold = Integer.parseInt(gold);
//
//            ArrayList<Item> statInventory = new ArrayList<>(inventory);
//
//            inventory = currentRecall(allItems, fullBuild, recipe, inventory, i, currentGold);
//            if(i == 5 && inventory.get(5) == fullBuild.get(5)) break;
//
//            recipe = updateRecipe(allItems, inventory, recipe, statInventory);
//
//            System.out.println("\nInventory: ");
//            for(Item x : inventory){
//                System.out.println(x.getName());
//            }
//
//            // a finalbuild item obtained, currentItem is the nextItem
//            if(inventory.contains(fullBuild.get(i))) {
//                for (int j = i; j < fullBuild.size(); j++) {
//                    if (inventory.contains(fullBuild.get(j))) ++i;
//                }
//                if(i == 6) break;
//                recipe = updateRecipe(allItems, inventory, createRecipe(allItems, fullBuild.get(i)),null);
//            }
//
//            System.out.println("\n-------------------------\n");
//        }
//        System.out.println("Build Finished");
//    }

//    public ArrayList<Item> currentRecall(HashMap<Integer, Item> allItems, ArrayList<Item> fullbuild, ArrayList<Item> recipe, ArrayList<Item> inventory, int index, int gold){
//        Item currentItem = fullbuild.get(index);
//        ArrayList<Item> statRecipe;
//        statRecipe = recipe;
//
//        // enough gold to finish buying current item
//        int finishingPrice = currentItem.recipePrice(allItems);
//        for(Item item: recipe) {
//            if(item.hasRecipe()) finishingPrice += item.recipePrice(allItems);
//            else finishingPrice += item.getPrice();
//        }
//
//        if(finishingPrice <= gold){
//            inventory = updateRecipe(allItems, createRecipe(allItems,currentItem), inventory, null);
//            inventory.add(currentItem);
//            gold -= finishingPrice;
//            System.out.println("Finish buying " + currentItem.getName());
//            if(index !=5) {
//                currentItem = fullbuild.get(++index);
//                return currentRecall(allItems, fullbuild, createRecipe(allItems, currentItem), inventory, index, gold);
//            }else return inventory;
//        }
//
//        for(int i = 0; i < statRecipe.size(); i++) {
//            if(fullInventory(inventory)) return inventory;
//
//            Item tempItem = statRecipe.get(i);
//
//            // current item is second to last, thus inventory space is used for more valuable items.
//            if(index >= 5 && i > 3) { System.out.println("Get More Gold!!!"); break;}
//
//            if(tempItem.hasRecipe() && tempItem.recipePrice(allItems) <= gold){
//                ArrayList<Item> tempRecipe = createRecipe(allItems, tempItem);
//
//                tempRecipe = updateRecipe(allItems, inventory, tempRecipe, null);
//
//                int tempPrice = tempItem.recipePrice(allItems);
//                for(Item item: tempRecipe) {
//                    if(item.hasRecipe()) tempPrice += item.recipePrice(allItems);
//                    else tempPrice += item.getPrice();
//                }
//
//                if(tempPrice <= gold){
//                    inventory = updateRecipe(allItems, createRecipe(allItems,tempItem), inventory,null);
//                    inventory.add(tempItem);
//                    gold -= tempPrice;
//                    System.out.println("Buy " + tempItem.getName());
//                    recipe = updateRecipe(allItems, inventory, recipe, null);
//                    return currentRecall(allItems, fullbuild, recipe, inventory, index, gold);
//                }
//            }
//
//            if(tempItem.getPrice() <= gold){
//                inventory.add(tempItem);
//                gold -= tempItem.getPrice();
//                System.out.println("Buy " + tempItem.getName());
//            }
//
//        }
//        return inventory;
//    }

    /**
     * removes already bought items from the recipe, approached incrementally because we don't want to remove dupes.
     * @param statList delete items from updatingList that exist in this list
     * @param updatingList List
     * @return
     */
    public ArrayList<Item> updateRecipe(HashMap<Integer, Item> allItems, ArrayList<Item> statList, ArrayList<Item> updatingList, ArrayList<Item> prevList){
        for(int i = 0; i < statList.size(); i++){
            Item ithItem = statList.get(i);
            boolean removed = false;

            for(int j = 0; j < updatingList.size(); j++){
                if(ithItem == updatingList.get(j)) {
                    updatingList.remove(j);
                    removed = true;
                    break;
                }
            }

            if(ithItem.hasRecipe() && removed) {
                if(prevList != null){
                    if(prevList.contains(allItems.get(ithItem.getRecipe().get(0)))) break;
                }
                updatingList = updateRecipe(allItems, createRecipe(allItems, ithItem), updatingList, null);
            }
        }
        return updatingList;
    }

    public boolean fullInventory(ArrayList<Item> inventory){
        if(inventory.size() >= 6) {
            return true;
        }
        return false;
    }

}
