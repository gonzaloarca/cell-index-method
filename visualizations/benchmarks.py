import argparse
import pandas as pd
import matplotlib.pyplot as plt


def plot_particle_count_benchmark(n_list, cell_non_periodic_list, cell_periodic_list, brute_force_list):
    fig = plt.plot(n_list, cell_non_periodic_list, 'o-', n_list,
                   cell_periodic_list, 'o-', n_list, brute_force_list, 'o-')
    plt.legend(fig, ['Cell Index Method (non periodic)',
               'Cell Index Method (periodic)', 'Brute Force'])
    plt.show()


def plot_cell_count_benchmark(m_list, cell_non_periodic_list, cell_periodic_list):
    fig = plt.plot(m_list, cell_non_periodic_list, 'o-', m_list,
                   cell_periodic_list, 'o-')
    plt.legend(fig, ['Cell Index Method (non periodic)',
               'Cell Index Method (periodic)'])
    plt.show()


if __name__ == '__main__':

    parser = argparse.ArgumentParser()
    parser.add_argument("-pcfile", type=argparse.FileType('r'),
                        default=None, help="File of particle count benchmark results", dest="pc_file", required=True)
    parser.add_argument("-ccfile", type=argparse.FileType('r'), default=0,
                        help="File of cell count benchmark results", dest="cc_file", required=True)

    args = parser.parse_args()

    pcData = pd.read_csv(args.pc_file, sep="\t", header=None)
    pcData.columns = ["n", "cell_non_periodic", "cell_periodic", "brute_force"]
    ccData = pd.read_csv(args.cc_file, sep="\t", header=None)
    ccData.columns = ["m", "cell_non_periodic", "cell_periodic"]

    plot_particle_count_benchmark(
        pcData["n"], pcData["cell_non_periodic"], pcData["cell_periodic"], pcData["brute_force"])

    plot_cell_count_benchmark(
        ccData["m"], ccData["cell_non_periodic"], ccData["cell_periodic"]
    )
