package pt.ua.smartWars.OnGameData;

/**
 * Created by Tiago on 04/04/2016.
 */
public class Position {
    private double x;
    private double y;
    private String pId;//player id

    public Position(String id, double x, double y){
        this.x=x;
        this.y=y;
        this.pId =id;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }
}
