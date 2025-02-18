package chatbot.dsl

import chatbot.api.*
import chatbot.bot.Bot
import chatbot.bot.MessageHandler

@BotMarkerDsl
class ChatBotBuilder(private val client: Client) {
    private var logLevel: LogLevel = LogLevel.ERROR
    private val messageHandlers = mutableListOf<MessageHandler<ChatContext?>>()
    private var contextManager: ChatContextsManager? = null

    fun use(logLevel: LogLevel) {
        this.logLevel = logLevel
    }

    fun use(contextManager: ChatContextsManager) {
        this.contextManager = contextManager
    }

    operator fun LogLevel.unaryPlus() {
        use(this)
    }

    fun behaviour(init: BehaviourBuilder<ChatContext?>.() -> Unit) {
        messageHandlers.addAll(BehaviourBuilder(CHAT_BOT_CONDITION) { it }.apply(init).behaviours)
    }

    fun build(): ChatBot = Bot(
        client = client,
        logLevel = logLevel,
        messageHandlers = messageHandlers,
        contextManager = contextManager,
    )

    companion object {
        private val CHAT_BOT_CONDITION: (ChatContext?) -> Boolean = { true }
    }
}

fun chatBot(client: Client, init: ChatBotBuilder.() -> Unit): ChatBot {
    return ChatBotBuilder(client).apply(init).build()
}
