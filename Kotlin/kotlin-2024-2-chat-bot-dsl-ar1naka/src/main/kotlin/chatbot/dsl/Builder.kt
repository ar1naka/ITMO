package chatbot.dsl

import chatbot.api.ChatContext
import chatbot.api.Message
import chatbot.bot.MessageHandler
import chatbot.bot.MessageProcessor

@BotMarkerDsl
open class Builder<C : ChatContext?>(
    private val checkContext: (ChatContext?) -> Boolean,
    private val retypeMessageHandler: (MessageHandler<C>) -> MessageHandler<ChatContext?>,
) {
    val behaviours = mutableListOf<MessageHandler<ChatContext?>>()

    fun onCommand(command: String, handler: MessageProcessor<C>) {
        addMessageHandler(
            { message -> message.text.startsWith("/$command") },
            handler,
        )
    }

    private fun addMessageHandler(
        predicate: (Message) -> Boolean,
        handler: MessageProcessor<C>,
    ) {
        val messageHandler = MessageHandler(
            { message, context -> predicate(message) && checkContext(context) },
            handler,
        )
        val newMessageHandler = retypeMessageHandler(messageHandler)
        behaviours.add(newMessageHandler)
    }

    fun onMessage(predicate: (Message) -> Boolean, handler: MessageProcessor<C>) {
        addMessageHandler(predicate, handler)
    }

    fun onMessagePrefix(prefix: String, handler: MessageProcessor<C>) {
        addMessageHandler({ message -> message.text.startsWith(prefix) }, handler)
    }

    fun onMessageContains(text: String, handler: MessageProcessor<C>) {
        addMessageHandler({ message -> message.text.contains(text) }, handler)
    }

    fun onMessage(messageTextExactly: String, handler: MessageProcessor<C>) {
        addMessageHandler({ message -> message.text == messageTextExactly }, handler)
    }

    fun onMessage(action: MessageProcessor<C>) {
        addMessageHandler({ true }, action)
    }
}
