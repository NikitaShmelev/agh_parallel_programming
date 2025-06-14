#include <mpi.h>
#include <stdio.h>
#include <stdlib.h>
#include <math.h>

#ifndef M_PI
  #define M_PI 3.14159265358979323846
#endif

int main(int argc, char **argv) {
    int rank, size;
    long long max_terms;
    double t0, local_sum = 0.0, pi_approx = 0.0;

    MPI_Init(&argc, &argv);  
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);
    MPI_Comm_size(MPI_COMM_WORLD, &size);

    if (rank == 0) {
        // Proces 0 wczytuje liczbę iteracji
        printf("Podaj liczbę wyrazów szeregu Leibniza: ");
        fflush(stdout);
        if (scanf("%lld", &max_terms) != 1) {
            fprintf(stderr, "Błąd wczytywania liczby iteracji\n");
            MPI_Abort(MPI_COMM_WORLD, 1);
        }
    }
    // Rozgłaszamy max_terms do wszystkich procesów
    MPI_Bcast(&max_terms, 1, MPI_LONG_LONG_INT, 0, MPI_COMM_WORLD);

    // Każdy proces zaczyna pomiar czasu tuż przed pętlą
    t0 = MPI_Wtime();

    // Wyznaczamy, które indeksy i ma policzyć każdy proces
    // wersja blokowa:
    long long block = max_terms / size;
    long long start = rank * block;
    long long end   = (rank == size-1 ? max_terms : start + block);

    // Obliczamy swoją część sumy:
    for (long long i = start; i < end; ++i) {
        double term = 1.0 / (2.0 * i + 1.0);
        if (i % 2) term = -term;
        local_sum += term;
    }

    // Zbieramy (redukujemy) sumy lokalne do procesu 0
    MPI_Reduce(&local_sum, &pi_approx, 1, MPI_DOUBLE,
               MPI_SUM, 0, MPI_COMM_WORLD);

    if (rank == 0) {
        // Mnożymy przez 4 i mierzymy czas
        pi_approx *= 4.0;
        double elapsed = MPI_Wtime() - t0;
        printf("\nWynik równoległy:\n"
               "  π ≈ %.15lf\n"
               "  błąd = %.2e\n"
               "  czas obliczeń = %.6lfs\n",
               pi_approx, fabs(pi_approx - M_PI), elapsed);
        printf("  wartość biblioteczna M_PI = %.15lf\n", M_PI);
    }

    MPI_Finalize();
    return 0;
}
