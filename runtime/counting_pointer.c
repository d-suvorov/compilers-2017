#include "counting_pointer.h"

#include <stdlib.h>

struct count_ptr {
    long count;
    char * data;
};

struct count_ptr * _make_count_ptr(char * raw) {
    struct count_ptr * res = (struct count_ptr *) malloc(sizeof(struct count_ptr));
    res->count = 1;
    res->data = raw; 
    return res;
}

char * _get_raw(const struct count_ptr * ptr) {
    return ptr->data;
}

void _increase_count(struct count_ptr * ptr) {
    ptr->count++;
}

void _decrease_count(struct count_ptr * ptr) {
    if (--ptr->count == 0)
        free(ptr->data);
}
