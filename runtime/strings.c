#include <stdlib.h>
#include <string.h>

#include "counting_pointer.h"

/* Take these from C standard library */
size_t strlen(const char * str);
char * strdup(const char * s);
int strcmp(const char *s1, const char *s2);

char strget(const char * str, size_t idx) {
    return str[idx];
}

int strset(char * str, size_t idx, char chr) {
    str[idx] = chr;
    return 0;
}

char * strsub(char * str, size_t offset, size_t length) {
    char * res = (char *) malloc((length + 1) * sizeof(char));
    memcpy(res, str + offset, length);
    res[length] = '\0';
    return res;
}

/* `strcat` from C standard library has different semantics */
char * _strcat(char * str1, char * str2) {
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
