package turingmachine

class TuringMachine(
    private val startingState: String,
    private val acceptedState: String,
    private val rejectedState: String,
    transitions: Collection<TransitionFunction>,
) {

    private val transitionMap: Map<Pair<String, Char>, TransitionFunction> =
        transitions.associateBy { it.state to it.symbol }

    fun initialSnapshot(input: String): Snapshot {
        val tape = Tape(input)
        return Snapshot(startingState, tape)
    }

    fun simulateStep(snapshot: Snapshot): Snapshot {
        val currentSymbol = snapshot.tape.content[snapshot.tape.position]
        val transition = transitionMap[Pair(snapshot.state, currentSymbol)]

        return if (transition != null) {
            snapshot.applyTransition(transition.transition)
        } else {
            Snapshot(rejectedState, snapshot.tape.copy())
        }
    }

    fun simulate(initialString: String): Sequence<Snapshot> {
        var snap = initialSnapshot(initialString)

        return sequence {
            yield(snap)
            while (snap.state != acceptedState && snap.state != rejectedState) {
                snap = simulateStep(snap)
                yield(snap)
            }
        }
    }

    class Snapshot(val state: String, val tape: Tape) {
        fun applyTransition(transition: Transition): Snapshot {
            tape.applyTransition(transition.newSymbol, transition.move)
            return Snapshot(transition.newState, tape)
        }

        override fun equals(other: Any?): Boolean {
            if (other !is Snapshot) return false
            return state == other.state
        }

        override fun hashCode(): Int {
            var result = state.hashCode()
            result = 31 * result + tape.content.hashCode()
            result = 31 * result + tape.position
            return result
        }

        override fun toString(): String {
            return state + System.lineSeparator() + tape
        }

        fun copy(): Snapshot {
            return Snapshot(state, tape.copy())
        }
    }

    class Tape(startState: String) {
        var content: MutableList<Char> = startState.toMutableList()
        var position: Int = 0

        init {
            if (startState.isEmpty()) {
                content.add(BLANK)
            }
        }

        fun applyTransition(char: Char, move: TapeTransition): Tape {
            if (position >= 0 && position < content.size) {
                content[position] = char
            }
            when (move) {
                TapeTransition.Right -> moveRight()
                TapeTransition.Left -> moveLeft()
                TapeTransition.Stay -> {}
            }
            return this
        }

        private fun moveRight() {
            position++
            if (position == content.size) {
                content.add(BLANK)
            }
            clean(0, TapeTransition.Right)
        }

        private fun moveLeft() {
            position--
            if (position < 0) {
                content.add(0, BLANK)
                position = 0
            }
            clean(content.size - 1, TapeTransition.Left)
        }

        private fun clean(pos: Int, move: TapeTransition) {
            if (position != pos && content[pos] == BLANK) {
                content.removeAt(pos)
                if (move == TapeTransition.Right) position--
            }
        }

        fun copy(): Tape {
            val newContent = content.toMutableList()
            val newTape = Tape(newContent.joinToString(""))
            newTape.position = position
            return newTape
        }

        override fun equals(other: Any?): Boolean {
            if (other !is Tape) return false
            return content == other.content && position == other.position
        }

        override fun hashCode(): Int {
            var result = content.hashCode()
            result = 31 * result + position
            return result
        }

        override fun toString(): String {
            return content.joinToString("")
        }
    }
}
