#ifndef GC_H
#define GC_H

void mark_ptr(void ** ptr);
void unmark_ptr(void ** ptr);
void assert_marked(void ** ptr);

struct count_ptr;

struct count_ptr * _make_count_ptr(char * raw);
char * _get_raw(const struct count_ptr * ptr);

void _increase_count(struct count_ptr * ptr);
void _decrease_count(struct count_ptr * ptr);

#endif
