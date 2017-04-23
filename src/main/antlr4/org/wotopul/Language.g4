grammar Language;

program
    : functionDefinition* stmt
    ;

functionDefinition
    : FUN ID '(' params ')' BEGIN stmt END
    ;

params
    : ID (',' ID)*
    |
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
    | RETURN expr                             # returnStatement
    | function_                               # functionStatement
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
    | left=expr op=('||' | '!!')             right=expr # infix
    | name=ID                                           # variable
    | value=NUM                                         # const
    | function_                                         # function
    | CharLiteral                                       # charLiteral
    | StringLiteral                                     # stringLiteral
    | BooleanLiteral                                    # booleanLiteral
    ;

function_
    : ID '(' args ')'
    ;

args
    : expr (',' expr)*
    |
    ;

// Lexer

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
FUN     : 'fun';
BEGIN   : 'begin';
END     : 'end';
RETURN  : 'return';

CharLiteral
    : '\'' Character '\''
    ;

fragment
Character : ~['\\\r\n];

StringLiteral
    :   '"' StringCharacters? '"'
    ;

fragment
StringCharacters
    :   StringCharacter+
    ;

fragment
StringCharacter: ~["\\\r\n];

BooleanLiteral
    : 'true'
    | 'false'
    ;

NUM : [0-9]+;
ID  : [a-zA-Z][_a-zA-Z0-9]*;
WS  : [ \t\r\n]+ -> skip;