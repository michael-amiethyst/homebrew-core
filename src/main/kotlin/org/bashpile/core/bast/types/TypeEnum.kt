package org.bashpile.core.bast.types

enum class TypeEnum {
    /** Coerces to anything */
    UNKNOWN,
    BOOLEAN,
    INT,
    /** Also used for unknown calculation results */
    FLOAT,
    STRING,
    LIST,
    MAP,
    REFERENCE;

    fun coercesTo(type: TypeEnum): Boolean {
        return this == type || this == UNKNOWN || type == UNKNOWN
    }
}
