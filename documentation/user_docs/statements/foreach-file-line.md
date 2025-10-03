# Foreach File Line Statements

Windows line endings OK.  May end with no trailing newline.
Skips first line of CSV.

Like
```
for(first: string, last: string, email: string, phone: string in "src/test/resources/data/example.csv"):
                print(first + " " + last + " " + email + " " + phone + "\n")
```
TODO