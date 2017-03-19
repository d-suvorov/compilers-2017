.PHONY: all clean runtime

.DEFAULT_GOAL: all

all: main runtime

runtime:
	make -C runtime/

main:
	mvn package

clean:
	mvn clean
	make -C runtime/ clean
