/*
Program to determine the best build path versus the opposing team composition. Giving the optimal gold needed when recalling. Using U.gg
as an item guideline as the meta changes.
*/
package home;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.*;

public class Main extends Application{

    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void start(Stage primaryStage) throws Exception{

        Parent root = FXMLLoader.load(getClass().getResource("fxml/GUI.fxml"));
        primaryStage.initStyle(StageStyle.UNDECORATED);

        root.setOnMousePressed(event ->  {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
        });

        root.setOnMouseDragged(event ->  {
                primaryStage.setX(event.getSceneX() - xOffset);
                primaryStage.setY(event.getSceneY() - yOffset);
        });

        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) throws IOException{
        launch(args);

//        champData champTest = new champData();
//        itemData itemTest = new itemData();
//
//        // HashMap that contains all items read in from text file
//        HashMap<Integer, Item> allItems = itemTest.readItems();
//        itemTest.readBuildPaths(allItems);
//
//        // HashMap that contains all champs read in from text file
//        HashMap<String, Champion> allChamps = champTest.readChamps();
//
//        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//
//        System.out.print("Your Champion: ");
//        String yourChamp = reader.readLine();
//
//        System.out.print("1.top --- 2.jungle --- 3.mid --- 4.adc --- 5.support\nRole:");
//        String yourRole = reader.readLine();
//        int role = Integer.parseInt(yourRole);
//
//        try {
//            champTest.addChamp(yourChamp, role, allChamps);
//        }catch(NullPointerException e) {
//            System.out.println("Error!\nCheck Spelling");
//        }
//
//        int[][] itemPosition = champTest.getItems(yourChamp, role);
//
//        Item[] recommendedItems = champTest.finalBuild(itemPosition,allItems);
//
////        for(int i = 0; i < recommendedItems.length; i++){
////            int totalPrice = recommendedItems[i].getPrice();
////            int recipePrice = 0;
////            ArrayList<Integer> recipe = recommendedItems[i].getRecipe();
////            System.out.println(recommendedItems[i].getName() + "  " + recommendedItems[i].getCode());
////
////            for(int j = 0; j < recipe.size(); j++){
////                recipePrice += items.get(recipe.get(j)).getPrice();
////                System.out.println(items.get(recipe.get(j)).getName() + "  " + items.get(recipe.get(j)).getCode());
////                System.out.println("        " + items.get(recipe.get(j)).getPrice());
////            }
////            System.out.println(recommendedItems[i].getName() + " recipe\n        " + (totalPrice-recipePrice));
////            System.out.println("Total: " + totalPrice);
////            System.out.println("-----------------------------------------");
////        }
//
//        ArrayList<Item> fullBuild = new ArrayList<>();
//        for(Item x : recommendedItems){
//            fullBuild.add(x);
//        }
//        itemTest.autoBuy(fullBuild, allItems);

    }

}
