#include <mpi.h>
#include <stdio.h>
#include <stdlib.h>
#include <time.h>

int main(int argc, char** argv) {
    MPI_Init(&argc, &argv);

    int rank, size;
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);
    MPI_Comm_size(MPI_COMM_WORLD, &size);

    int N = 5000000;
    int* arr = NULL;

    if (rank == 0) {
        arr = (int*)malloc(N * sizeof(int));
        if (!arr) {
            fprintf(stderr, "Memory allocation failed\n");
            MPI_Abort(MPI_COMM_WORLD, 1);
        }

        srand(time(NULL));
        for (int i = 0; i < N; i++) {
            arr[i] = rand() % 100 + 1;
        }
    }

    if (rank != 0) {
        arr = (int*)malloc(N * sizeof(int));
        if (!arr) {
            fprintf(stderr, "Memory allocation failed\n");
            MPI_Abort(MPI_COMM_WORLD, 1);
        }
    }

    double start_time = MPI_Wtime();

    MPI_Bcast(arr, N, MPI_INT, 0, MPI_COMM_WORLD);

    int chunk_size = (N + size - 1) / size;
    int start = rank * chunk_size;
    int end = (start + chunk_size > N) ? N : start + chunk_size;

    int local_sum = 0;
    for (int i = start; i < end; i++) {
        local_sum += arr[i];
    }

    int total_sum = 0;
    MPI_Reduce(&local_sum, &total_sum, 1, MPI_INT, MPI_SUM, 0, MPI_COMM_WORLD);

    double end_time = MPI_Wtime();

    if (rank == 0) {
        printf("Total sum: %d\n", total_sum);
        printf("Elapsed time: %.6f seconds\n", end_time - start_time);
    }

    free(arr);
    MPI_Finalize();
    return 0;
}