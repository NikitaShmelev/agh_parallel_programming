## how to run

```bash
gcc-14 -O3 -fopenmp calka_omp.c -o calka; export OMP_NUM_THREADS=8 && ./calka
```



```bash
mpicc mat_vec_row_MPI.c; mpiexec -n 4 ./a.out
```
