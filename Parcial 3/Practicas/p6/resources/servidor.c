#define _GNU_SOURCE
#include <sys/types.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <sys/socket.h>
#include <netdb.h>
#include <arpa/inet.h>
#include <time.h>

#define BUF_SIZE 500
#define PORT "7300"

int main( int argc , char *argv[] ){
    struct addrinfo hints;
    struct addrinfo *result, *rp;
    int sd, s,v=1;
    char hbuf[NI_MAXHOST], sbuf[NI_MAXSERV];
    struct sockaddr_storage peer_addr;
    socklen_t peer_addr_len, ctam;
    ssize_t nread;
    char buf[BUF_SIZE],enviar[BUF_SIZE],tiempo[7];
    char *facil[] = {"correo","escuela","computadora","telefono","deportivo","tablet"};
    char *medio[] = {"electrocardiograma","ferrocarril","fotosintesis","telecomunicaciones",
                     "fisioterapeuta","paralelepipedo"};
    char *dificil[] = {"aplicaciones para comunicacion en red","escuela superior de computo",
                    "vive cada instante como si fuera el ultimo","protocolo de control de transmisión",
                    "protocolo de datagramas de usuario","olvida aquello que no te permite avanzar"};
    int n;
    FILE* archivo;
    tiempo[6]='\0';
    srand(time(NULL));

    memset(&hints, 0, sizeof(struct addrinfo));
    hints.ai_family = AF_INET6;     /* Allow IPv4 or IPv6 */
    hints.ai_socktype = SOCK_DGRAM; /* Datagram socket */
    hints.ai_flags = AI_PASSIVE;    /* For wildcard IP address */
    hints.ai_protocol = 0;          /* Any protocol */
    hints.ai_canonname = NULL;
    hints.ai_addr = NULL;
    hints.ai_next = NULL;

    s = getaddrinfo( NULL, PORT , &hints , &result );
    if (s != 0) {
        fprintf(stderr, "getaddrinfo: %s\n", gai_strerror(s));
        exit(EXIT_FAILURE);
    }
    /* getaddrinfo() returns a list of address structures.
       Try each address until we successfully bind(2).
       If socket(2) (or bind(2)) fails, we (close the socket
       and) try the next address. */
    for ( rp = result; rp != NULL ; rp = rp->ai_next ) {
        sd = socket( rp->ai_family , rp->ai_socktype , rp->ai_protocol );
        if (sd == -1)
            continue;
	    int op = 0;
        int r = setsockopt( sd, IPPROTO_IPV6, IPV6_V6ONLY, &op, sizeof(op) );
	    if (setsockopt( sd, SOL_SOCKET, SO_REUSEADDR, &v,sizeof(int) ) == -1) {
            perror("setsockopt");
            exit(1);
        }
        if (bind(sd, rp->ai_addr, rp->ai_addrlen) == 0)
            break;       /* Success */
           close(sd);
    }
    if (rp == NULL) {       /* No address succeeded */
        fprintf(stderr, "Could not bind\n");
        exit(EXIT_FAILURE);
    }
    freeaddrinfo(result);   /* No longer needed */

    fprintf(stdout,"Servidor Iniciado en el Puerto %s ...\n",PORT);
    fflush(stdout);
    for ( ; ; ){
        peer_addr_len = sizeof(struct sockaddr_storage);
        memset(enviar,0,BUF_SIZE);
        memset(buf,0,BUF_SIZE);
        n = rand() % 6;
        nread = recvfrom(sd, buf, BUF_SIZE, 0,(struct sockaddr *) &peer_addr, &peer_addr_len);
        printf("Valor recibido %s\n",buf);
        if (nread == -1)
            continue; /* Ignore failed request */
        if ( !strcmp("f",buf) )
            memcpy( enviar,facil[n],strlen(facil[n]) );
        else if ( !strcmp("m",buf) )
            memcpy( enviar,medio[n],strlen(medio[n]) );
        else if ( !strcmp("d",buf) ){
            printf("%d\n",strlen(dificil[n]) );
            memcpy( enviar,dificil[n],strlen(dificil[n]) );
        }
        else if( !strcmp("t",buf) ){
            archivo = fopen("TiemposDeJuego.txt", "at");
            sendto(sd, enviar, BUF_SIZE, 0,(struct sockaddr *) &peer_addr,peer_addr_len);
            nread = recvfrom(sd, buf, BUF_SIZE, 0,(struct sockaddr *) &peer_addr, &peer_addr_len);
            if (nread  == -1)
                continue;
            memcpy(tiempo,buf,6);
            fprintf(archivo,"%s\n",tiempo);
            printf("Tiempo de juego: %s min\n",tiempo);
            sendto(sd, enviar, BUF_SIZE, 0,(struct sockaddr *) &peer_addr,peer_addr_len);
            fclose( archivo );
            continue;
        }
        else
            continue;
        fprintf(stdout,"Palabra enviada: %s\n",enviar);
        fflush(stdout);
        if ( sendto(sd, enviar, BUF_SIZE, 0,(struct sockaddr *) &peer_addr,peer_addr_len) != BUF_SIZE )
            fprintf(stderr, "Error sending response\n");
    }
}