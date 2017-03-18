grammar Language;

program
    : statement
    ;

statement
    : ST_SKIP                            # skip
    | first=statement ';' rest=statement # sequence
    | ID ':=' expr                       # assignment
    | ID ':=' READ '(' ')'               # read
    | WRITE '(' expr ')'                 # write
    ;

expr
    : '(' expr ')'                                       # parenthesis
    | left=expr op=(OP_MUL | OP_DIV | OP_MOD) right=expr # infix
    | left=expr op=(OP_ADD | OP_SUB) right=expr          # infix
    | name=ID                                            # variable
    | value=NUM                                          # const
    ;

ST_SKIP : 'skip';
READ    : 'read';
WRITE   : 'write';

OP_MUL : '*';
OP_DIV : '/';
OP_MOD : '%';
OP_ADD : '+';
OP_SUB : '-';

NUM : [0-9]+;
ID  : [a-zA-Z][a-zA-Z0-9]*;
WS  : [ \t\r\n]+ -> skip;