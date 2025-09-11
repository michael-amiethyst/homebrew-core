package org.bashpile.core.bast.expressions

import org.bashpile.core.bast.BastNode
import org.bashpile.core.bast.types.TypeEnum

abstract class ArithmeticBastNode(children: List<BastNode> = listOf(), majorType: TypeEnum = TypeEnum.UNKNOWN)
    : BastNode(children.toMutableList(), majorType = majorType)
