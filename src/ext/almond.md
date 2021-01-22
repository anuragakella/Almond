# The Almond Programming Language
## Grammar


```
program := decleration* EOF

decleration := function | class | statement

statement := variable | expression | for | while | if | print | return

variable := "var" identifier ( "=" expression )?

function := "function" identifier "(" parameters? "):"
				statement* 
			"end"

parameters := identifier ( "," identifier )*

class := "class" identifer ":" 
			variable*
			function* 
		 "end"


for := for "(" ((variable | expression)? ";") ( or? ";" ) ( or?) "):" 
			statement*
	       "end"

if := "if(" expression "):" 
		statement*
	  "end"

while := "while(" expression "):" 
			statement*
		 "end"

print := "print" expression 

return := "return" expression

expression := assignment

assignment := ( call "." )? identifier "=" assignment | or

or := and ( "or" and)*

and := equality ( "==" equality )*

equality := comparision ( "!=" | "==" comparision )*

comparision := addsub ( "<" | ">" | "<=" | ">=" addsub )*

addsub := muldiv ( "+" | "-" muldv )*

muldiv := unary ( "*" | "/" unary )*

unary := ( "-" | "!" ) unary | call 

call  := primary "(" arguments? ")? ("." identifier )* 

primary := "true" | "false" | "none" | "this" | number | strng | identifier | "(" expression ")"

arguments := expression ("," expression)*

number := decimal | whole

decimal := digit+ ( "."  digit+ )

whole := digit+

identifier = alpha | (alpha|digit)*

digit := 0 | 1 .... | 9

alpha := "a" ... | "z" | "A" .... | "Z" | "_"
```




