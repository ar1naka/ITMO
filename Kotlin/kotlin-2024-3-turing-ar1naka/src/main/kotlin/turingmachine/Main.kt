package turingmachine

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.float
import java.io.File
import java.io.FileNotFoundException
import java.lang.IllegalArgumentException

class TuringMachineApp : CliktCommand() {
    private val file: File by argument(help = "Path to file with Turing machine").file()

    private val string: String? by argument(help = "Path to file with start string").optional()

    private val auto: Boolean by option("--auto", help = "Automatic mode").flag(default = false)

    private val delay: Float by option("--delay", help = "Delay between steps in seconds for auto mode").float()
        .default(0.5f)

    override fun run() {
        try {
            val lines = file.readLines()
            val startString = getStartString()
            val start = extractState(lines, "start:", "Missing start state")
            val accept = extractState(lines, "accept:", "Missing accept state")
            val reject = extractState(lines, "reject:", "Missing reject state")
            val blank = extractState(lines, "blank:", "Missing blank symbol")

            val transitions = parseTransitions(lines, blank)
            val machine = TuringMachine(
                start,
                accept,
                reject,
                transitions,
            )
            simulateMachine(machine, startString, blank.single())
        } catch (e: IllegalArgumentException) {
            println(e.message)
        } catch (e: FileNotFoundException) {
            println(e.message)
        }
    }

    private fun getStartString(): String {
        val result = string?.let { File(it).readLines() }?.firstOrNull()
        return result ?: requireNotNull(readlnOrNull()?.takeIf { it.isNotBlank() }) { "Missing start string" }
    }

    private fun parseTransitions(lines: List<String>, blank: String): List<TransitionFunction> {
        return lines.filter { it.contains("->") }.map { line ->
            val parts = line.split("->")
            require(parts.size == 2) { "Invalid line, expected 'fromState inputSymbol -> toState outputSymbol move'" }

            val leftPart = parts[0].trim()
            val rightPart = parts[1].trim()

            val leftParts = leftPart.split(" ")
            require(leftParts.size == 2) { "Invalid left part format: Expected 'fromState inputSymbol'" }

            var (fromState, inputSymbol) = leftParts

            val rightParts = rightPart.split(" ")
            require(rightParts.size == 3) { "Invalid right part format: Expected 'toState outputSymbol move'" }

            val toState = rightParts[0].trim()
            var outputSymbol = rightParts[1].trim()
            val move = rightParts[2].trim()

            require(fromState.isNotEmpty()) { "From symbol cannot be empty in '$line'" }
            require(inputSymbol.isNotEmpty()) { "Input symbol cannot be empty in '$line'" }
            require(outputSymbol.isNotEmpty()) { "Output symbol cannot be empty in '$line'" }
            require(move.isNotEmpty()) { "Move cannot be empty in '$line'" }

            inputSymbol = inputSymbol.takeIf { it != blank } ?: BLANK.toString()
            outputSymbol = outputSymbol.takeIf { it != blank } ?: BLANK.toString()

            TransitionFunction(
                fromState,
                inputSymbol.single(),
                when (move.single()) {
                    '>' -> TapeTransition.Right
                    '<' -> TapeTransition.Left
                    '^' -> TapeTransition.Stay
                    else -> { throw IllegalArgumentException("Invalid symbol of move in '$line'") }
                },
                outputSymbol.single(),
                toState,
            )
        }
    }

    private fun extractState(lines: List<String>, prefix: String, errorMessage: String): String {
        return requireNotNull(
            lines.find { it.startsWith(prefix) }?.substringAfter(":")?.trim(),
        ) { errorMessage }
    }

    private fun simulateMachine(machine: TuringMachine, startString: String, blank: Char) {
        var snapshot: TuringMachine.Snapshot
        val simulation = machine.simulate(startString).iterator()

        while (simulation.hasNext()) {
            snapshot = simulation.next()
            printState(snapshot, snapshot.tape.position, blank)
            when (auto) {
                false -> readlnOrNull()
                true -> Thread.sleep(delay.toLong() * 1000L)
            }
        }
    }

    private fun printState(snapshot: TuringMachine.Snapshot, pos: Int, blank: Char) {
        println(snapshot.toString().replace(BLANK, blank))
        repeat(pos + 1) {
            print(if (it == pos) "^" else " ")
        }
        println()
    }
}

fun main(args: Array<String>) = TuringMachineApp().main(args)
