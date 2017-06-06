#include <stdlib.h>
#include <stdint.h>

struct array {
    size_t length;
    void * data;
};

struct array * _create_array() {
    struct array * res = (struct array *) malloc(sizeof(struct array));
    return res;
}

int32_t _arrget(const struct array * arr, size_t idx) {
    int32_t * flat_data = (int32_t *) arr->data;
    return flat_data[idx];
}

int32_t _arrset(struct array * arr, int32_t value, size_t idx) {
    int32_t * flat_data = (int32_t *) arr->data;
    flat_data[idx] = value;
    return 0;
}

int _arrlen(const struct array * arr) {
    return arr->length;
}

struct array * _arrmake(size_t length, int32_t value) {
    struct array * res = _create_array();
    res->length = length;
    int32_t * flat_data = (int32_t *) malloc(length * sizeof(int32_t));
    for (size_t i = 0; i < length; i++) {
        flat_data[i] = value;
    }
    res->data = (void *) flat_data;
    return res;
}

struct array * _Arrmake(size_t length, const struct array * init) {
    struct array * res = _create_array();
    res->length = length;
    int32_t * flat_data = (int32_t *) malloc(length * sizeof(int32_t));
    res->data = (void *) flat_data;

    int32_t * init_flat_data = (int32_t *) init->data;
    for (size_t i = 0; i < init->length; i++) {
        flat_data[i] = init_flat_data[i];
    }

    return res;
}
