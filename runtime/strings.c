#include <stdlib.h>
#include <string.h>

#include "gc.h"

/* Take these from C standard library */
size_t strlen(const char * str);
char * strdup(const char * str);
int strcmp(const char *str1, const char *str2);

char strget(const char * str, size_t idx) {
    return str[idx];
}

int strset(char * str, size_t idx, char chr) {
    str[idx] = chr;
    return 0;
}

char * strsub(const char * str, size_t offset, size_t length) {
    char * res = (char *) malloc((length + 1) * sizeof(char));
    memcpy(res, str + offset, length);
    res[length] = '\0';
    return res;
}

/* `strcat` from C standard library has different semantics */
char * _strcat(const char * str1, const char * str2) {
    size_t len1 = strlen(str1);
    size_t len2 = strlen(str2);
    size_t len = len1 + len2;
    char * res = (char *) malloc((len + 1) * sizeof(char));
    memcpy(res, str1, len1);
    memcpy(res + len1, str2, len2);
    res[len] = '\0';
    return res;
}

char * strmake(size_t length, char chr) {
    char * res = (char *) malloc((length + 1) * sizeof(char));
    memset(res, chr, length);
    res[length] = '\0';
    return res;
}

size_t _strlen_wrapper(const struct count_ptr * p_str) {
    return strlen(_get_raw(p_str));
}

struct count_ptr * _strdup_raw(const char * str) {
    return _make_count_ptr(strdup(str));
}

struct count_ptr * _strdup_wrapper(const struct count_ptr * p_str) {
    return _strdup_raw(_get_raw(p_str));
}

int _strcmp_wrapper(const struct count_ptr * p_str1, const struct count_ptr * p_str2) {
    return strcmp(_get_raw(p_str1), _get_raw(p_str2));
}

char _strget_wrapper(const struct count_ptr * p_str, size_t idx) {
    return strget(_get_raw(p_str), idx);
}

int _strset_wrapper(struct count_ptr * p_str, size_t idx, char chr) {
    return strset(_get_raw(p_str), idx, chr);
}

struct count_ptr * _strsub_wrapper(const struct count_ptr * p_str, size_t offset, size_t length) {
    return _make_count_ptr(strsub(_get_raw(p_str), offset, length));
}

struct count_ptr * _strcat_wrapper(const struct count_ptr * p_str1, const struct count_ptr * p_str2) {
    return _make_count_ptr(_strcat(_get_raw(p_str1), _get_raw(p_str2)));
}

struct count_ptr * _strmake_wrapper(size_t length, char chr) {
    return _make_count_ptr(strmake(length, chr));
}
