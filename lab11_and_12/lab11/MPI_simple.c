#include <mpi.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

int main(int argc, char **argv) {
    int rank, size, i;
    MPI_Status status;

    MPI_Init(&argc, &argv);
    MPI_Comm_size(MPI_COMM_WORLD, &size);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);

    if (size > 1) {
        if (rank == 0) {
            int number = -1;
            printf("Proces 0 rozpoczyna wysyłanie.\n");
            MPI_Send(&number, 1, MPI_INT, 1, 0, MPI_COMM_WORLD);
            MPI_Recv(&number, 1, MPI_INT, size-1, 0, MPI_COMM_WORLD, &status);
            printf("Proces 0 odebrał liczbę %d od procesu %d\n", number, status.MPI_SOURCE);
        } 
        else {
            MPI_Recv(&i, 1, MPI_INT, rank-1, 0, MPI_COMM_WORLD, &status);
            printf("Proces %d odebrał liczbę %d od procesu %d\n", rank, i, status.MPI_SOURCE);
            i++;
            MPI_Send(&i, 1, MPI_INT, (rank+1) % size, 0, MPI_COMM_WORLD);
            printf("Proces %d wysłał liczbę %d do procesu %d\n", rank, i, (rank+1) % size);
        }
    }
    else {
        printf("Pojedynczy proces o randze: %d (brak komunikatów)\n", rank);
    }

    MPI_Finalize(); 
    return(0);
}
