# If-else if-else Statements


## Boolean Logic

You can use `and`/`or` as well as `true` or `false`.

## Integer and Floating Point comparisons

Both are supported:
```
if (1 < 2 and 6.5 < 9.3):
    print("Math works!")
```
Other operators: `<=`, `>`, `>=`, `==`, `!=`.

Note: These are the same operators for arithmetic and String comparisons too.

If using a ShellString you'll need to typecast it from String to integer or float first.  As in:
```
if (#(expr 5 + 6): integer - 1 == 10):
    ...
```

## String Operators

```
if (isEmpty varName or isNotEmpty otherVar):
    ...
else if (someString == "literal"):
    ...
else:
    ...
```

## File Operators

exists, notExists, regularFileExists, directoryExists

## Native Bash operators

You can just use Bash flags directly (e.g. -f, -r, -w).