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
	cd $(TESTS_DIR)
	core/checkInterpreter
	expressions/checkInterpreter
	deep-expressions/checkInterpreter

clean:
	mvn clean
	$(MAKE) -C $(RUNTIME_DIR) clean

test-clean:
	$(MAKE) -C $(TESTS_DIR)/deep-expressions clean
