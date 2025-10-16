package org.bashpile.core.bast.expressions

import org.bashpile.core.bast.BastNode
import org.bashpile.core.TypeEnum

// TODO 0.19.0 - Move this and subclasses to arithmetic package
abstract class ArithmeticBastNode(children: List<BastNode> = listOf(), majorType: TypeEnum = TypeEnum.UNKNOWN)
    : BastNode(children.toMutableList(), majorType = majorType)
