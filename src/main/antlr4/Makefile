ANTLR4 = java -cp ../../../lib/antlr-4.6-complete.jar org.antlr.v4.Tool
GRAMMAR = Language.g4
ANTLR4_OPT = -no-listener -visitor -package org.wotopul
TARGET_DIR = ../java/org/wotopul

all:
	$(ANTLR4) $(ANTLR4_OPT) $(GRAMMAR) -o $(TARGET_DIR)

clean:
	rm -f $(TARGET_DIR)/*
