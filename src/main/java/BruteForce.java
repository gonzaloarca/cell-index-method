import ar.edu.itba.ss.cellindexmethod.models.Grid2D;
import ar.edu.itba.ss.cellindexmethod.models.Particle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BruteForce implements NeighbourDetection {
    final int N;
    final double rc;
    final List<Particle> particles;

    public BruteForce(int N, double rc, List<Particle> particles) {
        this.N = N;
        this.rc = rc;
        this.particles = new ArrayList<>(N);
        this.particles.addAll(particles);
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
        Map<Long, List<Particle>> neighbourLists = new HashMap<>();
        for (Particle p1 : particles) {
            neighbourLists.computeIfAbsent(p1.getId(), _ignore -> new ArrayList<>());
            for (Particle p2 : particles) {
                if (p1.equals(p2)) continue;
                double r = p1.getPosition().euclideanDistance(p2.getPosition()) - p1.getRadius() - p2.getRadius();
                if (r <= rc) {
                    neighbourLists.get(p1.getId()).add(p2);
                }
            }
        }

        return neighbourLists;
    }

    @Override
    public Map<Long, List<Particle>> calculateNeighbourListsPeriodic() {
        Map<Long, List<Particle>> neighbourLists = new HashMap<>();
        for (Particle p1 : particles) {
            neighbourLists.computeIfAbsent(p1.getId(), _ignore -> new ArrayList<>());
            for (Particle p2 : particles) {
                if (p1.equals(p2)) continue;
                double r = p1.getPosition().euclideanDistance(p2.getPosition()) - p1.getRadius() - p2.getRadius();
                if (r <= rc) {
                    neighbourLists.get(p1.getId()).add(p2);
                }
            }
        }

        return neighbourLists;
    }

}
