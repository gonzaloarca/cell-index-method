import ar.edu.itba.ss.cellindexmethod.models.DynamicParameters;
import ar.edu.itba.ss.cellindexmethod.models.Particle;
import ar.edu.itba.ss.cellindexmethod.models.Position2D;
import ar.edu.itba.ss.cellindexmethod.models.StaticParameters;

import java.io.*;
import java.util.*;
import java.util.function.BiFunction;

public class App {
    private final static BiFunction<Double, Double, Particle> particleGenerator = (x, y) -> new Particle(new Position2D(x, y), 1);
    private final static String OUT_FILE_NAME = "out.txt";
    private final static String STATIC_FILE_NAME = "Static100.txt";
    private final static String DYNAMIC_FILE_NAME = "Dynamic100.txt";


    public static void main(String[] args) throws IOException {

        StaticParameters staticParameters = readStaticParametersFromFile(STATIC_FILE_NAME);
        DynamicParameters dynamicParameters = readDynamicParametersFromFile(DYNAMIC_FILE_NAME, staticParameters.getParticleCount());

        NeighbourDetection cellIndexMethod = new CellIndexMethod(staticParameters.getDim(), 5, 6,
                createParticleList(dynamicParameters.getParticlePositionList(), staticParameters.getParticleRadiusList()));

        Map<Long, List<Particle>> neighbours = cellIndexMethod.calculateNeighbourLists();

        writeNeighbourListToFile(neighbours, OUT_FILE_NAME);

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

        ArrayList<Particle>[] particles = generateDistribution(10, 5, 5, 2.0);
        for(int i = 0; i < 5; i++) {
            for (Particle p: particles[i]) {
                System.out.println(p.getPosition());
            }
        }
    }

    private static StaticParameters readStaticParametersFromFile(String fileName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));

        String particleCountStr = reader.readLine().trim();
        String dimStr = reader.readLine().trim();

        int particleCount = Integer.parseInt(particleCountStr);

        StaticParameters parameters = new StaticParameters(particleCount, Double.parseDouble(dimStr));

        for (int i = 0; i < particleCount; i++) {
            String radiusStr = reader.readLine().trim().split("\\s+")[0];
            parameters.addParticleRadiusToList(Double.parseDouble(radiusStr));
        }

        reader.close();

        return parameters;
    }

    private static DynamicParameters readDynamicParametersFromFile(String fileName, int particleCount) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));

        String time = reader.readLine();

        DynamicParameters parameters = new DynamicParameters();

        for (int i = 0; i < particleCount; i++) {
            String[] particlePositionTokens = reader.readLine().trim().split("\\s+");

            double x = Double.parseDouble(particlePositionTokens[0]);
            double y = Double.parseDouble(particlePositionTokens[1]);

            parameters.addParticlePositionToList(x, y);
        }

        reader.close();

        return parameters;
    }

    private static List<Particle> createParticleList(List<Position2D> positionList, List<Double> radiusList) {
        List<Particle> particleList = new ArrayList<>();

        for (int i = 0; i < positionList.size(); i++) {
            particleList.add(new Particle(positionList.get(i), radiusList.get(i)));
        }

        return particleList;
    }

    public static void writeNeighbourListToFile(Map<Long, List<Particle>> neighbourList, String fileName)
            throws IOException {
        FileWriter fileWriter = new FileWriter(fileName);
        PrintWriter printWriter = new PrintWriter(fileWriter);

        neighbourList.forEach((particleId, particleNeighbours) -> {
            printWriter.printf("%d", particleId);
            particleNeighbours.forEach(neighbour -> printWriter.printf(",%d", neighbour.getId()));
            printWriter.println();
        });

        printWriter.close();
    }

    private static ArrayList<Particle>[] generateDistribution(long N, int M, long L, double radius) {
        double rightLimit = (double) L;

        ArrayList<Particle>[] particles = new ArrayList[M];

        for (int i = 0; i < N; ) {
            double xPosition = new Random().nextDouble() * rightLimit;
            double yPosition = new Random().nextDouble() * rightLimit;
            int row = (int) Math.floor(xPosition);
            Position2D position = new Position2D(xPosition, yPosition);
            boolean overlaps = false;

            if(particles[row] == null) {
                particles[row] = new ArrayList<>();
            }

            for (Particle p : particles[row]) {
                if(p.isOverlaping(position)) {
                    overlaps = true;
                    break;
                }
            }

            if(!overlaps) {
                particles[row].add(new Particle(position, radius));
                i++;
            }
        }

        return particles;
    }
}
