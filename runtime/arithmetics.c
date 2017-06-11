#include "arithmetics.h"

#include <stdint.h>

int32_t to_marked(int32_t src) {
    return 2 * src;
}

int32_t from_marked(int32_t src) {
    // hopefully signed (arithmetic) shift
    return src >> 1;
}
