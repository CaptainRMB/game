package lawnlayer;

import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;
import java.util.ArrayList;

public class App extends PApplet
{

    public static final int WIDTH = 1260;
    public static final int HEIGHT = 720;
    public static final int FPS = 60;
    public String configPath;
	public PImage grass, concrete, worm, beetle, ball;
    public int level = 0;
    public int color, lives;
    private JSONObject jsonConfigObject;
    private ArrayList<MapData> gameGameMapArrayList;
    private DrawMap drawMap;

    public App()
    {
        this.configPath = "config.json";
    }

    /**
     * Initialise the setting of the window size.
    */
    public void settings()
    {
        this.size(WIDTH, HEIGHT);
    }

    /**
     * Load all resources such as images. Initialise the elements such as the player, enemies and map elements.
    */
    public void setup()
    {
        frameRate(FPS);
        this.textSize(40);
        this.color = color(94, 60, 33);
        // Load images during setup
		this.grass = loadImage(this.getClass().getResource("grass.png").getPath());
        this.concrete = loadImage(this.getClass().getResource("concrete_tile.png").getPath());
        this.worm = loadImage(this.getClass().getResource("worm.png").getPath());
        this.beetle = loadImage(this.getClass().getResource("beetle.png").getPath());
        this.ball = loadImage(this.getClass().getResource("ball.png").getPath());
        this.gameGameMapArrayList = new ArrayList<>();
        readJson();
        this.drawMap = new DrawMap(this, gameGameMapArrayList, level, lives, grass, concrete, worm, beetle, ball);
    }
	
    /**
     * Draw all elements in the game by current frame. 
    */
    public void draw()
    {
        fill(color);
        this.rect(-1,-1, WIDTH+1, HEIGHT+1);
        drawMap.draw();
    }

    public void readJson()
    {
        jsonConfigObject = loadJSONObject("config.json");
        JSONArray levelArray = jsonConfigObject.getJSONArray("levels");

        lives = jsonConfigObject.getInt("lives");

        for (int i = 0; i < levelArray.size(); i++){
            ArrayList<Enemy> enemies = new ArrayList<>();
            JSONObject level_info = levelArray.getJSONObject(i);
            String outlay = level_info.getString("outlay");
            double goal = level_info.getDouble("goal");
            JSONArray enemiesJsonArray = level_info.getJSONArray("enemies");
            for (int j = 0; j < enemiesJsonArray.size(); j++){
                JSONObject enemy_info = enemiesJsonArray.getJSONObject(j);
                enemies.add(new Enemy(enemy_info.getInt("type"), enemy_info.getString("spawn")));
            }
            MapData gameMap = new MapData(outlay, enemies, goal);

            if(gameMap.getCharMap() != null){
                gameGameMapArrayList.add(gameMap);
            }else{
                System.out.println(gameMap.getOutlay() + " is invalid");
            }
        }
    }

    @Override
    public void keyReleased(){
        DrawMap.keyPress = false;
    }

    @Override
    public void keyPressed(){
        DrawMap.keyPress = true;
        this.drawMap.playerMove(keyCode);
    }

    public static void main(String[] args) {
        PApplet.main("lawnlayer.App");
    }
}
