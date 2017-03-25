grammar Language;

program
    : stmt
    ;

stmt
    : ST_SKIP                                 # skip
    | first=stmt ';' rest=stmt                # sequence
    | ID ':=' expr                            # assignment
    | ID ':=' READ '(' ')'                    # read
    | WRITE '(' expr ')'                      # write
    | IF cond=expr THEN thenClause=stmt elif*
      (ELSE elseClause=stmt)? FI              # if
    | WHILE cond=expr DO body=stmt OD         # while
    | REPEAT body=stmt UNTIL cond=expr        # repeat
    | FOR  init=stmt ','
           cond=expr ','
          after=stmt DO body=stmt OD          # for
    ;

elif
    : ELIF cond=expr THEN elifClause=stmt
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
DO      : 'do';
OD      : 'od';
WHILE   : 'while';
REPEAT  : 'repeat';
UNTIL   : 'until';
FOR     : 'for';

NUM : [0-9]+;
ID  : [a-zA-Z][_a-zA-Z0-9]*;
WS  : [ \t\r\n]+ -> skip;