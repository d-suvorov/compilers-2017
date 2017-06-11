CC = gcc
CFLAGS = -g -std=c99 -Wall -Wextra -Werror -pedantic -m32

runtime.o: io.o strings.o arrays.o gc.o arithmetics.o
	ld -m elf_i386 -r io.o strings.o arrays.o gc.o arithmetics.o -o runtime.o

io.o: io.c
	$(CC) $(CFLAGS) -c io.c -o io.o

strings.o: strings.c gc.h
	$(CC) $(CFLAGS) -c strings.c -o strings.o

arrays.o: arrays.c gc.h
	$(CC) $(CFLAGS) -c arrays.c -o arrays.o

gc.o: gc.c gc.h
	$(CC) $(CFLAGS) -c gc.c -o gc.o

arithmetics.o: arithmetics.c arithmetics.h
	$(CC) $(CFLAGS) -c arithmetics.c -o arithmetics.o

clean:
	rm -f runtime.o io.o strings.o arrays.o gc.o arithmetics.o
