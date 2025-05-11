#include<stdlib.h>
#include<stdio.h>
#include<omp.h>
#include<math.h>

#define N 1000000

int main() {
    int i;
    double* A = malloc((N+2)*sizeof(double));
    double* B = malloc((N+2)*sizeof(double));
    double* temp = malloc((N+2)*sizeof(double)); // Dodatkowa tablica pomocnicza
    double suma;

    // Inicjalizacja danych
    for(i=0; i<N+2; i++) A[i] = (double)i/N;
    for(i=0; i<N+2; i++) B[i] = 1.0 - (double)i/N;

    // Wersja sekwencyjna
    double t1 = omp_get_wtime();
    for(i=0; i<N; i++) {
        A[i] += A[i+2] + sin(B[i]);
    }
    t1 = omp_get_wtime() - t1;

    suma = 0.0;
    for(i=0; i<N+2; i++) suma += A[i];
    printf("suma %lf, czas obliczen sekwencyjnych %lf\n", suma, t1);

    // Reset danych
    for(i=0; i<N+2; i++) A[i] = (double)i/N;
    for(i=0; i<N+2; i++) B[i] = 1.0 - (double)i/N;

    // Wersja równoległa
    t1 = omp_get_wtime();
    
    #pragma omp parallel num_threads(2) default(none) shared(A, B, temp) private(i)
    {
        // Najpierw obliczamy wszystkie wartości do tablicy tymczasowej
        #pragma omp for
        for(i=0; i<N; i++) {
            temp[i] = A[i+2] + sin(B[i]);
            A[i] += A[i+2] + sin(B[i]);
        }
        
        // Bariera synchronizacyjna - upewniamy się, że wszystkie wartości temp są obliczone
        #pragma omp barrier
        
        // Teraz dodajemy obliczone wartości do A
        #pragma omp for
        for(i=0; i<N; i++) {
            A[i] += temp[i];
        }
    }
    
    t1 = omp_get_wtime() - t1;

    suma = 0.0;
    for(i=0; i<N+2; i++) suma += A[i];
    printf("suma %lf, czas obliczen rownoleglych %lf\n", suma, t1);

    // Zwolnienie pamięci
    free(A);
    free(B);
    free(temp);
    
    return 0;
}