import ar.edu.itba.ss.cellindexmethod.models.Grid2D;
import ar.edu.itba.ss.cellindexmethod.models.Particle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BruteForce implements NeighbourDetection{
    private final Grid2D grid;
    private final int M;

    public BruteForce(double L, int M, double rc, List<Particle> particles) {
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

    @Override
    public Map<Long, List<Particle>> calculateNeighbourLists() {
        return null;
    }

    @Override
    public Map<Long, List<Particle>> calculateNeighbourListsPeriodic() {
        return null;
    }
}
