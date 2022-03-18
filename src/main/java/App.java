import ar.edu.itba.ss.cellindexmethod.models.DynamicParameters;
import ar.edu.itba.ss.cellindexmethod.models.Particle;
import ar.edu.itba.ss.cellindexmethod.models.Position2D;
import ar.edu.itba.ss.cellindexmethod.models.StaticParameters;

import java.io.*;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class App {
    private final static BiFunction<Double, Double, Particle> particleGenerator = (x, y) -> new Particle(new Position2D(x, y), 1);
    private final static String OUT_FILE_NAME = "out500.txt";
    private final static String STATIC_FILE_NAME = "Static500.txt";
    private final static String DYNAMIC_FILE_NAME = "Dynamic500.txt";


    public static void main(String[] args) throws IOException {
        run(STATIC_FILE_NAME, DYNAMIC_FILE_NAME, OUT_FILE_NAME, 10, 2, true);
//        writeInputFiles(generateDistribution(500, 100, 1), "Static500.txt", "Dynamic500.txt", 100, 0);
    }

    public static void run(String staticPath, String dynamicPath, String outPath, int M, double rc, boolean isPeriodic) throws IOException {

        StaticParameters staticParameters = readStaticParametersFromFile(staticPath);
        DynamicParameters dynamicParameters = readDynamicParametersFromFile(dynamicPath, staticParameters.getParticleCount());

        double L = staticParameters.getDim();

        NeighbourDetection cellIndexMethod = new CellIndexMethod(L, M, rc,
                createParticleList(dynamicParameters.getParticlePositionList(), staticParameters.getParticleRadiusList()));

        Map<Long, List<Particle>> neighbourList;

        long startTime = System.nanoTime();

        if (isPeriodic) {
            neighbourList = cellIndexMethod.calculateNeighbourListsPeriodic();
        } else {
            neighbourList = cellIndexMethod.calculateNeighbourLists();
        }

        long endTime = System.nanoTime();

        double millis = ((double) (endTime - startTime)) / 1000000;
        System.out.println("Elapsed time: " + millis + " ms");

        writeNeighbourListToFile(neighbourList, outPath);
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

    private static void writeInputFiles(List<Particle> particles, String staticPathname, String dynamicPathname, int L, int time) throws IOException {
        PrintWriter staticPrintWriter = new PrintWriter(new FileWriter(staticPathname));
        staticPrintWriter.printf("%d\n%d\n", particles.size(), L);
        PrintWriter dynamicPrintWriter = new PrintWriter(new FileWriter(dynamicPathname));
        dynamicPrintWriter.printf("%d\n", time);
        Position2D position2D;
        for(int i = 0; i< particles.size();i++){
            staticPrintWriter.printf("%.4f\t1.0000",particles.get(i).getRadius());
            position2D = particles.get(i).getPosition();
            dynamicPrintWriter.printf("%.7e\t%.7e",position2D.getX(),position2D.getY());
            if( i != particles.size() - 1){
                staticPrintWriter.println();
                dynamicPrintWriter.println();
            }
        }

        staticPrintWriter.close();
        dynamicPrintWriter.close();
    }

    private static void writeNeighbourListToFile(Map<Long, List<Particle>> neighbourList, String fileName)
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

    private static List<Particle> generateDistribution(long N, long L, double radius) {
        double rightLimit = (double) L;

        List<Particle> particles = new ArrayList<>();
        ;

        for (int i = 0; i < N; ) {
            double xPosition = new Random().nextDouble() * rightLimit;
            double yPosition = new Random().nextDouble() * rightLimit;
            Position2D position = new Position2D(xPosition, yPosition);
            boolean overlaps = false;

            for (Particle p : particles) {
                if (p.isOverlaping(position)) {
                    overlaps = true;
                    break;
                }
            }

            if (!overlaps) {
                particles.add(new Particle(position, radius));
                i++;
            }
        }

        return particles;
    }
}
