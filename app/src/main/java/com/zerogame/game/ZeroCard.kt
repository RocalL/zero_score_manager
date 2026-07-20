package com.zerogame.game

import androidx.compose.ui.graphics.Color

enum class ZeroCardColor(val label: String, val color: Color, val index: Int) {
    RED("Red", Color(0xFFE53935), 0),
    BLUE("Blue", Color(0xFF1E88E5), 1),
    GREEN("Green", Color(0xFF43A047), 2),
    YELLOW("Yellow", Color(0xFFFDD835), 3),
    PURPLE("Purple", Color(0xFF8E24AA), 4),
    ORANGE("Orange", Color(0xFFF4511E), 5),
    BLACK("Black", Color(0xFF424242), 6)
}

data class ZeroCard(
    val id: Int,
    val color: ZeroCardColor,
    val value: Int
) {
    companion object {
        private val _all = mutableListOf<ZeroCard>()
        val all: List<ZeroCard> get() = _all

        init {
            var id = 0
            for (color in ZeroCardColor.entries) {
                for (value in 1..8) {
                    _all.add(ZeroCard(id++, color, value))
                }
            }
        }

        fun computeScore(selectedIds: Set<Int>): Pair<Int, Boolean> {
            if (selectedIds.isEmpty()) return 0 to false
            val cards = all.filter { it.id in selectedIds }

            val zeroValueIds = findFiveSameValue(cards)
            val zeroColorIds = findFiveSameColor(cards)
            val zeroCardIds = zeroValueIds + zeroColorIds

            val isZero = zeroValueIds.isNotEmpty() && zeroColorIds.isNotEmpty()
            val score = cards.filter { it.id !in zeroCardIds }.sumOf { it.value }
            return score to isZero
        }

        private fun findFiveSameValue(cards: List<ZeroCard>): Set<Int> {
            val grouped = cards.groupBy { it.value }
            for ((_, group) in grouped) {
                if (group.size >= 5) return group.take(5).map { it.id }.toSet()
            }
            return emptySet()
        }

        private fun findFiveSameColor(cards: List<ZeroCard>): Set<Int> {
            val grouped = cards.groupBy { it.color }
            for ((_, group) in grouped) {
                if (group.size >= 5) return group.take(5).map { it.id }.toSet()
            }
            return emptySet()
        }
    }
}
