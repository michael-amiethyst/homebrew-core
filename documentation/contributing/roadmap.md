# Bashpile Compiler Plans

1. primary expressions
2. Debug comments (mapping from Bashpile lines to generated Bash lines)
3. switch / getopt
4. C-style for loops
5. lists
6. foreach loops
7. Work-around for options with `||`, see https://unix.stackexchange.com/questions/65532/why-does-set-e-not-work-inside-subshells-with-parenthesis-followed-by-an-or
8. IDE integration (IntelliJ)

## Unscheduled ideas
Take 2nd file argument, it would be the compiled file (with shebang and chmod +x)

# Bashpile STDLIB Plans

* Conversion of Types (e.g. `float` to `int` and actually round up or down)
  * Verify types / asserts
* Argument Handling / getopt parsing
