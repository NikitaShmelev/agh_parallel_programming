# Use an official GCC image with OpenMP support
FROM gcc:latest

# Set working directory
WORKDIR /usr/src/app

# Copy your C files into the container
COPY openmp_watki_zmienne.c .
COPY Makefile  .

# Install any dependencies (GCC already comes with OpenMP in gcc:latest)

# Compile the programs
RUN make

# Command to run openmp_petle_simple by default
CMD ["./openmp_watki_zmienne"]
