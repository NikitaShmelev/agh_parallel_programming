# kompilator c
CCOMP = /usr/lib64/openmpi/bin/mpicc

# konsolidator
LOADER = /usr/lib64/openmpi/bin/mpicc

# program uruchomieniowy MPI
MPI_run = /usr/lib64/openmpi/bin/mpiexec -oversubscribe

# opcje optymalizacji:
# wersja do debugowania
OPT = -g -DDEBUG
# wersja zoptymalizowana do mierzenia czasu
# OPT = -O3

all: MPI_simple

MPI_simple : MPI_simple.o
	$(LOADER) $(OPT) MPI_simple.o -o MPI_simple $(LIB)

# jak uzyskac plik moj_program.o ?
MPI_simple.o: MPI_simple.c 
	$(CCOMP) -c $(OPT) MPI_simple.c

clean:
	rm -f *.o MPI_simple
