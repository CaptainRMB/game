package lawnlayer;

import processing.core.PApplet;
import processing.core.PImage;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import static processing.core.PConstants.CENTER;

public class DrawMap {

    PApplet p;
    int level;
    int lives;
    PImage grass;
    PImage concrete;
    PImage worm;
    PImage beetle;
    PImage player;
    int numGrass;
    static boolean keyPress;
    Map<Integer, Path> pathMap;
    int playerpathCount;
    boolean start;
    boolean end;
    boolean freeze;
    MapData gMap;
    Ball playerObject;
    ArrayList<Enemy> enemies;
    ArrayList<MapData> gameMaps;
    char[][] charMap;
    int direction = -1;
    int powerRow;
    int powerCol;

    public static final int LEFT = 37;
    public static final int UP = 38;
    public static final int RIGHT = 39;
    public static final int DOWN = 40;
    public static final int GRIDSIZE = 20;
    public static final int TOPBAR = 80;

    long enemyTime, playerTime, autoTime, redTime, powerTime, freezeTime;


    public DrawMap(PApplet p, ArrayList<MapData> gameMaps, int level, int lives, PImage grass, PImage concrete, PImage worm, PImage beetle, PImage player){

        this.p = p;
        this.gameMaps = gameMaps;
        this.level = level;
        this.lives = lives;
        this.grass = grass;
        this.concrete = concrete;
        this.worm = worm;
        this.beetle = beetle;
        this.player = player;

        // this.concreteObject = new Concrete();
        this.playerObject = new Ball(0, 0);
        this.gMap = gameMaps.get(level);
        this.charMap = this.gMap.getCharMap();
        this.enemies = gMap.getEnemies();

        this.enemyTime = System.currentTimeMillis();
        this.playerTime = System.currentTimeMillis();
        this.autoTime = System.currentTimeMillis();
        this.redTime = System.currentTimeMillis();
        this.powerTime = System.currentTimeMillis();
        this.freezeTime = System.currentTimeMillis();

        this.pathMap = new TreeMap<>();
        this.playerpathCount = 0;
        this.numGrass = 0;
        this.powerRow = -1;
        this.powerCol = -1;
        this.freeze = false;
        EnemytLocation();

    }

    public void EnemytLocation(){
        for (int i = 0; i < enemies.size(); i++){
            Enemy tem = enemies.get(i);
            int r = 0;
            int c = 0;

            if (tem.getPosition().equalsIgnoreCase("random")){
                while(charMap[r][c] == 'X'){
                    r = (int)(Math.random() * 32);
                    c = (int)(Math.random() * 63);
                }
                tem.setRow(r);
                tem.setCol(c);
            }else if (tem.getPosition().contains(",")){
                String[] tempArray = tem.getPosition().split(",");
                r = Integer.parseInt(tempArray[0]);
                c = Integer.parseInt(tempArray[1]);
                tem.setRow(r);
                tem.setCol(c);
            }
        }
    }

    public void draw() {
        if(lives != 0) {
            if (level < gameMaps.size()){
                drawText();
                drawEnvironment();
                if (System.currentTimeMillis() - redTime > 48){
                    redSpread();
                    redTime = System.currentTimeMillis();
                }
                if (System.currentTimeMillis() - powerTime > 10000){
                    powerSpawn();
                }
                drawPower();
                checkSurvive();
                drawPlayer();
                if (start && end) {
                    createGrass();
                    start = false;
                    end = false;
                }
                getPower();
                drawEnemies();
                nextLevel();
            }else{
                p.textSize(40);
                p.fill(255,255,255);
                p.text("YOU WIN", 27*GRIDSIZE, TOPBAR+13*GRIDSIZE);
            }
        }else {
            p.textSize(40);
            p.fill(255,255,255);
            p.text("GAME OVER", 27*GRIDSIZE, TOPBAR+13*GRIDSIZE);
        }
    }

    public void nextLevel(){
        int percent, goal;
        percent = (int)Math.round(numGrass * 100 / gMap.getSpace());
        goal = (int)Math.round(100 * gMap.getGoal());

        if (goal <= percent){
            level++;
            if (level < gameMaps.size()){
                System.out.println(level);
                this.gMap = gameMaps.get(level);
                this.charMap = this.gMap.getCharMap();
                this.enemies = gMap.getEnemies();
                this.enemyTime = System.currentTimeMillis();
                this.playerTime = System.currentTimeMillis();
                this.autoTime = System.currentTimeMillis();
                EnemytLocation();
                numGrass = 0;
                playerObject.setOri();
                freeze = false;
                powerRow = -1;
                powerCol = -1;
            }
        }
    }

