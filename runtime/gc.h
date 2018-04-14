#ifndef GC_H
#define GC_H

#include <stdint.h>

/* A raw integer constant */
#define MARKED_NULL 1u

#define STRING_TAG 0
#define  ARRAY_TAG 1

void _mark_ptr(void ** ptr);
void _unmark_ptr(void ** ptr);
void _assert_marked(void ** ptr);

struct count_ptr;

struct count_ptr * _make_count_ptr(char * raw, int ref_type);
char * _get_as_string(const struct count_ptr * ptr);
int32_t * _get_as_array32(const struct count_ptr * ptr);

void _increase_count(struct count_ptr * ptr);
void _decrease_count(struct count_ptr * ptr);

#endif
