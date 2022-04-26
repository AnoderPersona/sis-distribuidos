#include <stdio.h>
#include <stdlib.h>
#include "mpi.h"
#include <time.h>

int sum_array(int arr[], int lon){
    int suma = 0;

    for(int i = 0; i < lon; i++){
        suma += arr[i];
    }

    return suma;
}

int main(int argc, char **argv){
    
    if(argc < 4){
        printf("Ejecutar como ./prog N V P, donde N es la dimensión de la parcela NxN, V es la cantidad de ovejitas en el campo y P es la cantidad de pasos tope\n");
    }
    int process_rank, process_size, tag=100, root = 0, N = atoi(argv[1]), i, proc_elements, n_elem, ovejas = atoi(argv[2]), pasos = atoi(argv[3]);
    
    MPI_Init(NULL, NULL); //inicializar entorno
    MPI_Comm_rank(MPI_COMM_WORLD, &process_rank); //obtener id proceso, guarda en process_rank
    MPI_Comm_size(MPI_COMM_WORLD, &process_size); //obtener total procesos, guarda en process_size
    n_elem = N*N;
    proc_elements = n_elem/process_size;

    int *parcela = NULL;

    //coordenadas
    int x, y, pos_actual, sentido;


    //ESTA OK
    //Genera matrices dentro del proceso master
    if(process_rank == root){

        srand(time(NULL));

        parcela = (int*)malloc(n_elem*sizeof(int));

        //Creando parcela
        printf("Proceso %d, parcela: \n[\n", root);
        for(int i = 0; i < n_elem; ++i){
            parcela[i] = 0;
            printf(" %d", parcela[i]);
            if ((i+1)%N==0) printf("\n");
        }
        printf("]\n");

        //añadiendo ovejitas
        for(int i = 0; i < ovejas;i++){
            
            x = rand() % N;
            y = rand() % N;

            while (parcela[x+(y*N)] == 1){
                x = rand() % N;
                y = rand() % N;
            }

            parcela[x+(y*N)] = 1;
            printf("Oveja %d en la posicion (%d,%d)\n", i+1, x,y);
        
        }
        printf("Nueva parcela: \n[\n");
        for(int i = 0; i < n_elem; ++i){
            printf(" %d", parcela[i]);
            if ((i+1)%N==0) printf("\n");
        }
        printf("]\n");
    }
    MPI_Barrier(MPI_COMM_WORLD);
    //int *gather_ovejitas = (int*)malloc(process_size*sizeof(int));
    int *particion_parcela = (int*)malloc(n_elem*sizeof(int));
    
    //MPI_Scatter(parcela, proc_elements*N, MPI_INT, proc_elements*N, particion_parcela, MPI_INT, root, MPI_COMM_WORLD);
    MPI_Scatter(parcela, n_elem, MPI_INT, particion_parcela, n_elem, MPI_INT, root, MPI_COMM_WORLD);

    //posicion perrito
    srand(time(NULL)+process_rank);
    x = rand() % proc_elements;
    int ovejitas_encontradas_proc = 0;

    //randomWalk
    for(int i = 0; i < pasos; i++){    //ovejitas_encontradas_total < ovejas){

        printf("proceso %d en paso %d\n", process_rank,i);
        pos_actual = particion_parcela[x];//((y*N)-1)+x];

        //encuentra ovejitas
        if (pos_actual == 1) {
            ovejitas_encontradas_proc++;
            particion_parcela[x]=0;//((y*N)-1)+x] = 0;
            printf("Wuju, el perrito %d encontró una oveja en el espacio (%d) y lleva en total %d\n", process_rank,x, ovejitas_encontradas_proc);
        }   

        //mover perritos

        //movimiento horizontal
        printf("Perrito en pos %d\n",x);//,(y*N-1));

        sentido = rand() % 2; 
        //ve si se mueve hacia derecha o izquierda
        
        if (sentido == 0){
            if (x+1==proc_elements){//(((y*N)/y)-1)+(x+1) > N-1){
                x--;
                printf("Perrito %d se mueve a la izquierda x-1 por borde\n",process_rank);
            }
            else{
                printf("Perrito %d se mueve a la derecha normal\n",process_rank);
                x++; 
            }
        }
        else{
            if (x-1<0){//(((y*N)/y)-1)+(x-1) < 0){
                x++;
                printf("Perrito %d se mueve a la derecha x+1 por borde\n",process_rank);
            }
            else{
                printf("Perrito %d se mueve a la izquierda normal\n",process_rank);
                x--;
            }
            
        }
        
    }

    printf("perrito %d encontró %d ovejas\n",process_rank, ovejitas_encontradas_proc);

    int *gather_ovejitas = (int*)malloc(process_size*sizeof(int));


    MPI_Barrier(MPI_COMM_WORLD);
    //MPI_Gather(particion_parcela, n_elem, MPI_INT, parcela, proc_elements*N, MPI_INT,root, MPI_COMM_WORLD);
    MPI_Gather(&ovejitas_encontradas_proc, 1, MPI_INT, gather_ovejitas, 1, MPI_INT,root, MPI_COMM_WORLD);
    //MPI_Barrier(MPI_COMM_WORLD);
    
    if ( process_rank == root){

        int ovejitas_totales = 0;
        
        printf("Cada proceso encontró:\n");

        for (int i = 0; i < process_size; i++){
            ovejitas_totales+=gather_ovejitas[i];
            printf("El perrito n° %d encontró %d ovejitas\n", i+1, gather_ovejitas[i]);
        }

        printf("En total, se encontraron %d ovejitas en %d pasos\n", ovejitas_totales, pasos);

        if (ovejitas_totales == 0) printf("FRACASO\n");
        else if (ovejitas_totales == ovejas) printf("ÉXITO TOTAL\n");
        else printf("ÉXITO PARCIAL\n");

    }
    MPI_Barrier(MPI_COMM_WORLD);
    MPI_Finalize();

    return 0;
}