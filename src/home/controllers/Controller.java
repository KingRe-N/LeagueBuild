package home.controllers;

import home.data.Champion;
import home.data.Item;
import home.data.champData;
import home.data.itemData;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class Controller implements Initializable{
    private champData champTest = new champData();
    private itemData itemTest = new itemData();

    // HashMap that contains all champs read in from text file
    private HashMap<String, Champion> allChamps;

    // HashMap that contains all items read in from text file
    private HashMap<Integer, Item> allItems;

    @FXML
    private Button topButton,junButton,midButton,adcButton,supButton;
    @FXML
    private Button goldButton;
    @FXML
    private TextField yourChamp, currentGold;
    @FXML
    private Label startingItem1, startingItem2, startingItem3;
    @FXML
    private Label finalItem1, finalItem2, finalItem3, finalItem4, finalItem5, finalItem6;
    @FXML
    private TextArea itemDisplay, inventoryDisplay;

    private String champName, goldString;
    private int role, gold;
    private Item[] startingItems;
    private Item[] recommendedItems;

    private ArrayList<Item> fullBuild = new ArrayList<>();
    private ArrayList<Item> inventory = new ArrayList<>();
    private ArrayList<Item> recipe = new ArrayList<>();
    private int i = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        yourChamp.focusedProperty().addListener((obs, oldVal, newVal) ->
        {
            if (!newVal) {
                champName = yourChamp.getText();
            }
        });

        currentGold.textProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    currentGold.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        currentGold.focusedProperty().addListener((obs, oldVal, newVal) ->
        {
            if (!newVal) {
                goldString = currentGold.getText();
                if(goldString.isEmpty()) return;
                gold = Integer.parseInt(currentGold.getText());
            }
        });

        try {
            allItems = itemTest.readItems();
            itemTest.readBuildPaths(allItems);
            allChamps = champTest.readChamps();
        }catch(IOException ex){
            ex.printStackTrace();
        }

    }

    @FXML
    private void handleClose(ActionEvent event){
        System.exit(0);
    }

    @FXML
    private void handleCalc(ActionEvent event){
        itemDisplay.clear();
        inventoryDisplay.clear();

        goldString = currentGold.getText();
        if(goldString.isEmpty()) return;
        gold = Integer.parseInt(currentGold.getText());

        ArrayList<Item> statInventory = new ArrayList<>(inventory);

        inventory = currentRecall(allItems, fullBuild, recipe, inventory, i, gold);

        recipe = itemTest.updateRecipe(allItems, inventory, recipe, statInventory);

        for(Item x : inventory){
            inventoryDisplay.appendText(x.getName() + "\n");
        }

        // a finalbuild item obtained, currentItem is the nextItem
        if(inventory.contains(fullBuild.get(i))) {
            for (int j = i; j < fullBuild.size(); j++) {
                if (inventory.contains(fullBuild.get(j))) ++i;
            }

            if(i == 6) {
                itemDisplay.appendText("Build Finished");
                goldButton.disarm();
                currentGold.setEditable(false);
                return;
            }

            recipe = itemTest.updateRecipe(allItems, inventory, itemTest.createRecipe(allItems, fullBuild.get(i)),null);
        }
        if(inventory.equals(statInventory))itemDisplay.appendText("Buy nothing.");

        currentGold.clear();
    }

    public ArrayList<Item> currentRecall(HashMap<Integer, Item> allItems, ArrayList<Item> fullbuild, ArrayList<Item> recipe, ArrayList<Item> inventory, int index, int gold){
        Item currentItem = fullbuild.get(index);
        ArrayList<Item> statRecipe;
        statRecipe = recipe;

        // enough gold to finish buying current item
        int finishingPrice = currentItem.recipePrice(allItems);
        for(Item item: recipe) {
            if(item.hasRecipe()) finishingPrice += item.recipePrice(allItems);
            else finishingPrice += item.getPrice();
        }

        if(finishingPrice <= gold){
            inventory = itemTest.updateRecipe(allItems, itemTest.createRecipe(allItems,currentItem), inventory, null);
            inventory.add(currentItem);
            gold -= finishingPrice;
            itemDisplay.appendText("Finish " + currentItem.getName() + "\n");
            if(index !=5) {
                currentItem = fullbuild.get(++index);
                return currentRecall(allItems, fullbuild, itemTest.createRecipe(allItems, currentItem), inventory, index, gold);
            }else return inventory;
        }

        for(int i = 0; i < statRecipe.size(); i++) {
            if(itemTest.fullInventory(inventory)) return inventory;

            Item tempItem = statRecipe.get(i);

            // current item is second to last, thus inventory space is used for more valuable items.
            if(index >= 5 && i > 3) { itemDisplay.appendText("Not enough gold.\n"); break;}

            if(tempItem.hasRecipe() && tempItem.recipePrice(allItems) <= gold){
                ArrayList<Item> tempRecipe = itemTest.createRecipe(allItems, tempItem);

                tempRecipe = itemTest.updateRecipe(allItems, inventory, tempRecipe, null);

                int tempPrice = tempItem.recipePrice(allItems);
                for(Item item: tempRecipe) {
                    if(item.hasRecipe()) tempPrice += item.recipePrice(allItems);
                    else tempPrice += item.getPrice();
                }

                if(tempPrice <= gold){
                    inventory = itemTest.updateRecipe(allItems, itemTest.createRecipe(allItems,tempItem), inventory,null);
                    inventory.add(tempItem);
                    gold -= tempPrice;
                    itemDisplay.appendText("Buy  " + tempItem.getName() + "\n");
                    recipe = itemTest.updateRecipe(allItems, inventory, recipe, null);
                    return currentRecall(allItems, fullbuild, recipe, inventory, index, gold);
                }
            }

            if(tempItem.getPrice() <= gold){
                inventory.add(tempItem);
                gold -= tempItem.getPrice();
                itemDisplay.appendText("Buy  " + tempItem.getName() + "\n");
            }

        }
        return inventory;
    }

    @FXML
    private void handleRole(ActionEvent event){
        Object node = event.getSource();
        Button clicked = (Button)node;
        Button[] allBtns = {topButton, junButton, midButton, adcButton, supButton};
        Label[] startingLbl = {startingItem1, startingItem2, startingItem3};
        Label[] finalLbl = {finalItem1, finalItem2, finalItem3, finalItem4, finalItem5, finalItem6};

        clicked.setStyle("-fx-background-color: #2A2A42; -fx-text-fill: #fcff45");
        for(int i = 0; i < 5; i++){
            if(clicked != allBtns[i]){
                allBtns[i].setStyle("-fx-background-color: #1f1f31; -fx-text-fill: #ffffff");
            }else role = i+1;
        }

        if (champName.isEmpty()) return;

        try {
            champTest.addChamp(champName, role, allChamps);

            int[][] itemPosition = champTest.getItems(champName, role);
            startingItems = champTest.startingItems(itemPosition, allItems);
            recommendedItems = champTest.finalBuild(itemPosition,allItems);


            for(int i = 0; i < startingItems.length; i++){
                if(startingItems[i] == null) break;
                startingLbl[i].setText(startingItems[i].getName());
            }

            for(int i = 0; i < recommendedItems.length; i++){
                if(recommendedItems[i] == null) break;
                finalLbl[i].setText(recommendedItems[i].getName());
            }

            fullBuild.clear();
            inventory.clear();

            for(Item x : recommendedItems){ fullBuild.add(x); }
            recipe = itemTest.createRecipe(allItems, fullBuild.get(i));
        }
        catch (IOException e){
            System.out.println("Enter champ name");
        }

    }

    @FXML
    private void handleChamp(ActionEvent event){
        Label[] startingLbl = {startingItem1, startingItem2, startingItem3};
        Label[] finalLbl = {finalItem1, finalItem2, finalItem3, finalItem4, finalItem5, finalItem6};

        champName = yourChamp.getText();

        if(champName.isEmpty()) return;

        try {
            champTest.addChamp(champName, role, allChamps);

            int[][] itemPosition = champTest.getItems(champName, role);
            startingItems = champTest.startingItems(itemPosition, allItems);
            recommendedItems = champTest.finalBuild(itemPosition,allItems);

            for(int i = 0; i < startingItems.length; i++){ if(startingItems[i] == null)break; startingLbl[i].setText(startingItems[i].getName()); }
            for(int i = 0; i < recommendedItems.length; i++){ finalLbl[i].setText(recommendedItems[i].getName()); }

            fullBuild.clear();
            inventory.clear();

            for(Item x : recommendedItems){ fullBuild.add(x); }
            recipe = itemTest.createRecipe(allItems, fullBuild.get(i));
        }
        catch (IOException e){
            System.out.println("Select lane");
        }
    }
}
