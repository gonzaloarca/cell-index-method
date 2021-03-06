import ar.edu.itba.ss.cellindexmethod.models.DynamicParameters;
import ar.edu.itba.ss.cellindexmethod.models.Particle;
import ar.edu.itba.ss.cellindexmethod.models.Position2D;
import ar.edu.itba.ss.cellindexmethod.models.StaticParameters;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.BiFunction;

public class App {
    private final static BiFunction<Double, Double, Particle> particleGenerator = (x, y) -> new Particle(new Position2D(x, y), 1);
    private final static String OUT_FILE_NAME = "out.txt";
    private final static String STATIC_FILE_NAME = "Static.txt";
    private final static String DYNAMIC_FILE_NAME = "Dynamic.txt";


    public static void main(String[] args) throws IOException {
//        writeRandomParticleFiles(generateDistribution(1000, 20, 0.25), STATIC_FILE_NAME, DYNAMIC_FILE_NAME, 20, 0);
//        run(STATIC_FILE_NAME, DYNAMIC_FILE_NAME, OUT_FILE_NAME, 10, 1, true);
        runCellCountBenchmark(1, 1, 20, 500, 0.25);
        runParticleCountBenchmark(100, 1, 20, 5000, 0.25);
    }

    private static Map<Long, List<Particle>> runBruteForce(int n, double rc, List<Particle> particles) {
        NeighbourDetection bruteForceMethod = new BruteForce(n, rc, particles);
        Map<Long, List<Particle>> neighbourList;

        neighbourList = bruteForceMethod.calculateNeighbourLists();

        return neighbourList;
    }

    private static Map<Long, List<Particle>> runCellIndexMethod(double L, int M, double rc, List<Particle> particles, boolean isPeriodic) {
        NeighbourDetection cellIndexMethod = new CellIndexMethod(L, M, rc, particles);

        Map<Long, List<Particle>> neighbourList;
        if (isPeriodic) {
            neighbourList = cellIndexMethod.calculateNeighbourListsPeriodic();
        } else {
            neighbourList = cellIndexMethod.calculateNeighbourLists();
        }

        return neighbourList;
    }

    public static void runCellCountBenchmark(int cellCountStep, double rc, double L, int particleCount, double particleRadius) throws IOException {
        writeRandomParticleFiles(generateDistribution(particleCount, L, particleRadius), STATIC_FILE_NAME, DYNAMIC_FILE_NAME, L, 0);
        StaticParameters staticParameters = readStaticParametersFromFile(STATIC_FILE_NAME);
        DynamicParameters dynamicParameters = readDynamicParametersFromFile(DYNAMIC_FILE_NAME, staticParameters.getParticleCount());
        List<Particle> particles = createParticleList(dynamicParameters.getParticlePositionList(), staticParameters.getParticleRadiusList());

        PrintWriter printWriter = new PrintWriter(new FileWriter("CellCountBenchmark.txt"));

        int maxM = (int) Math.ceil(L / rc);
        System.out.println(maxM);
        List<Particle> sublist = particles.subList(0, particleCount);

        for (int m = 1; m < maxM; m += cellCountStep) {

            long startTime = System.nanoTime();
            runCellIndexMethod(L, m, rc, sublist, false);
            long endTime = System.nanoTime();
            double millis = ((double) (endTime - startTime)) / 1000000;

            long periodicStartTime = System.nanoTime();
            runCellIndexMethod(L, m, rc, sublist, true);
            long periodicEndTime = System.nanoTime();
            double periodicMillis = ((double) (periodicEndTime - periodicStartTime)) / 1000000;
            printWriter.printf("%d\t%.5f\t%.5f\n", m, millis, periodicMillis);
        }

        printWriter.close();
    }

