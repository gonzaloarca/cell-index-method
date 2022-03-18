import pathlib
from turtle import circle
import numpy as np
import matplotlib.pyplot as plt
import argparse


class Particle:
    def __init__(self, id, x, y, radius):
        self.id = id
        self.x = x
        self.y = y
        self.radius = radius

    def __str__(self):
        return f'({self.x}, {self.y})'


def plot_particles(L, M, particles=[], rc=5, selected_particle="0", neighbors=[]):

    circleCenters = ([p.x for p in particles], [
                     p.y for p in particles], [p.id for p in particles])
    circles = []
    print(neighbors)
    for i in range(len(circleCenters[0])):
        if circleCenters[2][i] == selected_particle:
            color = '#00ff00'
            circles.append(plt.Circle((circleCenters[0][i], circleCenters[1][i]),
                                      rc + particles[i].radius,
                                      color='orange',
                                      fill=False))
            circles.append(plt.Circle((circleCenters[0][i] + L, circleCenters[1][i]),
                                      rc + particles[i].radius,
                                      color='orange',
                                      fill=False))
            circles.append(plt.Circle((circleCenters[0][i] + L, circleCenters[1][i] + L),
                                      rc + particles[i].radius,
                                      color='orange',
                                      fill=False))
            circles.append(plt.Circle((circleCenters[0][i], circleCenters[1][i] + L),
                                      rc + particles[i].radius,
                                      color='orange',
                                      fill=False))
            circles.append(plt.Circle((circleCenters[0][i], circleCenters[1][i] - L),
                                      rc + particles[i].radius,
                                      color='orange',
                                      fill=False))
            circles.append(plt.Circle((circleCenters[0][i] - L, circleCenters[1][i]),
                                      rc + particles[i].radius,
                                      color='orange',
                                      fill=False))
            circles.append(plt.Circle((circleCenters[0][i] - L, circleCenters[1][i] - L),
                                      rc + particles[i].radius,
                                      color='orange',
                                      fill=False))

        elif circleCenters[2][i] in neighbors:
            color = 'red'
        else:
            color = 'black'
        circles.append(plt.Circle((circleCenters[0][i], circleCenters[1][i]),
                                  particles[i].radius,
                                  color=color,
                                  alpha=1))

    _, ax = plt.subplots()
    ax.set_xlim([0, L])
    ax.set_ylim([0, L])
    ax.set_xticks(np.arange(0, L, L/M))
    ax.set_yticks(np.arange(0, L, L/M))
    ax.set_aspect('auto')
    plt.grid()
    for circle in circles:
        ax.add_patch(circle)
    plt.show()


if __name__ == '__main__':

    parser = argparse.ArgumentParser()
    parser.add_argument("--neighbors", type=argparse.FileType('r'),
                        default=None, help="File of neighbors list", dest="neighbors", required=True)
    parser.add_argument("--selected", type=str, default=0,
                        help="Index of selected particle", dest="selected", required=True)
    parser.add_argument("--rc", type=float, default=5,
                        help="Cut radius", dest="rc", required=True)
    parser.add_argument("--static", type=argparse.FileType('r'),
                        help="Static information file", dest='static', required=True)
    parser.add_argument("--dynamic", type=argparse.FileType('r'),
                        help="Dynamic information file", dest='dynamic', required=True)

    parser.add_argument("--M", type=int,
                        help="Number of cells", dest='M', required=True)
    args = parser.parse_args()

    neighborList = {}
    rc = args.rc
    selected_particle = args.selected
    M = args.M
    ids = []
    with args.neighbors as neighborFile:
        lines = neighborFile.readlines()
        for line in lines:
            currentId = line.split(",")[0]
            ids.append(currentId)
            aux = [i for i in line[:-1].split(",")[1:]]
            neighborList[currentId] = aux
    particles_props = []
    with args.static as staticProperties:
        lines = staticProperties.readlines()
        N = int(lines[0])
        L = float(lines[1])
        for line in lines[2:]:
            particles_props.append([float(i) for i in line[:-1].split()])

    particles_positions_by_time = []
    currentTime = -1
    with args.dynamic as dynamicProperties:
        lines = dynamicProperties.readlines()
        for i in range(len(lines)):
            if len(lines[i].split()) == 1:
                currentTime = int((lines[i][:-1].split()[0]))
                particles_positions_by_time.append([])
            else:
                particles_positions_by_time[currentTime].append(
                    [float(j) for j in lines[i][:-1].split()])

    particles = []
    for i in range(len(particles_positions_by_time[0])):
        particles.append(Particle(ids[i],
                                  particles_positions_by_time[0][i][0], particles_positions_by_time[0][i][1], particles_props[i][0]))
    plot_particles(L, M, rc=rc, particles=particles,
                   selected_particle=selected_particle, neighbors=neighborList[selected_particle])
