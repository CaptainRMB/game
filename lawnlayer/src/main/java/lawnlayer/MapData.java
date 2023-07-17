package lawnlayer;

import jdk.nashorn.internal.runtime.arrays.ArrayLikeIterator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class MapData {

    String outlay,levelName;
    ArrayList<Enemy> enemies;
    double goal;
    char[][] charMap;
    int space;

    public MapData(String outlay, ArrayList<Enemy>enemies,  double goal) {
        this.outlay = outlay;
        this.enemies = enemies;
        this.goal = goal;
        this.space = 0;
        this.charMap = insertCharMap(outlay);
        this.levelName = outlay.split("\\.")[0];
    }

    public ArrayList<Enemy> getEnemies(){
        return this.enemies;
    }

    public double getGoal(){
        return this.goal;
    }

    private char[][] insertCharMap(String path) {

        char[][] charMap = new char[32][63];

        try{
            Scanner scan = new Scanner(new File(path));
            int i = 0;
            while (scan.hasNextLine()){
                charMap[i] = scan.nextLine().toCharArray();
                i++;
            }
            return IsMapValid(charMap);
        }catch (FileNotFoundException e){
            System.out.println("The map file not found");
            return null;
        }
    }

    public char[][] IsMapValid(char[][] charMap) {
        boolean[][] visit = new boolean[charMap.length][charMap[0].length];
        int row = -1;
        int col = -1;
        for (int i = 0; i < 32; i++) {
            if (row == -1) {
                for (int j = 0; j < 63; j++) {
                    if (charMap[i][j] == ' ') {
                        row = i;
                        col = j;
                        break;
                    }
                }
            } else {
                break;
            }
        }

        if (recursive(charMap, row, col, visit)) {
            return charMap;
        } else {
            return null;
        }
    }

    public boolean recursive(char[][] charMap, int row, int col, boolean[][] visit) {
        if (row < 0 || col < 0 || row > 31 || col > 62) {
            return false;
        }

        if (charMap[row][col] == 'X' || visit[row][col]) {
            return true;
        }

        this.space+=1;
        visit[row][col] = true;
        boolean up = recursive(charMap, row - 1, col, visit);
        boolean left = recursive(charMap, row, col - 1, visit);
        boolean right = recursive(charMap, row + 1, col, visit);
        boolean down = recursive(charMap, row, col+1, visit);
        return up && left && right && down;
    }

    public String getOutlay() {
        return this.outlay;
    }

    public char[][] getCharMap() {
        return this.charMap;
    }

    public int getSpace(){
        return this.space;
    }
}

