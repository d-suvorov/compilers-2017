CC = gcc
CFLAGS = -g -std=c99 -Wall -Wextra -Werror -pedantic -m32

runtime.o: io.o strings.o counting_pointer.o arrays.o
	ld -m elf_i386 -r io.o strings.o counting_pointer.o arrays.o -o runtime.o

io.o: io.c
	$(CC) $(CFLAGS) -c io.c -o io.o

strings.o: strings.c counting_pointer.h
	$(CC) $(CFLAGS) -c strings.c -o strings.o

counting_pointer.o: counting_pointer.c counting_pointer.h
	$(CC) $(CFLAGS) -c counting_pointer.c -o counting_pointer.o

arrays.o: arrays.c counting_pointer.h
	$(CC) $(CFLAGS) -c arrays.c -o arrays.o

clean:
	rm -f runtime.o io.o strings.o counting_pointer.o arrays.o