    public void getPower(){
        if (playerObject.getCol()==powerCol&&playerObject.getRow()==powerRow){
            powerRow = -1;
            powerCol = -1;
            freeze = true;
            powerTime = System.currentTimeMillis();
            freezeTime = System.currentTimeMillis();
        }
        if(System.currentTimeMillis() - freezeTime > 5000){
            freeze = false;
        }
    }

    public void drawEnvironment(){
        for (int i = 0; i < charMap.length; i++){
            for (int j = 0; j < 63; j++){
                if (charMap[i][j] == 'X'){
                    p.image(concrete, j*GRIDSIZE, i*GRIDSIZE+TOPBAR);
                } else if (charMap[i][j] == 'T') {
                    p.fill(p.color(26, 250, 10));
                    p.rect(j*GRIDSIZE, TOPBAR+i*GRIDSIZE, GRIDSIZE, GRIDSIZE);
                } else if (charMap[i][j] == 'G') {
                    p.image(grass, j*GRIDSIZE, i*GRIDSIZE+TOPBAR);
                } else if (charMap[i][j] == 'R') {
                    p.fill(p.color(235, 0, 5));
                    p.rect(j*GRIDSIZE, TOPBAR+i*GRIDSIZE, GRIDSIZE, GRIDSIZE);
                }
            }
        }
    }

    public void checkSurvive(){
        if (charMap[playerObject.getRow()][playerObject.getCol()] == 'R'){
            lives --;
            playerObject.setOri();
            for (int i = 0; i< pathMap.size(); i++){
                charMap[pathMap.get(i).getRow()][pathMap.get(i).getCol()] = ' ';
            }
            pathMap.clear();
            playerpathCount = 0;
        }

        for (int i = 0; i < enemies.size(); i++){
            if (playerObject.getRow() == enemies.get(i).getRow() && playerObject.getCol() == enemies.get(i).getCol()){
                lives --;
                playerObject.setOri();
                for (int ii = 0; ii< pathMap.size(); ii++){
                    charMap[pathMap.get(ii).getRow()][pathMap.get(ii).getCol()] = ' ';
                }
                pathMap.clear();
                playerpathCount = 0;
                break;
            }
        }
    }

    public void redSpread(){
        for (int i = 0; i < pathMap.size(); i++){
            int r = pathMap.get(i).getRow();
            int c = pathMap.get(i).getCol();

            if (charMap[r][c] == 'R'){
                if (i - 1 >= 0 && charMap[pathMap.get(i-1).getRow()][pathMap.get(i-1).getCol()] == 'T'){
                    charMap[pathMap.get(i-1).getRow()][pathMap.get(i-1).getCol()] = 'R';
                }
                if (i + 1 < pathMap.size() && charMap[pathMap.get(i+1).getRow()][pathMap.get(i+1).getCol()] == 'T'){
                    charMap[pathMap.get(i+1).getRow()][pathMap.get(i+1).getCol()] = 'R';
                    break;
                }
            }
        }
    }

    public void drawPower(){
        if (powerRow >= 0 && powerCol >= 0){
            p.ellipseMode(CENTER);
            p.fill(p.color(252, 186, 3));
            p.ellipse(powerCol*GRIDSIZE+10, TOPBAR+powerRow*GRIDSIZE+10, GRIDSIZE, GRIDSIZE);
        }
    }

    public void powerSpawn(){
        powerRow = (int)(Math.random()*32);
        powerCol = (int)(Math.random()*63);
        while(charMap[powerRow][powerCol] == 'T'){
            powerRow = (int)(Math.random()*32);
            powerCol = (int)(Math.random()*63);
        }
        powerTime = System.currentTimeMillis();
    }
    
