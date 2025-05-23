package org.bashpile.core.bast

/**
 * For inner nodes in the tree data structure sense.  Used as a "holder node".
 *
 * @see https://en.wikipedia.org/wiki/Tree_(abstract_data_type)#:~:text=An%20internal%20node
 */
class InternalBastNode(children: List<BastNode>) : BastNode(children) {
}
