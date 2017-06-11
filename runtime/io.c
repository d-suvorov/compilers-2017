#include <stdio.h>

#include "arithmetics.h"

void write(int val) {
    printf("%d\n", from_marked(val));
}

int read() {
    int val;
    printf("> ");
    scanf("%d", &val);
    return to_marked(val);
}
