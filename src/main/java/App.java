import ar.edu.itba.ss.cellindexmethod.models.Particle;
import ar.edu.itba.ss.cellindexmethod.models.Position2D;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class App {
    private final static BiFunction<Double, Double, Particle> particleGenerator = (x, y) -> new Particle(new Position2D(x, y), 1);
    private final static String OUT_FILE_NAME = "out.txt";

    public static void main(String[] args) {
        NeighbourDetection cellIndexMethod = new CellIndexMethod(5, 5, 1, Arrays.asList(
                particleGenerator.apply(0.3, 0.8), //0
                particleGenerator.apply(0.6, 0.8), //1
                particleGenerator.apply(2.6, 0.8), //2
                particleGenerator.apply(0.5, 1.3), //3
                particleGenerator.apply(1.5, 1.3), //4
                particleGenerator.apply(3.1, 1.5), //5
                particleGenerator.apply(4.2, 1.3), //6
                particleGenerator.apply(1.2, 2.1), //7
                particleGenerator.apply(2.5, 2.1), //8
                particleGenerator.apply(1.8, 3.2), //9
                particleGenerator.apply(3.6, 3.2), //10
                particleGenerator.apply(4.9, 4.9) //11
        ));
        Map<Long, List<Particle>> neighbours = cellIndexMethod.calculateNeighbourLists();

        try {
            App.writeNeighbourListToFile(neighbours, OUT_FILE_NAME);
        } catch (IOException e) {
            e.printStackTrace();
        }
        neighbours.forEach((k, v) -> {
            System.out.print(k);
            System.out.println(v);
        });

        NeighbourDetection bruteForce = new BruteForce(12, 1, Arrays.asList(
                particleGenerator.apply(0.3, 0.8), //0
                particleGenerator.apply(0.6, 0.8), //1
                particleGenerator.apply(2.6, 0.8), //2
                particleGenerator.apply(0.5, 1.3), //3
                particleGenerator.apply(1.5, 1.3), //4
                particleGenerator.apply(3.1, 1.5), //5
                particleGenerator.apply(4.2, 1.3), //6
                particleGenerator.apply(1.2, 2.1), //7
                particleGenerator.apply(2.5, 2.1), //8
                particleGenerator.apply(1.8, 3.2), //9
                particleGenerator.apply(3.6, 3.2), //10
                particleGenerator.apply(4.9, 4.9) //11
        ));
        neighbours = bruteForce.calculateNeighbourLists();
        neighbours.forEach((k, v) -> {
            System.out.print(k);
            System.out.println(v);
        });
    }

    public static void writeNeighbourListToFile(Map<Long, List<Particle>> neighbourList, String fileName)
            throws IOException {
        FileWriter fileWriter = new FileWriter(fileName);
        PrintWriter printWriter = new PrintWriter(fileWriter);

        neighbourList.forEach((particleId, particleNeighbours) -> {
            printWriter.printf("%d\t", particleId);
            particleNeighbours.forEach(neighbour -> printWriter.printf("%d ", neighbour.getId()));
            printWriter.println();
        });

        printWriter.close();
    }
}
