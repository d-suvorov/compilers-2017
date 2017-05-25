#include "counting_pointer.h"

#include <stdlib.h>

struct count_ptr {
    long count;
    char * data;
};

struct count_ptr * make_count_ptr(char * raw) {
    struct count_ptr * res = (struct count_ptr *) malloc(sizeof(struct count_ptr));
    res->count = 0;
    res->data = raw; 
    return res;
}

char * get(struct count_ptr * ptr) {
    return ptr->data;
}

void increase_count(struct count_ptr * ptr) {
    ptr->count++;
}

void decrease_count(struct count_ptr * ptr) {
    if (--ptr->count == 0)
        free(ptr->data);
}
