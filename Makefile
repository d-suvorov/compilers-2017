RUNTIME_DIR = runtime
TESTS_DIR = compiler-tests

.PHONY: all clean runtime

.DEFAULT_GOAL: all

all: runtime build

runtime:
	$(MAKE) -C $(RUNTIME_DIR)

build:
	mvn package

test:
	$(MAKE) -j4 -C $(TESTS_DIR)/core -f checkInterpreter
	$(MAKE) -j4 -C $(TESTS_DIR)/expressions -f checkInterpreter
	$(MAKE) -j4 -C $(TESTS_DIR)/deep-expressions -f checkInterpreter

test-gc:
	$(MAKE) -C $(TESTS_DIR)/gc

clean:
	mvn clean
	$(MAKE) -C $(RUNTIME_DIR) clean

test-clean:
	$(MAKE) -C $(TESTS_DIR)/deep-expressions clean
