#include <stdlib.h>
#include <stdio.h>
#include <math.h>
#include <omp.h>

#define WYMIAR 10

int main ()
{
  // finish 
  double a[WYMIAR][WYMIAR];

  for(int i=0;i<WYMIAR;i++) for(int j=0;j<WYMIAR;j++) a[i][j]=1.02*i+1.01*j;

  // podwĂłjna pÄtla - sekwencyjnie
  double suma=0.0;
  for(int i=0;i<WYMIAR;i++) {
    for(int j=0;j<WYMIAR;j++) {
      suma += a[i][j];
    }
  }
  
  printf("Suma wyrazĂłw tablicy: %lf\n", suma);

  omp_set_nested(1);

  // podwĂłjna pÄtla - docelowo rĂłwnolegle
  double suma_parallel=0.0; int i,j;
  // ...
  for(i=0;i<WYMIAR;i++) {
    int id_w = omp_get_thread_num();
    // #pragma omp ordered
    // ...
    for(j=0;j<WYMIAR;j++) {
      suma_parallel += a[i][j];
      // #pragma omp ordered
      // dla dekompozycji 1D
      printf("(%1d,%1d)-W_%1d ",i,j,omp_get_thread_num()); 
      // dla dekompozycji 2D
      printf("(%1d,%1d)-W_%1d,%1d ",i,j,id_w,omp_get_thread_num()); 
    }
    // #pragma omp ordered
    printf("\n");
  }

  printf("Suma wyrazĂłw tablicy rĂłwnolegle: %lf\n", suma_parallel);

}