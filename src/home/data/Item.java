package home.data;

import java.util.ArrayList;
import java.util.HashMap;

public class Item implements Comparable<Item>{
    private String name;
    private int pos;
    private int price;
    private ArrayList<Integer> recipe= new ArrayList<>();

//    private int aDamage,
//                aSpeed,
//                crit,
//                lifeSteal;
//    private int aPower,
//                cooldown,
//                mana,
//                manaRegen;
//    private int health,
//                armor,
//                mr,
//                regen;

    public Item(int pos, String name, int price){
        this.pos = pos;
        this.name = name;
        this.price = price;
    }
    public void addRecipe(int itemCode) {
        recipe.add(itemCode);
    }
    public ArrayList<Integer> getRecipe() {
        return recipe;
    }
    public String getName(){
        return name;
    }
    public Integer getPrice(){
        return price;
    }
    public int getCode(){
        return pos;
    }

    public int compareTo(Item o) {
        return this.getPrice().compareTo(o.getPrice());
    }

    public boolean hasRecipe(){
        if(recipe.isEmpty()) return false;
        return true;
    }
    public int recipePrice(HashMap<Integer, Item> items){
        int res = this.price;
        int components = 0;
        for(int i = 0; i < recipe.size(); i++){
            components += items.get(recipe.get(i)).getPrice();
        }
        return res - components;
    }

}
