package chatbot.dsl

import chatbot.api.Keyboard
import chatbot.api.Message
import chatbot.api.MessageId

@BotMarkerDsl
class MessageBuilder(val message: Message) {
    var text: String = ""
    var keyboard: Keyboard? = null
    var replyTo: MessageId? = null

    fun removeKeyboard() {
        keyboard = Keyboard.Remove
    }

    fun withKeyboard(init: KeyboardBuilder.() -> Unit) {
        val keyboardBuilder = KeyboardBuilder()
        keyboardBuilder.init()
        keyboard = keyboardBuilder.build()
    }

    fun keyboardIsEmpty(): Boolean {
        return when (keyboard) {
            is Keyboard.Markup -> (keyboard as Keyboard.Markup).keyboard.all { it.isEmpty() }
            else -> false
        }
    }
}
