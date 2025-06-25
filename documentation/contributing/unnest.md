# The unnesting subshells workaround

Bash loses the error code of a subshell (e.g. `exit 1` is ignored).  The workaround is to move the subshell out
and save the result in a variable.

The end result will be something like:
```bash
declare __bp_var1
__bp_var1=$(unnestedSubShellCommand)
$(outerSubshellUses $___bp_var1)
```

We do this by transforming the BAST tree before we `render`

```mermaid
graph TD;
    shellStringNode --> internalNode

    internalNode --> subshellStart
    internalNode --> internalNode2
    internalNode --> closingParenthesis
    
    internalNode2 --> leafNode
```
becomes
```mermaid
graph TD;
    shellString --> internalNode
    internalNode --> variableDeclaration
    variableDeclaration --> id["i="]
    variableDeclaration --> nestedSubshellContents
    internalNode --> parentSubshellContents
    parentSubshellContents --> variableReference
    variableReference --> i["$i"]
```