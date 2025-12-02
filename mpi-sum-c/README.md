```
### Cluster Setup

Create MPI Cluster
```

python set_up_mpi.py (node_count) --cpu (cores) --ram <MB>

```
Example:
```

python set_up_mpi.py 3 --cpu 2 --ram 1024

```
### Compile MPI Program

Compile C file
```

python run_mpi.py --c (file.c)

```
Examples:
```

python run_mpi.py --c partial_sum.c

python run_mpi.py --c wave_sum.c

```
### Run MPI Program

Run executable across cluster
```

python run_mpi.py --r <executable_name> -n <processes>

```
**Examples:**
```

python run_mpi.py --r partial_sum -n 9

python run_mpi.py --r wave_sum -n 9