    public void playerMove(int code){
        int r, c;
        r = playerObject.getRow();
        c = playerObject.getCol();

        if (System.currentTimeMillis() - playerTime > 10){
            if (code == UP){
                if (r-1 >= 0 && charMap[r-1][c] != 'T'){
                    if ((charMap[r][c] == 'X' || charMap[r][c] == 'G') && charMap[r-1][c] == ' '){
                        start = true;
                    }
                    if (charMap[r][c] == 'T' && (charMap[r-1][c] == 'X' || charMap[r-1][c] == 'G')){
                        end = true;
                    }
                    if (charMap[r][c] == 'X' || charMap[r][c] == 'G' || charMap[r][c] == 'T' && direction != DOWN){
                        playerObject.setRow(r-1);
                        direction = UP;
                    }
                }else{
                    if (charMap[r][c] == 'T' && (charMap[r+1][c] == 'X' || charMap[r+1][c] == 'G')){
                        end = true;
                    }
                    if (charMap[r][c] == 'T' && direction == DOWN){
                        playerObject.setRow(r+1);
                    }else if(r-1>=0 && charMap[r-1][c] == 'T'){
                        playerCollideTail();
                    }
                }
            }else if (code == DOWN){
                if (r+1 < 32 && charMap[r+1][c] != 'T'){
                    if ((charMap[r][c] == 'X' || charMap[r][c] == 'G') && charMap[r+1][c] == ' '){
                        start = true;
                    }
                    if (charMap[r][c] == 'T' && (charMap[r+1][c] == 'X' || charMap[r+1][c] == 'G')){
                        end = true;
                    }
                    if (charMap[r][c] == 'X' || charMap[r][c] == 'G' || charMap[r][c] == 'T' && direction != UP){
                        playerObject.setRow(r+1);
                        direction = DOWN;
                    }
                }else {
                    if (charMap[r][c] == 'T' && (charMap[r - 1][c] == 'X' || charMap[r - 1][c] == 'G')) {
                        end = true;
                    }
                    if (charMap[r][c] == 'T' && direction == UP) {
                        playerObject.setRow(r-1);
                    } else if (r + 1 < 32  && charMap[r + 1][c] == 'T') {
                        playerCollideTail();
                    }
                }
            }else if (code == LEFT){
                if (c-1 >= 0 && charMap[r][c-1] != 'T'){
                    if ((charMap[r][c] == 'X' || charMap[r][c] == 'G') && charMap[r][c-1] == ' '){
                        start = true;
                    }
                    if (charMap[r][c] == 'T' && (charMap[r][c-1] == 'X' || charMap[r][c-1] == 'G')){
                        end = true;
                    }
                    if (charMap[r][c] == 'X' || charMap[r][c] == 'G' || charMap[r][c] == 'T' && direction != RIGHT){
                        playerObject.setCol(c-1);
                        direction = LEFT;
                    }
                }else {
                    if (charMap[r][c] == 'T' && (charMap[r][c+1] == 'X' || charMap[r][c+1] == 'G')) {
                        end = true;
                    }
                    if (charMap[r][c] == 'T' && direction == RIGHT) {
                        playerObject.setCol(c+1);
                    } else if (c-1 >= 0 && charMap[r][c-1] == 'T') {
                        playerCollideTail();
                    }
                }
            }else if (code == RIGHT) {
                if (c+1 < 63 && charMap[r][c+1] != 'T'){
                    if ((charMap[r][c] == 'X' || charMap[r][c] == 'G') && charMap[r][c+1] == ' '){
                        start = true;
                    }
                    if (charMap[r][c] == 'T' && (charMap[r][c+1] == 'X' || charMap[r][c+1] == 'G')){
                        end = true;
                    }
                    if (charMap[r][c] == 'X' || charMap[r][c] == 'G' || charMap[r][c] == 'T' && direction != LEFT){
                        playerObject.setCol(c+1);
                        direction = RIGHT;
                    }
                }else {
                    if (charMap[r][c] == 'T' && (charMap[r][c-1] == 'X' || charMap[r][c-1] == 'G')) {
                        end = true;
                    }
                    if (charMap[r][c] == 'T' && direction == LEFT) {
                        playerObject.setCol(c-1);
                    } else if (c+1 < 63 && charMap[r][c+1] == 'T') {
                        playerCollideTail();
                    }
                }
            }
            playerTime = System.currentTimeMillis();
        }
    }
    public void playerCollideTail(){
        lives --;
        playerObject.setRow(0);
        playerObject.setCol(0);
        for (int i = 0; i < pathMap.size(); i++){
            charMap[pathMap.get(i).getRow()][pathMap.get(i).getCol()] = ' ';
        }
        playerpathCount = 0;
        pathMap.clear();
        freeze = false;
    }
    public void playerAutoMove(){
        int r = playerObject.getRow();
        int c = playerObject.getCol();
        if (System.currentTimeMillis() - autoTime > 60) {
            if (direction == UP && charMap[r][c] == 'T') {
                playerMove(UP);
            } else if (direction == DOWN && charMap[r][c] == 'T') {
                playerMove(DOWN);
            } else if (direction == LEFT && charMap[r][c] == 'T') {
                playerMove(LEFT);
            } else if (direction == RIGHT && charMap[r][c] == 'T') {
                playerMove(RIGHT);
            }
            autoTime = System.currentTimeMillis();
        }
    }
    public void lossGrass(){
        numGrass --;
    }
    public void drawPlayer(){
        int r = playerObject.getRow();
        int c = playerObject.getCol();
        p.image(player, c*GRIDSIZE, r*GRIDSIZE+TOPBAR);
        if (charMap[r][c] == ' ') {
            charMap[r][c] = 'T';
            pathMap.put(playerpathCount, new Path(r, c));
            playerpathCount ++;
        }
        if(!keyPress){
            playerAutoMove();
        }
    }

