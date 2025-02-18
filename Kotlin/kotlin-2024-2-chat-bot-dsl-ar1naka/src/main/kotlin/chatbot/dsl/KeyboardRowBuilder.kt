package chatbot.dsl

import chatbot.api.Keyboard

@BotMarkerDsl
class KeyboardRowBuilder {
    val row = mutableListOf<Keyboard.Button>()

    fun button(text: String) {
        row.add(Keyboard.Button(text))
    }

    operator fun String.unaryMinus() {
        row.add(Keyboard.Button(this))
    }
}
