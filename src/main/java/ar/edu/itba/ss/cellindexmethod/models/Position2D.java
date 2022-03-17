package ar.edu.itba.ss.cellindexmethod.models;

public class Position2D {
    private double x;
    private double y;

    public Position2D() {
        this.x = 0;
        this.y = 0;
    }

    public Position2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double euclideanDistance(Position2D position) {
        final double xDistance = this.x - position.getX();
        final double yDistance = this.y - position.getY();

        return Math.sqrt((xDistance * xDistance) + (yDistance * yDistance));
    }

    public Position2D add(Position2D otherPos) {
        return new Position2D(this.x + otherPos.getX(), this.y + otherPos.getY());
    }

    @Override
    public String toString() {
        return "x:" + this.x + ", y:" + y;
    }
}
