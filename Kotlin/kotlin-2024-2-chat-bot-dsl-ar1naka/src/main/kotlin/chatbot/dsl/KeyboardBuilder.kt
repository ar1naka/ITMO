package chatbot.dsl

import chatbot.api.Keyboard

@BotMarkerDsl
class KeyboardBuilder {
    var oneTime: Boolean = false
    var keyboard = mutableListOf<MutableList<Keyboard.Button>>()

    fun row(init: KeyboardRowBuilder.() -> Unit) {
        keyboard.add(KeyboardRowBuilder().apply(init).row)
    }

    fun build(): Keyboard.Markup {
        return Keyboard.Markup(oneTime, keyboard)
    }
}
