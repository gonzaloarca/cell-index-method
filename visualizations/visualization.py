from turtle import circle
import numpy as np
import matplotlib.pyplot as plt
import pandas as pd


class Particle:
    def __init__(self, x, y, radius):
        self.x = x
        self.y = y
        self.radius = radius
    
    def __str__(self):
        return f'({self.x}, {self.y})'


def plot_particles(L, M, particles=[], rc=5,selected_particle=0,neighbors=[]):
    
    circleCenters = ([p.x for p in particles], [p.y for p in particles])
    circles = []
    for i in range(len(circleCenters[0])):
        if i == selected_particle:
            color = '#00ff00'
        elif i in neighbors:
            color = 'red'
        else:
            color = 'black'
        circles.append(plt.Circle((circleCenters[0][i], circleCenters[1][i]),
                                  particles[i].radius,
                                  color=color,
                                  alpha=1))
        circles.append(plt.Circle((circleCenters[0][i], circleCenters[1][i]),
        rc+ particles[i].radius,
        color='orange',
        alpha=0.1))
    _, ax = plt.subplots()
    ax.set_xlim([0, L])
    ax.set_ylim([0, L])
    ax.set_xticks(np.arange(0, L, L/M))
    ax.set_yticks(np.arange(0, L, L/M))
    plt.grid()
    for circle in circles:
        print(circle)
        ax.add_patch(circle)
    plt.show()


if __name__ == '__main__':

    neighborList = []
    L = -1
    N = -1
    M = 10

    with open('./out.txt') as neighborFile:
        lines = neighborFile.readlines()
        for line in lines:
            aux = [int(i) for i in line[:-1].split(",")[1:]]
            neighborList.append(aux)
    particles_props =[]
    with open('./Static100.txt') as staticProperties:
        lines = staticProperties.readlines()
        N = int(lines[0])
        L = int(lines[1])
        for line in lines[2:]:
            particles_props.append([float(i) for i in line[:-1].split()])

    particles_positions_by_time = []
    currentTime = -1
    with open('./Dynamic100.txt') as dynamicProperties:
        lines = dynamicProperties.readlines()
        for i in range(len(lines)):
            if len(lines[i].split()) == 1:
                currentTime = int((lines[i][:-1].split()[0]))
                particles_positions_by_time.append([])
            else:
                particles_positions_by_time[currentTime].append([float(j) for j in lines[i][:-1].split()])

    particles = []
    for i in range(len(particles_positions_by_time[0])):
        particles.append(Particle(particles_positions_by_time[0][i][0], particles_positions_by_time[0][i][1], particles_props[i][0]))
    selected_particle = 5
    plot_particles(L, M, particles=particles,selected_particle=selected_particle,neighbors=neighborList[selected_particle])
