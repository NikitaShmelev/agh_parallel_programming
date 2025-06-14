#include<stdlib.h>
#include<stdio.h>
#include<omp.h>

int f_threadprivate = 0;
#pragma omp threadprivate(f_threadprivate)

int main(){
  
#ifdef _OPENMP
  printf("\nKompilator rozpoznaje dyrektywy OpenMP\n");
#endif

  int liczba_watkow;
  
  int a_shared = 1;
  int b_private = 2;
  int c_firstprivate = 3;
  int e_atomic = 5;
  
  // Ustawienie liczby wątków na 5 zgodnie z punktem 9
  omp_set_num_threads(5);
  
  printf("przed wejsciem do obszaru rownoleglego - nr_threads %d, thread ID %d\n",
        omp_get_num_threads(), omp_get_thread_num());
  printf("\ta_shared \t= %d\n", a_shared);
  printf("\tb_private \t= %d\n", b_private);
  printf("\tc_firstprivate \t= %d\n", c_firstprivate);
  printf("\te_atomic \t= %d\n", e_atomic);
  printf("\tf_threadprivate \t= %d\n", f_threadprivate);
  
  // Pierwszy obszar równoległy
#pragma omp parallel default(none) shared(a_shared, e_atomic) private(b_private) firstprivate(c_firstprivate)
  {
    int i;
    int d_local_private;
    
    #pragma omp barrier
    d_local_private = a_shared + c_firstprivate;
    // Sekcja krytyczna dla całej pętli modyfikującej a_shared
    #pragma omp critical
    {
      for(i=0; i<10; i++){
        a_shared++; 
      }
    }
    
    for(i=0; i<10; i++){
      c_firstprivate += omp_get_thread_num();
    }
    
    // Operacje atomowe na e_atomic
    for(i=0; i<10; i++){
      #pragma omp atomic
      e_atomic += omp_get_thread_num();
    }
    
    // Bariera synchronizująca przed wydrukami
    #pragma omp barrier
    
    // Ustawienie wartości f_threadprivate na ID wątku
    f_threadprivate = omp_get_thread_num();
    
    printf("\nw obszarze równoległym: aktualna liczba watkow %d, moj ID %d\n",
          omp_get_num_threads(), omp_get_thread_num());
    printf("\ta_shared \t= %d\n", a_shared);
    printf("\tb_private \t= %d\n", b_private);
    printf("\tc_firstprivate \t= %d\n", c_firstprivate);
    printf("\td_local_private = %d\n", d_local_private);
    printf("\te_atomic \t= %d\n", e_atomic);
    printf("\tf_threadprivate = %d\n", f_threadprivate);
  }
  
  printf("\npo zakonczeniu pierwszego obszaru rownoleglego:\n");
  printf("\ta_shared \t= %d\n", a_shared);
  printf("\tb_private \t= %d\n", b_private);
  printf("\tc_firstprivate \t= %d\n", c_firstprivate);
  printf("\te_atomic \t= %d\n", e_atomic);
  printf("\tf_threadprivate \t= %d\n", f_threadprivate);
  
  // Drugi obszar równoległy
#pragma omp parallel default(none) shared(a_shared, e_atomic) private(b_private) firstprivate(c_firstprivate)
  {
    printf("\nw drugim obszarze równoległym: aktualna liczba watkow %d, moj ID %d\n",
          omp_get_num_threads(), omp_get_thread_num());
    printf("\tf_threadprivate = %d\n", f_threadprivate);
  }
  
  printf("\npo zakonczeniu drugiego obszaru rownoleglego:\n");
  printf("\ta_shared \t= %d\n", a_shared);
  printf("\tb_private \t= %d\n", b_private);
  printf("\tc_firstprivate \t= %d\n", c_firstprivate);
  printf("\te_atomic \t= %d\n", e_atomic);
  printf("\tf_threadprivate \t= %d\n", f_threadprivate);
  
  return 0;
}