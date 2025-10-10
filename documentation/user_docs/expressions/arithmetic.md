# Arithmetic Expressions

Integer arithmetic uses Bash built-ins.
Floating points use `bc` in a subshell.

Float assignments to an integer round down.

++i, i++, --i, i-- are supported as well.

Example:
```
i: integer = 1 + 2 * 3 / 4
print(i) # 1
r: 7.5
areaOfCircle: float = 3.14159 * r * r
print(areaOfCircle) # approx 176.7144375
```
