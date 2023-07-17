package lawnlayer;

public class Ball
{
    int row, col;

    public Ball(int row, int col)
    {
        this.row = row;
        this.col = col;
    }

    public int getRow(){
        return this.row;
    }

    public int getCol(){
        return this.col;
    }

    public void setRow(int r){
        this.row = r;
    }

    public void setCol(int c){
        this.col = c;
    }

    public void setOri(){
        this.row = 0;
        this.col = 0;
    }

}
