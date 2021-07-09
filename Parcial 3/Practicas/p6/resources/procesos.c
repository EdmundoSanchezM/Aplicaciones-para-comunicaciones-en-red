#include<sys/types.h>
#include<sys/wait.h>
#include<unistd.h>
#include<stdio.h>
#include<stdlib.h>

int main( int argc, char * argv[] ){
	
	pid_t pid,pid2;

	pid = fork();
	if( pid == -1)
		perror("Error al crear el proceso hijo: \n");
	else if( pid == 0){
		printf("Yo soy el proceso hijo y mi ID es: %d y el ID de mi padre es: %d\n",getpid(),getppid());

		pid2 = fork();
		if( pid2 == -1 )
			perror("Error al crear el proceso nieto\n");
		else if( pid2 == 0)
			printf("Yo soy el proceso Nieto y mi ID es: %d y el ID de mi padre es: %d\n",getpid(),getppid());
		else
			wait(NULL);
	}
	else{
		printf("Yo soy el proceso padre y mi ID es: %d\n",getpid());
		wait(NULL);
	}
	return 0;
}

