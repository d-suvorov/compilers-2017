#include <stdlib.h>
#include <stdint.h>

int32_t _arrget(const int32_t * arr, size_t idx) {
    return arr[idx + 1];
}

int32_t _arrset(size_t idx, int32_t value, int32_t * arr) {
    arr[idx + 1] = value;
    return 0;
}

int _arrlen(const int32_t * arr) {
    return arr[0];
}

int32_t * _arrmake_impl(size_t length) {
    int32_t * res = (int32_t *) malloc((length + 1) * sizeof(int32_t));
    res[0] = length;
    return res;
}

int32_t * _arrmake(size_t length, int32_t value) {
    int32_t * res = _arrmake_impl(length);
    for (size_t i = 1; i < length + 1; i++) {
        res[i + 1] = value;
    }
    return res;
}

int32_t * _Arrmake(size_t length, const int32_t * init) {
    int32_t * res = _arrmake_impl(length);
    size_t init_length = init[0];
    for (size_t i = 0; i < init_length; i++) {
        res[i + 1] = init[i + 1];
    }
    return res;
}
