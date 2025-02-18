package chatbot.dsl

import chatbot.api.ChatContext
import chatbot.bot.MessageHandler

@BotMarkerDsl
class ContextBuilder<C : ChatContext?>(
    checkContext: (ChatContext?) -> Boolean,
    retypeMessageHandler: (MessageHandler<C>) -> MessageHandler<ChatContext?>,
) : Builder<C>(checkContext, retypeMessageHandler)
