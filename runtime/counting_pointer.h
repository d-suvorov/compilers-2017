#ifndef COUNTING_POINTER_H
#define COUNTING_POINTER_H

struct count_ptr;

struct count_ptr * make_count_ptr(char * raw);
char * get(struct count_ptr * ptr);

void increase_count(struct count_ptr * ptr);
void decrease_count(struct count_ptr * ptr);

#endif
