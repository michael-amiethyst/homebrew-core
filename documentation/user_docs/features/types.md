# Types

* boolean
* integer - Same range as Bash.  Up to 2^63 - 1, over 9 quintillion!
* float - almost any precision, uses the defaults from the `bc` utility
* string - Break up strings over multiple lines by escaping the newline, so end the line with `\`
* list - on roadmap
* map - on roadmap
* reference - on roadmap

## Typecasts

Values are not checked at runtime, but only at compile time.  Syntax is 'VAR as TYPE', with 
the same precedence as Java.