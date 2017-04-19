RUNTIME_DIR = runtime
TESTS_DIR = compiler-tests

.PHONY: all clean runtime

.DEFAULT_GOAL: all

all: runtime build

runtime:
	$(MAKE) -C $(RUNTIME_DIR)

build:
	mvn package

test: all
	$(MAKE) -C $(TESTS_DIR)/deep-expressions -j4 -s

clean:
	mvn clean
	$(MAKE) -C $(RUNTIME_DIR) clean

test-clean:
	$(MAKE) -C $(TESTS_DIR)/deep-expressions clean
