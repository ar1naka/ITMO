package chatbot.bot

import chatbot.api.ChatContext
import chatbot.api.ChatId
import chatbot.api.Client
import chatbot.api.Message
import chatbot.dsl.BotMarkerDsl
import chatbot.dsl.MessageBuilder

@BotMarkerDsl
class MessageProcessorContext<C : ChatContext?>(
    val message: Message,
    val client: Client,
    val context: C,
    val setContext: (c: ChatContext?) -> Unit,
) {
    fun sendMessage(chatId: ChatId, init: MessageBuilder.() -> Unit) {
        val messageBuilder = MessageBuilder(message).apply(init)
        sendMessage(chatId, messageBuilder)
    }

    fun sendMessage(chatId: ChatId, text: String) {
        val messageBuilder = MessageBuilder(message).apply { this.text = text }
        sendMessage(chatId, messageBuilder)
    }

    private fun sendMessage(chatId: ChatId, messageBuilder: MessageBuilder) {
        if (messageBuilder.text.isEmpty() && messageBuilder.keyboard == null) {
            return
        }
        if (messageBuilder.keyboardIsEmpty()) {
            return
        }
        client.sendMessage(chatId, messageBuilder.text, messageBuilder.keyboard, messageBuilder.replyTo)
    }
}

typealias MessageProcessor<C> = MessageProcessorContext<C>.() -> Unit