    public static void runParticleCountBenchmark(int particleStep, double rc, double L, int maxParticleCount, double particleRadius) throws IOException {

        writeRandomParticleFiles(generateDistribution(maxParticleCount, L, particleRadius), STATIC_FILE_NAME, DYNAMIC_FILE_NAME, L, 0);
        StaticParameters staticParameters = readStaticParametersFromFile(STATIC_FILE_NAME);
        DynamicParameters dynamicParameters = readDynamicParametersFromFile(DYNAMIC_FILE_NAME, staticParameters.getParticleCount());
        List<Particle> particles = createParticleList(dynamicParameters.getParticlePositionList(), staticParameters.getParticleRadiusList());

        int M = (int) (L / (2 * rc));

        PrintWriter printWriter = new PrintWriter(new FileWriter("ParticleCountBenchmark.txt"));

        for (int n = particleStep; n < particles.size(); n += particleStep) {
            List<Particle> sublist = particles.subList(0, n);

            long startTimeBrute = System.nanoTime();
            runBruteForce(n, rc, sublist);
            long endTimeBrute = System.nanoTime();
            double millisBrute = ((double) (endTimeBrute - startTimeBrute)) / 1000000;

            long startTime = System.nanoTime();
            runCellIndexMethod(L, M, rc, sublist, false);
            long endTime = System.nanoTime();
            double millis = ((double) (endTime - startTime)) / 1000000;

            long startTimePeriodic = System.nanoTime();
            runCellIndexMethod(L, M, rc, sublist, true);
            long endTimePeriodic = System.nanoTime();
            double millisPeriodic = ((double) (endTimePeriodic - startTimePeriodic)) / 1000000;
            printWriter.printf("%d\t%.5f\t%.5f\t%.5f\n", n, millis, millisPeriodic, millisBrute);
        }

        printWriter.close();
    }


    public static void run(String staticPath, String dynamicPath, String outPath, int M, double rc, boolean isPeriodic) throws IOException {

        StaticParameters staticParameters = readStaticParametersFromFile(staticPath);
        DynamicParameters dynamicParameters = readDynamicParametersFromFile(dynamicPath, staticParameters.getParticleCount());

        double L = staticParameters.getDim();

        Map<Long, List<Particle>> neighbourList = runCellIndexMethod(L, M, rc, createParticleList(dynamicParameters.getParticlePositionList(), staticParameters.getParticleRadiusList()), isPeriodic);

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

    private static void writeRandomParticleFiles(List<Particle> particles, String staticPathname, String dynamicPathname, double L, int time) throws IOException {
        PrintWriter staticPrintWriter = new PrintWriter(new FileWriter(staticPathname));
        staticPrintWriter.printf("%d\n%.4f\n", particles.size(), L);
        PrintWriter dynamicPrintWriter = new PrintWriter(new FileWriter(dynamicPathname));
        dynamicPrintWriter.printf("%d\n", time);
        Position2D position2D;
        for (int i = 0; i < particles.size(); i++) {
            staticPrintWriter.printf("%.4f\t1.0000", particles.get(i).getRadius());
            position2D = particles.get(i).getPosition();
            dynamicPrintWriter.printf("%.7e\t%.7e", position2D.getX(), position2D.getY());
            if (i != particles.size() - 1) {
                staticPrintWriter.println();
                dynamicPrintWriter.println();
            }
        }

        staticPrintWriter.close();
        dynamicPrintWriter.close();
    }

    private static void writeNeighbourListToFile(Map<Long, List<Particle>> neighbourList, String fileName) throws IOException {
        FileWriter fileWriter = new FileWriter(fileName);
        PrintWriter printWriter = new PrintWriter(fileWriter);

        neighbourList.forEach((particleId, particleNeighbours) -> {
            printWriter.printf("%d", particleId);
            particleNeighbours.forEach(neighbour -> printWriter.printf(",%d", neighbour.getId()));
            printWriter.println();
        });

        printWriter.close();
    }

    private static List<Particle> generateDistribution(long N, double L, double radius) {

        List<Particle> particles = new ArrayList<>();
        ;

        for (int i = 0; i < N; ) {
            double xPosition = new Random().nextDouble() * L;
            double yPosition = new Random().nextDouble() * L;
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