    public void drawEnemies(){
        if (System.currentTimeMillis() - enemyTime > 100){
            if(!freeze){
                for (Enemy e: enemies){
                    e.playerMove(charMap, this);
                }
            }
            enemyTime = System.currentTimeMillis();
        }
        for (Enemy e: enemies){
            if (e.getType() == 0){
                p.image(worm, e.getCol()*GRIDSIZE, e.getRow()* GRIDSIZE+TOPBAR);
            }else{
                p.image(beetle, e.getCol()*GRIDSIZE, e.getRow()* GRIDSIZE+TOPBAR);
            }
        }
    }

    public void createGrass(){
        for (int i = 0; i < 32; i++){
            for (int j = 0; j < 63; j++){
                boolean[][] visit = new boolean[32][63];
                if (charMap[i][j] == ' '&&noEnemy(i, j, visit)){
                    fillGrass(i, j);
                }
            }
        }
        for (int i = 0; i <  pathMap.size(); i++){
            Path p = pathMap.get(i);
            charMap[p.getRow()][p.getCol()] = 'G';
            numGrass++;
        }
        pathMap.clear();
        playerpathCount = 0;
    }

    public boolean noEnemy(int r, int c, boolean[][] visit){
        if (r < 0 || r > 31 || c < 0 || c > 63){return false;}
        for (Enemy e : enemies){
            if (e.getRow() == r && e.getCol() == c){return false;}
        }
        if (charMap[r][c] == 'G' || charMap[r][c] == 'T' || charMap[r][c] == 'X' || charMap[r][c] == 'R' || visit[r][c]){
            return true;
        }
        visit[r][c] = true;
        return noEnemy(r+1, c, visit)&&noEnemy(r-1, c, visit)&&noEnemy(r, c+1, visit)&&noEnemy(r, c-1, visit);
    }

    public void fillGrass(int r, int c){
        if (r < 0 || r > 31 || c < 0 || c > 63){
            return;
        }
        if (charMap[r][c] == 'G' || charMap[r][c] == 'T' || charMap[r][c] == 'X' || charMap[r][c] == 'R'){
            return;
        }
        charMap[r][c] = 'G';
        numGrass ++;

        fillGrass(r+1, c);
        fillGrass(r-1, c);
        fillGrass(r, c-1);
        fillGrass(r, c+1);
    }
    
    public void drawText(){
        int percent = (int)Math.round(numGrass * 100 / gMap.getSpace());
        int goal = (int)Math.round(100 * gMap.getGoal());
        p.fill(255, 255, 255);
        p.text("Lives: " + lives, 10*GRIDSIZE, TOPBAR-2*GRIDSIZE);
        p.fill(255, 255, 255);
        p.text(percent + "%/" + goal + "%", 40*GRIDSIZE, TOPBAR-2*GRIDSIZE);
        p.fill(255, 255, 255);
        p.text("Level " + (level+1), 55*GRIDSIZE, TOPBAR-1*GRIDSIZE);
        p.textSize(27);
    }
}
