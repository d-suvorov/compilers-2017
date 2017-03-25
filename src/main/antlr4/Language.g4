grammar Language;

program
    : stmt
    ;

stmt
    : ST_SKIP                              # skip
    | first=stmt ';' rest=stmt             # sequence
    | ID ':=' expr                         # assignment
    | ID ':=' READ '(' ')'                 # read
    | WRITE '(' expr ')'                   # write
    | IF expr THEN thenClause=stmt elif*
      (ELSE elseClause=stmt)? FI           # if
    ;

elif
    : ELIF expr THEN stmt
    ;

expr
    : '(' expr ')'                                      # parenthesis
    | left=expr op=('*' | '/' | '%')         right=expr # infix
    | left=expr op=('+' | '-')               right=expr # infix
    | left=expr op=('<' | '<=' | '>' | '>=') right=expr # infix
    | left=expr op=('==' | '!=')             right=expr # infix
    | left=expr op='&&'                      right=expr # infix
    | left=expr op='||'                      right=expr # infix
    | name=ID                                           # variable
    | value=NUM                                         # const
    ;

ST_SKIP : 'skip';
READ    : 'read';
WRITE   : 'write';
IF      : 'if';
THEN    : 'then';
ELIF    : 'elif';
ELSE    : 'else';
FI      : 'fi';

NUM : [0-9]+;
ID  : [a-zA-Z][a-zA-Z0-9]*;
WS  : [ \t\r\n]+ -> skip;