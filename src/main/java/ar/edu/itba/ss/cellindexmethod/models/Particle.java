package ar.edu.itba.ss.cellindexmethod.models;

import java.util.Objects;

public class Particle {
    private static long _id = 0;
    private long id;
    private Position2D position;
    private double radius;

    public Particle(Position2D position, double radius) {
        this.position = position;
        this.radius = radius;
        this.id = _id++;
    }

    public Position2D getPosition() {
        return position;
    }

    public double getRadius() {
        return radius;
    }

    public long getId() {
        return id;
    }

    @Override
    public String toString() {
        return Long.toString(this.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Particle particle = (Particle) o;
        return id == particle.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
