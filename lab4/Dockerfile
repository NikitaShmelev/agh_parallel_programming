# Use an official GCC image with OpenMP support
FROM gcc:latest

# Set working directory
WORKDIR /usr/src/app

# Copy your C files into the container
COPY openmp_petle_simple.c .
COPY openmp_petle.c .

# Install any dependencies (GCC already comes with OpenMP in gcc:latest)

# Compile the programs
RUN gcc -fopenmp openmp_petle_simple.c -o openmp_petle_simple && \
    gcc -fopenmp openmp_petle.c -o openmp_petle

# Command to run openmp_petle_simple by default
CMD ["./openmp_petle_simple"]
