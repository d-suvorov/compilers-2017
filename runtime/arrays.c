#include <stdlib.h>
#include <stdint.h>

#include "gc.h"

int32_t _arrget(const int32_t * arr, size_t idx) {
    void ** arr_ptr = (void **) &arr;
    assert_marked(arr_ptr);
    unmark_ptr(arr_ptr);
    return arr[idx + 1];
}

int32_t _arrset(size_t idx, int32_t value, int32_t * arr) {
    void ** arr_ptr = (void **) &arr;
    assert_marked(arr_ptr);
    unmark_ptr(arr_ptr);
    arr[idx + 1] = value;
    return 0;
}

int _arrlen(const int32_t * arr) {
    void ** arr_ptr = (void **) &arr;
    assert_marked(arr_ptr);
    unmark_ptr(arr_ptr);
    return arr[0];
}

int32_t * _arrmake_impl(size_t length) {
    int32_t * res = (int32_t *) malloc((length + 1) * sizeof(int32_t));
    res[0] = length;
    mark_ptr((void **) &res);
    return res;
}

int32_t * _arrmake(size_t length, int32_t value) {
    int32_t * res = _arrmake_impl(length);
    unmark_ptr((void **) &res);
    for (size_t i = 1; i < length + 1; i++) {
        res[i + 1] = value;
    }
    mark_ptr((void **) &res);
    return res;
}

int32_t * _Arrmake(size_t length, const int32_t * init) {
    int32_t * res = _arrmake_impl(length);
    unmark_ptr((void **) &res);
    unmark_ptr((void **) &init);
    size_t init_length = init[0];
    for (size_t i = 0; i < init_length; i++) {
        res[i + 1] = init[i + 1];
    }
    mark_ptr((void **) &res);
    return res;
}
