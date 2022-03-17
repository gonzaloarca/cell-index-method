package ar.edu.itba.ss.cellindexmethod.models;

import java.util.ArrayList;
import java.util.List;

public class StaticParameters {
    private final double dim;
    private final int particleCount;
    private final List<Double> particleRadiusList;

    public StaticParameters(int particleCount, double dim) {
        this.particleCount = particleCount;
        this.dim = dim;
        this.particleRadiusList = new ArrayList<>();
    }

    public int getParticleCount() {
        return particleCount;
    }

    public double getDim() {
        return dim;
    }

    // FIXME: quizas no es buena practica darle acceso a la lista + metodo de añadir por separado
    public List<Double> getParticleRadiusList() {
        return particleRadiusList;
    }

    public void addParticleRadiusToList(double radius) {
        particleRadiusList.add(radius);
    }
}
