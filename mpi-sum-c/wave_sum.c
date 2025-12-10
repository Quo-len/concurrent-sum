#include <mpi.h>
#include <stdio.h>
#include <stdlib.h>
#include <time.h>

int main(int argc, char **argv) {
    MPI_Init(&argc, &argv);

    int rank, world;
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);
    MPI_Comm_size(MPI_COMM_WORLD, &world);

    const int N = 900000;
    long long array[N];

    if (rank == 0) {
        srand((unsigned)time(NULL));
        for (int i = 0; i < N; i++)
            array[i] = rand() % 100;
    }

    MPI_Bcast((void*)array, N, MPI_LONG_LONG, 0, MPI_COMM_WORLD);

    int len = N;
    int wave = 0;

    double start_time = MPI_Wtime();

    long long local_res[N];
    long long root_res[N];

    while (len > 1) {
        int pairs = len / 2;

        for (int i = 0; i < pairs; i++)
            local_res[i] = 0;

        for (int k = rank; k < pairs; k += world) {
            int j = len - 1 - k;
            local_res[k] = array[k] + array[j];
        }

        for (int i = 0; i < pairs; i++)
            root_res[i] = 0;

        MPI_Reduce(local_res, root_res, pairs, MPI_LONG_LONG, MPI_SUM, 0, MPI_COMM_WORLD);

        if (rank == 0) {
            wave++;

            for (int i = 0; i < pairs; i++)
                array[i] = root_res[i];

            len = pairs + (len % 2);
        }

        MPI_Bcast(&len, 1, MPI_INT, 0, MPI_COMM_WORLD);
        MPI_Bcast(array, len, MPI_LONG_LONG, 0, MPI_COMM_WORLD);
    }

    double end_time = MPI_Wtime();

    if (rank == 0) {
        printf("Final sum: %lld\n", array[0]);
        printf("Total time: %.6f seconds\n", end_time - start_time);
    }

    MPI_Finalize();
    return 0;
}
