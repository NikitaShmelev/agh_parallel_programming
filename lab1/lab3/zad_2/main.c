#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>

#define MAX_WATKI 100

void* zadanie_watku(void* arg) {
    int identyfikator = *(int*)arg;
    pthread_t system_id = pthread_self();
    printf("Watek ID: %d, pthread_self: %lu\n", identyfikator, system_id);
    return NULL;
}

int main(int argc, char* argv[]) {
    if (argc != 2) {
        fprintf(stderr, "Użycie: %s <liczba_watkow>\n", argv[0]);
        exit(EXIT_FAILURE);
    }

    int liczba_watkow = atoi(argv[1]);
    if (liczba_watkow <= 0 || liczba_watkow > MAX_WATKI) {
        fprintf(stderr, "Błąd: liczba wątków powinna być z zakresu 1..%d\n", MAX_WATKI);
        exit(EXIT_FAILURE);
    }

    pthread_t watki[MAX_WATKI];
    int identyfikatory[MAX_WATKI];

    for (int i = 0; i < liczba_watkow; ++i) {
        identyfikatory[i] = i;
        if (pthread_create(&watki[i], NULL, zadanie_watku, &identyfikatory[i]) != 0) {
            perror("pthread_create");
            exit(EXIT_FAILURE);
        }
    }

    for (int i = 0; i < liczba_watkow; ++i) {
        pthread_join(watki[i], NULL);
    }

    printf("Wszystkie wątki zakończyły działanie.\n");

    return 0;
}

