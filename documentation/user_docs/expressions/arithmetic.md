# Arithmetic Expressions

# Integers
Integer arithmetic uses Bash built-ins.  See Bash documentation for `$(( ))` for details.

Preincrement, postdecrement and all operators in between are supported.  
For integer i you can write `++i`, `i++`, `--i` or `i--`.

# Floats
Floating point math uses `bc` in a subshell.

Float assignments to an integer round down.

Example:
```
i: integer = 1 + 2 * 3 / 4
print(i) # 1
r: 7.5
areaOfCircle: float = 3.14159 * r * r
print(areaOfCircle) # approx 176.7144375
```
