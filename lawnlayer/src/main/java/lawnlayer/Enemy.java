package lawnlayer;

public class Enemy
{

    private int type;
    private String spawn;
    private int row = 0;
    private int col = 0;
    private int direction;

    static final int UPLEFT = 0;
    static final int UPRIGHT = 1;
    static final int BOTLEFT = 2;
    static final int BOTRIGHT = 3;


    public Enemy(int type, String spawn) {
        this.type = type;
        this.spawn = spawn;
        this.direction = (int)(Math.random()*4);
    }

    public void playerMove(char[][] cmap, DrawMap dmap){
        if (direction == UPLEFT){
            if (cmap[row-1][col-1] == ' '){
                row --;
                col --;
            }else{
                if (cmap[row][col-1] == 'T'){
                    cmap[row][col-1] = 'R';
                }
                if (cmap[row-1][col] == 'T'){
                    cmap[row-1][col] = 'R';
                }
                if (cmap[row][col-1] != ' '){
                    direction = UPRIGHT;
                }
                if (cmap[row-1][col] != ' '){
                    direction = BOTLEFT;
                }
                if (type == 1){
                    if (cmap[row][col-1] == 'G'){
                        cmap[row][col-1] = ' ';
                        dmap.lossGrass();
                    }
                    if (cmap[row-1][col] == 'G'){
                        cmap[row-1][col] = ' ';
                        dmap.lossGrass();
                    }
                }
            }
        } else if (direction == BOTLEFT) {
            if (cmap[row+1][col-1] == ' '){
                row ++;
                col --;
            }else {
                if (cmap[row][col-1] == 'T') {
                    cmap[row][col-1] = 'R';
                }
                if (cmap[row+1][col] == 'T') {
                    cmap[row+1][col] = 'R';
                }
                if (cmap[row][col-1] != ' ') {
                    direction = BOTRIGHT;
                }
                if (cmap[row+1][col] != ' ') {
                    direction = UPLEFT;
                }
                if (type == 1) {
                    if (cmap[row][col-1] == 'G') {
                        cmap[row][col-1] = ' ';
                        dmap.lossGrass();
                    }
                    if (cmap[row+1][col] == 'G') {
                        cmap[row+1][col] = ' ';
                        dmap.lossGrass();
                    }
                }
            }
        }else if (direction == UPRIGHT) {
            if (cmap[row-1][col+1] == ' '){
                row --;
                col ++;
            }else {
                if (cmap[row][col+1] == 'T') {
                    cmap[row][col+1] = 'R';
                }
                if (cmap[row-1][col] == 'T') {
                    cmap[row-1][col] = 'R';
                }
                if (cmap[row][col+1] != ' ') {
                    direction = UPLEFT;
                }
                if (cmap[row-1][col] != ' ') {
                    direction = BOTRIGHT;
                }
                if (type == 1) {
                    if (cmap[row][col+1] == 'G') {
                        cmap[row][col+1] = ' ';
                        dmap.lossGrass();
                    }
                    if (cmap[row-1][col] == 'G') {
                        cmap[row-1][col] = ' ';
                        dmap.lossGrass();
                    }
                }
            }
        }else if (direction == BOTRIGHT) {
            if (cmap[row+1][col+1] == ' '){
                row ++;
                col ++;
            }else {
                if (cmap[row][col+1] == 'T') {
                    cmap[row][col+1] = 'R';
                }
                if (cmap[row+1][col] == 'T') {
                    cmap[row+1][col] = 'R';
                }
                if (cmap[row][col+1] != ' ') {
                    direction = BOTLEFT;
                }
                if (cmap[row+1][col] != ' ') {
                    direction = UPRIGHT;
                }
                if (type == 1) {
                    if (cmap[row][col+1] == 'G') {
                        cmap[row][col+1] = ' ';
                        dmap.lossGrass();
                    }
                    if (cmap[row+1][col] == 'G') {
                        cmap[row+1][col] = ' ';
                        dmap.lossGrass();
                    }
                }
            }
        }
    }

    public void setRow(int r){
        this.row = r;
    }

    public void setCol(int c){
        this.col = c;
    }

    public String getPosition(){
        return this.spawn;
    }

    public int getType(){
        return this.type;
    }

    public int getRow(){
        return this.row;
    }

    public int getCol(){
        return this.col;
    }

}
