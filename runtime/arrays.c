#include <stdlib.h>
#include <stdint.h>

#include "gc.h"
#include "arithmetics.h"

int32_t _arrget(const int32_t * arr, size_t idx) {
    return arr[idx + 1];
}

int32_t _arrset(size_t idx, int32_t value, int32_t * arr) {
    _decrease_count((struct count_ptr *) arr[idx + 1]);
    arr[idx + 1] = value;
    return 0;
}

int32_t _arrlen(const int32_t * arr) {
    return arr[0];
}

int32_t * _arrmake_impl(size_t length) {
    int32_t * res = (int32_t *) malloc((length + 1) * sizeof(int32_t));
    res[0] = length;
    return res;
}

int32_t * _arrmake(size_t length, int32_t value) {
    int32_t * res = _arrmake_impl(length);
    for (size_t i = 0; i < length; i++) {
        res[i + 1] = value;
    }
    return res;
}

int32_t * _Arrmake(size_t length, const int32_t * init) {
    int32_t * res = _arrmake_impl(length);
    size_t init_length = init[0];
    size_t i = 0;
    for (; i < init_length; i++) {
        res[i + 1] = init[i + 1];
    }
    for (; i < length; i++) {
        res[i + 1] = MARKED_NULL;
    }
    return res;
}

int32_t _arrget_wrapper(const struct count_ptr * p_arr, size_t idx) {
    return _arrget(_get_as_array32(p_arr), from_marked(idx));
}

int32_t _arrset_wrapper(size_t idx, int32_t value, struct count_ptr * p_arr) {
    return _arrset(from_marked(idx), value, _get_as_array32(p_arr));
}

int32_t _arrlen_wrapper(const struct count_ptr * p_arr) {
    int32_t res = _arrlen(_get_as_array32(p_arr));
    return to_marked(res);
}

struct count_ptr * _arrmake_impl_wrapper(size_t length) {
    return _make_count_ptr((char *) _arrmake_impl(from_marked(length)));
}

struct count_ptr * _arrmake_wrapper(size_t length, int32_t value) {
    return _make_count_ptr((char *) _arrmake(from_marked(length), value));
}

struct count_ptr * _Arrmake_wrapper(size_t length, const struct count_ptr * init) {
    return _make_count_ptr((char *) _Arrmake(from_marked(length), _get_as_array32(init)));
}

