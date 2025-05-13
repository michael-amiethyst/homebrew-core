# Bashpile: A modern language that transpiles to Bash

## Why?

I hate Bash.  Surviving the Unix Wars left it scarred.  
Did you know that there are 4 different built in ways to do addition and none of them support floats?
Here's some things from the "Bash Iceberg" that Bashpile will handle for you:
* Ensuring that generated scripts run in "strict mode" to ensure that errors are caught early
* shellcheck compatible
* Portability (e.g. POSIX)
* Whitespace (Bash says that spaces separate tokens but "varName = 1" will not work)
* Various Bash bugs (e.g. a nested subshell will have a bad exit code ignored)

As a bonus example if in that arithmetic you want to use `<=`,
it isn't supported so you have to use not `>`.  However, that's interpreted as a redirect so you have to escape it as `\>`.
If you didn't notice that's a workaround for a workaround!  Bashpile handles it for you.

Bashpile has over 20 StackOverflow answers built in and you get to work with a language similar to Python and Java.

## Why not just use Fish or Python directly?

Bash runs just about anywhere on the most minimal of servers, including production machines and docker containers.
Also, for all of its faults mentioned above it's fast, especially if you are making a lot of command line calls.


## Quickstart

1. `brew tap michael-amiethyst/core`
2. `brew install michael-amiethyst/core`
3. `echo "print('Hello World')" > /tmp/hello && bashpile /tmp/hello`

## Script Start

1. Create a script starting with `#!/usr/bin/env bashpile`
2. $(bashpile SCRIPT)
3. If errors run `cat ~/.bashpile/log.txt`

## Language Reference

* Quick compiles!  Within a second
* White space agnostic (except for Python style indentation)
* `print("Any String here")`, single quotes allowed with no special difference
* `"string" + "concatination"`