grammar Language;

@header {
import java.util.*;
import java.util.stream.*;
}

@parser::members {

private Set<String> create_set(String... elements) {
    return new HashSet<>(Arrays.asList(elements));
}

private Set<String> join_sets(Set<String>... sets) {
    return Arrays.stream(sets)
        .flatMap(Collection::stream)
        .collect(Collectors.toCollection(HashSet::new));
}

}

program
    : functionDefinition* stmt
    ;

functionDefinition returns [Set<String> locals]
    : FUN ID '(' params ')' BEGIN stmt END
      { $locals = $stmt.locals; }
    ;

params
    : ID (',' ID)*
    |
    ;

stmt returns [Set<String> locals]
    : ST_SKIP
      { $locals = create_set(); }                           # skip
    | first=stmt ';' rest=stmt
      { $locals = join_sets($first.locals, $rest.locals); } # sequence
    | ID ':=' expr
      { $locals = create_set($ID.text); }                   # assignment
    | ID ':=' READ '(' ')'
      { $locals = create_set($ID.text); }                   # read
    | WRITE '(' expr ')'
      { $locals = create_set(); }                           # write
    | IF cond=expr THEN thenClause=stmt (elifs+=elif)*
      (elseKeyword=ELSE elseClause=stmt)? FI
      {
          Set<String> res = create_set();
          res.addAll($thenClause.locals);
          if ($elseKeyword != null)
              res.addAll($elseClause.locals);
          for (ElifContext elifCtx : $elifs)
              res.addAll(elifCtx.locals);
          $locals = res;
      }                                                     # if
    | WHILE cond=expr DO body=stmt OD
      { $locals = join_sets($body.locals); }                # while
    | REPEAT body=stmt UNTIL cond=expr
      { $locals = join_sets($body.locals); }                # repeat
    | FOR  init=stmt ','
           cond=expr ','
          after=stmt DO body=stmt OD
      { $locals = join_sets($body.locals); /* TODO init, after */ } # for
    | RETURN expr
      { $locals = create_set(); }                           # returnStatement
    | function_
      { $locals = create_set(); }                           # functionStatement
    ;

elif returns [Set<String> locals]
    : ELIF cond=expr THEN elifClause=stmt
      { $locals = join_sets($elifClause.locals); }
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