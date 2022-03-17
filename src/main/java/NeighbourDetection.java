import ar.edu.itba.ss.cellindexmethod.models.Particle;

import java.util.List;
import java.util.Map;

public interface NeighbourDetection {

    Map<Long, List<Particle>> calculateNeighbourLists();

    Map<Long, List<Particle>> calculateNeighbourListsPeriodic();

}
