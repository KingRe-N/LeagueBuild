package home.data;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class champData {

    // gets recommended items from u.gg html page and stores them into a 2d array
    public int[][] getItems(String name, int role) throws IOException{
        String lane = "";
        if(role == 1) lane = "top";
        else if(role == 2) lane = "jungle";
        else if(role == 3) lane = "middle";
        else if(role == 4) lane = "adc";
        else lane = "support";

        Document doc = Jsoup.connect("https://u.gg/lol/champions/"+ name +"/build/?role=" + lane).get();
        Elements items = doc.getElementsByClass("_grid-3 _grid-columns media-query media-query_MOBILE_SMALL__MOBILE_LARGE").get(0).getElementsByClass("image-wrapper");;

        ArrayList<int[]> list = new ArrayList<>();
        int index = 0;

        for(Element item : items) {
            list.add(index, new int[2]);
            int     x = 0, y = 0, i = 0;
            Element child = item.child(0).child(0).child(0);
            String position = child.toString().substring(170, 198);
            String numbers = position.replaceAll("[^0-9, ' ', ';']","");

            int imageNumber = Integer.parseInt(child.toString().substring(child.toString().indexOf("item") + 4, child.toString().indexOf(".png")));

            while(numbers.charAt(i) != ' '){
                int next = Character.getNumericValue(numbers.charAt(i));
                x = x * 10 + next;
                i++;
            }
            i++;
            while(numbers.charAt(i) != ';'){
                int next = Character.getNumericValue(numbers.charAt(i));
                y = y * 10 + next;
                i++;
            }

            if(x == 0) x = 1;
            list.get(index)[0] = x;

            if(imageNumber == 0) y = y * 10;
            else if (imageNumber == 1) y = y * 10 + 1;
            else if (imageNumber == 2) y = y * 10 + 2;

            if(y == 0) y = 1;
            list.get(index)[1] = y;
            index++;
        }

        int[][] res = new int[list.size()][2];

        for(int i = 0; i < res.length; i++) {
            res[i][0] = list.get(i)[0];
            res[i][1] = list.get(i)[1];
        }

        return res;
    }

    public Item[] startingItems(int[][] recItems, HashMap<Integer, Item> items){
        Item[] allItems = new Item[recItems.length];
        Item[] res = new Item[3];
        int gold = 501;
        for(Item i: res){ i = null; }

        for (int i = 0; i < recItems.length; i++) {
            int x = recItems[i][0];
            int y = recItems[i][1];
            String keyString = "" + x + y;
            int key = Integer.parseInt(keyString);

            Item currItem = items.get(key);
            allItems[i] = currItem;
        }

        int index = 0;
        for(int i = 0; i < 2; i++){
            if(allItems[i] == null) {
                continue;
            }
            else if(allItems[i].getPrice() <= gold)
            {
                res[index] = allItems[i];
                gold = gold - allItems[i].getPrice();
                index++;
            }
        }

        return res;
    }

    public Item[] finalBuild(int[][] recItems, HashMap<Integer, Item> items){
        Item[] allItems = new Item[recItems.length];
        Item[] finalBuild = new Item[6];
        for(Item i: finalBuild){ i = null; }

        for (int i = 0; i < recItems.length; i++) {
            int x = recItems[i][0];
            int y = recItems[i][1];
            String keyString = "" + x + y;
            int key = Integer.parseInt(keyString);

            Item currItem = items.get(key);
            allItems[i] = currItem;
        }

        // index of when core items are stored
        int indexAll = 2;
        int indexFinal = 0;

        // if first item is corrupting pot
        if(allItems[0].getCode() == 481920) {
            indexAll--;
        }

        // starting items 0-1 or 0
        // core items 2 - 4   or 1 - 3  always the same
        // 4th item could be allItems 5or6  or  4or5
        // 5th item either allItems 7-9     or  6-8
        // 6th item either allItems 10-12   or  9-11

        // CORE ITEMS   items 1 - 3
        for(int i = indexAll; i < indexAll + 3; i++) {
            finalBuild[indexFinal] = allItems[i];
            indexFinal++;
        }
        indexAll += 3;

        // item 4
        finalBuild[indexFinal++] = allItems[indexAll];
        indexAll+=2;

        // item 5
        int skips=0;       // need this 2 determine index of the beginning of 6th item options
        do {
            if (new itemData().itemExist(finalBuild, allItems[indexAll])) {
                indexAll++;
                skips++;
            } else {
                finalBuild[4] = allItems[indexAll++];
                indexFinal = 5;
            }
        }while(indexFinal == 4);

        indexAll = indexAll + (2-skips);

        // item 6
        do {
            if (new itemData().itemExist(finalBuild, allItems[indexAll])) {
                indexAll++;
            } else {
                finalBuild[5] = allItems[indexAll++];
                indexFinal = 6;
            }
        }while(indexFinal == 5);

        // Checks for tiamat in the core build, Seems like the only non finished item that can appear
        // deletes tiamat from final and shifts all items and adds the next best item to the end.
        boolean hasTiamat = false;
        for(int i = 0; i < finalBuild.length; i++){
            if(finalBuild[i].getCode() == 481) {
                hasTiamat = true;
                for(int j = i; j < finalBuild.length-1; j++){
                    Item temp = finalBuild[j+1];
                    finalBuild[j] = finalBuild[j+1];
                }
                break;
            }
        }
        if(hasTiamat) {
            finalBuild[finalBuild.length - 1] = allItems[indexAll];
        }
        return finalBuild;
    }

    // get winrate/rank for respective role
    public void addChamp(String name, int role, HashMap<String, Champion> champs) throws IOException{
        String lane = "";
        if(role == 1) lane = "top";
        else if(role == 2) lane = "jungle";
        else if(role == 3) lane = "middle";
        else if(role == 4) lane = "adc";
        else lane = "support";

        System.out.println("https://u.gg/lol/champions/"+ name +"/build/?role=" + lane);

        Document doc = Jsoup.connect("https://u.gg/lol/champions/"+ name +"/build/?role=" + lane).get();
        String rateString = "";

        for(int i = 5; i > 1; i--){
            rateString = doc.getElementsByClass("win-rate").first().text().substring(0,i);
            if(Character.isDigit(rateString.charAt(i-1))) break;
        }

        double rate = Double.parseDouble(rateString);
        System.out.println("Winrate: " + rate);
        champs.get(name).setWinRate(rate,lane);
    }

    public HashMap<String, Champion> readChamps() throws IOException {
        Scanner s = new Scanner(new File("champs.txt"));
        HashMap<String, Champion> champs= new HashMap<String, Champion>();

        while (s.hasNext()) {
            String champName = s.nextLine();
            String attribute = s.nextLine();
            int cc = Integer.parseInt(s.nextLine());

            Champion champ = new Champion(champName,attribute,cc);
            champs.put(champName, champ);
        }
        return champs;
    }

}
