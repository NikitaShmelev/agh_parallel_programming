#include <stdlib.h>
#include <stdio.h>
#include <omp.h>

#define WYMIAR 10

int main() {
    double a[WYMIAR][WYMIAR];
    double suma = 0.0;
    double suma_parallel = 0.0;

    // Inicjalizacja tablicy
    for(int i = 0; i < WYMIAR; i++) {
        for(int j = 0; j < WYMIAR; j++) {
            a[i][j] = 1.02 * i + 1.01 * j;
        }
    }

    // Suma sekwencyjna
    for(int i = 0; i < WYMIAR; i++) {
        for(int j = 0; j < WYMIAR; j++) {
            suma += a[i][j];
        }
    }
    printf("Suma sekwencyjna: %lf\n", suma);

    // Wariant 1: Dekompozycja wierszowa
    suma_parallel = 0.0;
    #pragma omp parallel for default(none) shared(a) reduction(+:suma_parallel) schedule(static, 2) ordered
    for(int i = 0; i < WYMIAR; i++) {
        #pragma omp ordered
        printf("Wiersz %d rozpoczety przez watek %d\n", i, omp_get_thread_num());
        
        for(int j = 0; j < WYMIAR; j++) {
            suma_parallel += a[i][j];
            #pragma omp ordered
            printf("(%d,%d)-W%d ", i, j, omp_get_thread_num());
        }
        #pragma omp ordered
        printf("\n");
    }
    #pragma omp ordered
    printf("Suma równoległa (wierszowa): %lf\n\n", suma_parallel);

    // Wariant 2: Dekompozycja kolumnowa
    suma_parallel = 0.0;
    #pragma omp parallel for default(none) shared(a) reduction(+:suma_parallel) schedule(dynamic) ordered
    for(int j = 0; j < WYMIAR; j++) {
        #pragma omp ordered
        printf("Kolumna %d rozpoczeta przez watek %d\n", j, omp_get_thread_num());
        
        for(int i = 0; i < WYMIAR; i++) {
            suma_parallel += a[i][j];
            #pragma omp ordered
            printf("(%d,%d)-W%d ", i, j, omp_get_thread_num());
        }
        printf("\n");
    }
    #pragma omp ordered
    printf("Suma równoległa (kolumnowa): %lf\n\n", suma_parallel);

    // Wariant 3: Dekompozycja kolumnowa z ręczną redukcją
    suma_parallel = 0.0;
    #pragma omp parallel default(none) shared(a, suma_parallel)
    {
        double local_sum = 0.0;
        #pragma omp for schedule(static) ordered
        for(int j = 0; j < WYMIAR; j++) {
            #pragma omp ordered
            printf("Kolumna %d rozpoczeta przez watek %d\n", j, omp_get_thread_num());
            
            for(int i = 0; i < WYMIAR; i++) {
                local_sum += a[i][j];
                #pragma omp ordered
                printf("(%d,%d)-W%d ", i, j, omp_get_thread_num());
            }
            #pragma omp ordered
            printf("\n");
        }
        #pragma omp critical
        suma_parallel += local_sum;
    }
    #pragma omp ordered
    printf("Suma równoległa (kolumnowa z redukcją ręczną): %lf\n\n", suma_parallel);

    // Wariant 4: Dekompozycja 2D
    suma_parallel = 0.0;
    omp_set_nested(1);
    #pragma omp parallel for default(none) shared(a) reduction(+:suma_parallel) schedule(static, 2) ordered
    for(int i = 0; i < WYMIAR; i++) {
        #pragma omp ordered
        printf("Wiersz %d rozpoczety przez zespół %d\n", i, omp_get_team_num());
        
        #pragma omp parallel for schedule(static, 2)
        for(int j = 0; j < WYMIAR; j++) {
            suma_parallel += a[i][j];
            printf("(%d,%d)-W%d.%d ", i, j, omp_get_team_num(), omp_get_thread_num());
        }
        #pragma omp ordered
        printf("\n");
    }
    #pragma omp ordered
    printf("Suma równoległa (2D): %lf\n", suma_parallel);

    return 0;
}