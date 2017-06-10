#include "gc.h"

#include <stdlib.h>
#include <stdint.h>
#include <assert.h>

void mark_ptr(void ** ptr) {
    uint32_t * int_ptr = (uint32_t *) ptr;
    *int_ptr = *int_ptr | (uint32_t) 1;
}

void unmark_ptr(void ** ptr) {
    uint32_t * int_ptr = (uint32_t *) ptr;
    *int_ptr = *int_ptr & ~ (uint32_t) 1;
}

void assert_marked(void ** ptr) {
    uint32_t * int_ptr = (uint32_t *) ptr;
    assert(*int_ptr & (uint32_t) 1);
}

struct count_ptr {
    long count;
    char * data;
};

struct count_ptr * _make_count_ptr(char * raw) {
    struct count_ptr * res = (struct count_ptr *) malloc(sizeof(struct count_ptr));
    res->count = 1;
    res->data = raw; 
    mark_ptr((void **) &res);
    return res;
}

char * _get_raw(const struct count_ptr * ptr) {
    assert_marked((void **) &ptr);
    unmark_ptr((void **) &ptr);
    return ptr->data;
}

void _increase_count(struct count_ptr * ptr) {
    assert_marked((void **) &ptr);
    unmark_ptr((void **) &ptr);
    ptr->count++;
}

void _decrease_count(struct count_ptr * ptr) {
    assert_marked((void **) &ptr);
    unmark_ptr((void **) &ptr);
    if (ptr == NULL)
        return;
    if (--ptr->count == 0)
        free(ptr->data);
    free(ptr);
}
