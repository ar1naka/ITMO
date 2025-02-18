package chatbot.dsl

import chatbot.api.ChatContext
import chatbot.bot.MessageHandler
import chatbot.bot.MessageProcessorContext

@BotMarkerDsl
class BehaviourBuilder<C : ChatContext?>(
    checkContext: (ChatContext?) -> Boolean,
    retypeMessageHandler: (MessageHandler<C>) -> MessageHandler<ChatContext?>,
) : Builder<C>(checkContext, retypeMessageHandler) {
    inline fun <reified C : ChatContext?> retypeImpl(): (MessageHandler<C>) -> MessageHandler<ChatContext?> = {
        MessageHandler(
            { message, context -> context is C && it.predicate(message, context) },
            { it.processor(MessageProcessorContext(message, client, context as C, setContext)) },
        )
    }

    inline fun <reified C : ChatContext> into(init: Builder<C>.() -> Unit) {
        BehaviourBuilder<C>(
            { context -> context != null && context is C },
            retypeImpl(),
        ).apply(init).let {
            behaviours.addAll(it.behaviours)
        }
    }

    inline infix fun <reified C : ChatContext> C.into(init: Builder<C>.() -> Unit) {
        BehaviourBuilder<C>(
            { context -> context != null && context == this },
            retypeImpl(),
        ).apply(init).let {
            behaviours.addAll(it.behaviours)
        }
    }
}
