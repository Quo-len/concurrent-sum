#include <mpi.h>
#include <stdio.h>
#include <stdlib.h>
#include <time.h>

typedef long long ll;

int main(int argc, char **argv) {
    MPI_Init(&argc, &argv);

    int rank, world;
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);
    MPI_Comm_size(MPI_COMM_WORLD, &world);

    int N = 5000000; 
    ll *array = NULL;

    if (rank == 0) {
        array = (ll*)malloc(sizeof(ll) * N);
        srand((unsigned)time(NULL));
        for (int i = 0; i < N; i++)
            array[i] = rand() % 100;

        //printf("Initial array: ");
        //for (int i = 0; i < N; i++) printf("%lld ", array[i]);
        //printf("\n");
    }

    MPI_Bcast(&N, 1, MPI_INT, 0, MPI_COMM_WORLD);
    if (rank != 0) array = (ll*)malloc(sizeof(ll) * N);

    int len = N;
    int wave = 0;

    double start_time = MPI_Wtime();

    while (len > 1) {
        MPI_Bcast(array, len, MPI_LONG_LONG, 0, MPI_COMM_WORLD);

        int pairs = len / 2;
        ll *local_res = (ll*)calloc(pairs, sizeof(ll));

        for (int k = rank; k < pairs; k += world) {
            int j = len - 1 - k;
            local_res[k] = array[k] + array[j];
        }

        ll *root_res = NULL;
        if (rank == 0) root_res = (ll*)calloc(pairs, sizeof(ll));

        MPI_Reduce(local_res, root_res, pairs, MPI_LONG_LONG, MPI_SUM, 0, MPI_COMM_WORLD);

        if (rank == 0) {
            wave++;
            for (int i = 0; i < pairs; i++)
                array[i] = root_res[i];

            len = pairs + (len % 2);
            // printf("Wave %d (len=%d): ", wave, len);
            // for (int i = 0; i < len; i++) printf("%lld ", array[i]);
            // printf("\n");
        }

        free(local_res);
        if (rank == 0) free(root_res);

        MPI_Bcast(&len, 1, MPI_INT, 0, MPI_COMM_WORLD);
    }

    double end_time = MPI_Wtime();

    if (rank == 0) {
        // printf("Final sum: %lld\n", array[0]);
        printf("Total time: %.6f seconds\n", end_time - start_time);
    }

    free(array);
    MPI_Finalize();
    return 0;
}
