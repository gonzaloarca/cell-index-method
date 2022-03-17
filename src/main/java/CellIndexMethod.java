import ar.edu.itba.ss.cellindexmethod.models.Grid2D;
import ar.edu.itba.ss.cellindexmethod.models.Particle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CellIndexMethod {
    private final Grid2D grid;
    private final int M;

    public CellIndexMethod(double L, int M, double rc, List<Particle> particles) {
        double cellSize = L / M;
        this.grid = new Grid2D(M, cellSize);
        this.M = M;
        for (Particle particle : particles) {
            this.grid.addParticle(particle);
        }
    }

    private static void addToMap(Map<Long, List<Particle>> neighbourLists, List<Particle> particles1, List<Particle> particles2) {

        for (Particle p : particles1) {
            neighbourLists.computeIfAbsent(p.getId(), _ignore -> new ArrayList<>());
            neighbourLists.get(p.getId())
                    .addAll(particles2.stream()
                            .filter(p2 -> p2.getId() != p.getId())
                            .collect(Collectors.toList())
                    );
        }
    }

    public Map<Long, List<Particle>> calculateNeighbourLists() {
        Map<Long, List<Particle>> neighbourLists = new HashMap<>();
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < M; j++) {
                Grid2D.Cell cell = grid.get(i, j);

                if (cell == null) continue;

                List<Particle> sameCellNeighbours = grid.get(i, j).getParticles();


                addToMap(neighbourLists, sameCellNeighbours, sameCellNeighbours);

                //Top cell
                if (i < M - 1 && grid.get(i + 1, j) != null) {
                    List<Particle> foundParticles = grid.get(i + 1, j).getParticles();
                    addToMap(neighbourLists, sameCellNeighbours, foundParticles);
                    addToMap(neighbourLists, foundParticles, sameCellNeighbours);

                }

                //Top right cell
                if (i < M - 1 && j < M - 1 && grid.get(i + 1, j + 1) != null) {
                    List<Particle> foundParticles = grid.get(i + 1, j + 1).getParticles();
                    addToMap(neighbourLists, sameCellNeighbours, foundParticles);
                    addToMap(neighbourLists, foundParticles, sameCellNeighbours);
                }

                //Right cell
                if (j < M - 1 && grid.get(i, j + 1) != null) {
                    List<Particle> foundParticles = grid.get(i, j + 1).getParticles();
                    addToMap(neighbourLists, sameCellNeighbours, foundParticles);
                    addToMap(neighbourLists, foundParticles, sameCellNeighbours);
                }

                //Bottom right cell
                if (i > 0 && j < M - 1 && grid.get(i - 1, j + 1) != null) {
                    List<Particle> foundParticles = grid.get(i - 1, j + 1).getParticles();
                    addToMap(neighbourLists, sameCellNeighbours, foundParticles);
                    addToMap(neighbourLists, foundParticles, sameCellNeighbours);
                }
            }
        }

        return neighbourLists;
    }

    public Map<Long, List<Particle>> calculateNeighbourListsPeriodic() {

        Map<Long, List<Particle>> neighbourLists = new HashMap<>();
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < M; j++) {
                Grid2D.Cell cell = grid.get(i, j);

                if (cell == null) continue;

                List<Particle> sameCellNeighbours = grid.get(i, j).getParticles();

                addToMap(neighbourLists, sameCellNeighbours, sameCellNeighbours);

                //Top cell
                int columnIndex = j;
                int rowIndex = i == M - 1 ? 0 : i + 1;
                if (grid.get(rowIndex, columnIndex) != null) {
                    List<Particle> foundParticles = grid.get(rowIndex, columnIndex).getParticles();
                    addToMap(neighbourLists, sameCellNeighbours, foundParticles);
                    addToMap(neighbourLists, foundParticles, sameCellNeighbours);
                }

                //Top right cell
                columnIndex = j == M - 1 ? 0 : j + 1;
                if (grid.get(rowIndex, columnIndex) != null) {
                    List<Particle> foundParticles = grid.get(rowIndex, columnIndex).getParticles();
                    addToMap(neighbourLists, sameCellNeighbours, foundParticles);
                    addToMap(neighbourLists, foundParticles, sameCellNeighbours);
                }

                //Right cell
                rowIndex = i;
                if (grid.get(rowIndex, columnIndex) != null) {
                    List<Particle> foundParticles = grid.get(rowIndex, columnIndex).getParticles();
                    addToMap(neighbourLists, sameCellNeighbours, foundParticles);
                    addToMap(neighbourLists, foundParticles, sameCellNeighbours);
                }

                //Bottom right cell
                rowIndex = i == 0 ? M - 1 : i - 1;
                if (grid.get(rowIndex, columnIndex) != null) {
                    List<Particle> foundParticles = grid.get(rowIndex, columnIndex).getParticles();
                    addToMap(neighbourLists, sameCellNeighbours, foundParticles);
                    addToMap(neighbourLists, foundParticles, sameCellNeighbours);
                }


            }
        }

        return neighbourLists;
    }
}
