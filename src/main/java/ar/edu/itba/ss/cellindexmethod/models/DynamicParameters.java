package ar.edu.itba.ss.cellindexmethod.models;

import java.util.ArrayList;
import java.util.List;

public class DynamicParameters {
    private final List<Position2D> particlePositionList;

    public DynamicParameters() {
        this.particlePositionList = new ArrayList<>();
    }

    // FIXME: quizas no es buena practica darle acceso a la lista + metodo de añadir por separado
    public List<Position2D> getParticlePositionList() {
        return particlePositionList;
    }

    public void addParticlePositionToList(double x, double y) {
        particlePositionList.add(new Position2D(x, y));
    }
}
