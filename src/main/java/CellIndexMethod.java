import ar.edu.itba.ss.cellindexmethod.models.Grid2D;
import ar.edu.itba.ss.cellindexmethod.models.Particle;
import ar.edu.itba.ss.cellindexmethod.models.Position2D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CellIndexMethod implements NeighbourDetection {
    private final Grid2D grid;
    private final int M;
    private final double L;
    private final double rc;

    public CellIndexMethod(double L, int M, double rc, List<Particle> particles) {
        double cellSize = L / M;
        this.grid = new Grid2D(M, cellSize);
        this.M = M;
        this.L = L;
        this.rc = rc;
        for (Particle particle : particles) {
            this.grid.addParticle(particle);
        }
    }

    @Override
    public Map<Long, List<Particle>> calculateNeighbourLists() {
        Map<Long, List<Particle>> neighbourLists = new HashMap<>();
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < M; j++) {
                Grid2D.Cell cell = grid.get(i, j);

                if (cell == null) continue;

                List<Particle> sameCellNeighbours = cell.getParticles();
                List<Particle> foundParticles;

                addSameCellToMap(neighbourLists, sameCellNeighbours);

                //Top cell
                if (i < M - 1 && grid.get(i + 1, j) != null) {
                    foundParticles = grid.get(i + 1, j).getParticles();
                    addToMap(neighbourLists, sameCellNeighbours, foundParticles);
                }

                //Top right cell
                if (i < M - 1 && j < M - 1 && grid.get(i + 1, j + 1) != null) {
                    foundParticles = grid.get(i + 1, j + 1).getParticles();
                    addToMap(neighbourLists, sameCellNeighbours, foundParticles);
                }

                //Right cell
                if (j < M - 1 && grid.get(i, j + 1) != null) {
                    foundParticles = grid.get(i, j + 1).getParticles();
                    addToMap(neighbourLists, sameCellNeighbours, foundParticles);
                }

                //Bottom right cell
                if (i > 0 && j < M - 1 && grid.get(i - 1, j + 1) != null) {
                    foundParticles = grid.get(i - 1, j + 1).getParticles();
                    addToMap(neighbourLists, sameCellNeighbours, foundParticles);
                }
            }
        }

        return neighbourLists;
    }

    @Override
    public Map<Long, List<Particle>> calculateNeighbourListsPeriodic() {

        Map<Long, List<Particle>> neighbourLists = new HashMap<>();
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < M; j++) {
                Grid2D.Cell cell = grid.get(i, j);

                if (cell == null) continue;

                addSameCellToMap(neighbourLists, cell.getParticles());

                //Top cell
                int columnIndex = j;
                int rowIndex = i == M - 1 ? 0 : i + 1;
                if (grid.get(rowIndex, columnIndex) != null) {
                    addToMapPeriodic(neighbourLists, cell, grid.get(rowIndex, columnIndex));
                }

                //Top right cell
                columnIndex = j == M - 1 ? 0 : j + 1;
                if (grid.get(rowIndex, columnIndex) != null) {
                    addToMapPeriodic(neighbourLists, cell, grid.get(rowIndex, columnIndex));
                }

                //Right cell
                rowIndex = i;
                if (grid.get(rowIndex, columnIndex) != null) {
                    addToMapPeriodic(neighbourLists, cell, grid.get(rowIndex, columnIndex));
                }

                //Bottom right cell
                rowIndex = i == 0 ? M - 1 : i - 1;
                if (grid.get(rowIndex, columnIndex) != null) {
                    addToMapPeriodic(neighbourLists, cell, grid.get(rowIndex, columnIndex));
                }


            }
        }

        return neighbourLists;
    }

    private void addSameCellToMap(Map<Long, List<Particle>> neighbourLists, List<Particle> particles) {

        final int size = particles.size();

        for (int i = 0; i < size; i++) {
            neighbourLists.computeIfAbsent(particles.get(i).getId(), _ignore -> new ArrayList<>());
            for (int j = i + 1; j < size; j++) {
                double r = particles.get(i).getPosition().euclideanDistance(particles.get(j).getPosition()) - particles.get(i).getRadius() - particles.get(j).getRadius();
                if (r <= rc) {
                    neighbourLists.computeIfAbsent(particles.get(j).getId(), _ignore -> new ArrayList<>());
                    neighbourLists.get(particles.get(i).getId()).add(particles.get(j));
                    neighbourLists.get(particles.get(j).getId()).add(particles.get(i));
                }
            }
        }
    }

    private void addToMap(Map<Long, List<Particle>> neighbourLists, List<Particle> particles1, List<Particle> particles2) {

        for (Particle p1 : particles1) {
            neighbourLists.computeIfAbsent(p1.getId(), _ignore -> new ArrayList<>());
            for (Particle p2 : particles2) {
                if (p1.equals(p2)) continue;
                double r = p1.getPosition().euclideanDistance(p2.getPosition()) - p1.getRadius() - p2.getRadius();
                if (r <= rc) {
                    neighbourLists.computeIfAbsent(p2.getId(), _ignore -> new ArrayList<>());
                    neighbourLists.get(p1.getId()).add(p2);
                    neighbourLists.get(p2.getId()).add(p1);
                }
            }
        }
    }

    private Position2D correction(Grid2D.Cell currentCell, Grid2D.Cell neighbourCell) {
        final int currentRow = currentCell.getRow();
        final int currentCol = currentCell.getCol();
        final int neighbourRow = neighbourCell.getRow();
        final int neighbourCol = neighbourCell.getCol();
        final Position2D correctedPosition = new Position2D();


        if (currentRow == 0 && neighbourRow == M - 1) {
            correctedPosition.setY(-L);
        } else if (currentRow == M - 1 && neighbourRow == 0) {
            correctedPosition.setY(L);
        }

        if (currentCol == M - 1 && neighbourCol == 0) {
            correctedPosition.setX(L);
        }

        return correctedPosition;


    }

    private void addToMapPeriodic(Map<Long, List<Particle>> neighbourLists, Grid2D.Cell cell1, Grid2D.Cell cell2) {

        final List<Particle> particles1 = cell1.getParticles();
        final List<Particle> particles2 = cell2.getParticles();

        for (Particle p1 : particles1) {
            neighbourLists.computeIfAbsent(p1.getId(), _ignore -> new ArrayList<>());
            for (Particle p2 : particles2) {
                if (p1.equals(p2)) continue;
                double r = p1.getPosition().euclideanDistance(p2.getPosition().add(correction(cell1, cell2))) - p1.getRadius() - p2.getRadius();
                if (r <= rc) {
                    neighbourLists.computeIfAbsent(p2.getId(), _ignore -> new ArrayList<>());
                    neighbourLists.get(p1.getId()).add(p2);
                    neighbourLists.get(p2.getId()).add(p1);
                }
            }
        }
    }

}
