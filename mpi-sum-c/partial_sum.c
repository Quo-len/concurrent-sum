#include <mpi.h>
#include <stdio.h>
#include <stdlib.h>
#include <time.h>

int main(int argc, char** argv) {
    MPI_Init(&argc, &argv);

    int rank, size;
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);
    MPI_Comm_size(MPI_COMM_WORLD, &size);

    const int N =  1000000;
    int arr[N];

    if (rank == 0) {
     for (int i = 0; i < N; i++) {
       arr[i] = rand() % 100 + 1;
    }
        srand(time(NULL));
    }

    double start_time = MPI_Wtime();

    MPI_Bcast(arr, N, MPI_INT, 0, MPI_COMM_WORLD);

    int chunk_size = N / size;
    int start = rank * chunk_size;
    int end = (start + chunk_size > N) ? N : start + chunk_size;

    long long local_sum = 0;
    for (int i = start; i < end; i++) {
        local_sum += arr[i];
    }

    long long total_sum = 0;
    MPI_Reduce(&local_sum, &total_sum, 1, MPI_LONG_LONG, MPI_SUM, 0, MPI_COMM_WORLD);

    double end_time = MPI_Wtime();

    if (rank == 0) {
        printf("Total sum: %llu\n", total_sum);
        printf("Elapsed time: %.6f seconds\n", end_time - start_time);
    }

    MPI_Finalize();
    return 0;
}