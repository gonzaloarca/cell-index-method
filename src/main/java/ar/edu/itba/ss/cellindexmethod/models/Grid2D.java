package ar.edu.itba.ss.cellindexmethod.models;

import java.util.ArrayList;
import java.util.List;

public class Grid2D {
    final private Cell[][] grid;
    final private int size;
    final private double cellSize;


    public Grid2D(int size, double cellSize) {
        this.size = size;
        this.grid = new Cell[size][size];
        this.cellSize = cellSize;

    }

    public void addParticle(Particle particle) {
        int i = calculateGridRow(particle.getPosition().getY());
        int j = calculateGridCol(particle.getPosition().getX());

        if (grid[i][j] == null) {
            grid[i][j] = new Cell(i, j);
        }

        grid[i][j].addParticle(particle);
    }

    public Cell get(int i, int j) {
        return grid[i][j];
    }

    private int calculateGridCol(double x) {

        return (int) Math.floor(x / (cellSize));
    }

    private int calculateGridRow(double y) {
        return (int) Math.floor(y / (cellSize));
    }

    static public class Cell {
        final private int row;
        final private int col;
        final private List<Particle> particles;

        public Cell(int row, int col) {
            this.particles = new ArrayList<>();
            this.col = col;
            this.row = row;
        }

        public List<Particle> getParticles() {
            return particles;
        }

        public void addParticle(Particle particle) {
            particles.add(particle);
        }

        public int getRow() {
            return row;
        }

        public int getCol() {
            return col;
        }
    }

}
