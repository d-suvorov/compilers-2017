CC = gcc
CFLAGS = -std=c11 -Wall -Wextra -Werror -pedantic -m32

runtime.o: io.o strings.o counting_pointer.o
	ld -m elf_i386 -r io.o strings.o counting_pointer.o -o runtime.o

io.o: io.c
	$(CC) $(CFLAGS) -c io.c -o io.o

strings.o: strings.c counting_pointer.h
	$(CC) $(CFLAGS) -c strings.c -o strings.o

counting_pointer.o: counting_pointer.c counting_pointer.h
	$(CC) $(CFLAGS) -c counting_pointer.c -o counting_pointer.o

clean:
	rm -f runtime.o io.o strings.o counting_pointer.o
