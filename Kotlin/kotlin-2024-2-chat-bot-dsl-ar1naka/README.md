# Задание 4. Chat bot DSL

Вам необходимо реализовать DSL для декларативного описания функциональности чат ботов (в Telegram, VK или других мессенджерах).

Вам дано базовое api для работы с ботом (пакет `chatbot.api`):

* Интерфейс `Client`, скрывающий детали взаимодействия с api мессенджеров
* Интерфейс `ChatContextsManager`, хранит внутри состояние определенного чата.
  Например, пользователь выбрал какой-то пункт меню и теперь должен нажать какую-то кнопку подменю.

Также дана базовая реализация чат-бота в пакете `chatbot.bot`:
* `MessageProcessor` - лямбда функция-обработчик сообщения;
* `MessageHandler` - класс с полями
  * `predicete` - условие, при котором нужно выполнить обработчик,
  * `processor` - обработчик, который нужно выполнить, если предикат выполняется;
* `Bot` - класс, реализующий интерфейс `ChatBot` и принимающий список `MessageHandler`  
  При обработке сообщения выбирается первый подходящий обработчик, а все последующие должны игнорироваться.

Реализуйте DSL по заданию ниже в пакете `chatbot.dsl`.

Решение можно проверить с помощью тестов, которые запускаются в интерфейсе Intellij Idea или через консоль `./gradlew test` (*Nix) или `.\gradlew test` (Windows).

Готовое решение добавьте в ветку `solution`.
Убедитесь, что ваш код корректно отформатирован через `./gradlew ktlintCheck` (*Nix) или `.\gradlew ktlintCheck` (Windows).
Создайте pull request с этой веткой, в качестве заголовка pull request обязательно укажите свое ФИО.
После создания PR убедитесь, что тесты прошли.

## Базовая настройка

* Создайте конструкцию `chatBot(client: Client)` для определения бота. 
  Функция должна создавать реализацию интерфейса `ChatBot` на основе класса `Bot`.

* Внутри `chatBot` добавьте возможность настраивать уровень логирования
    * через конструкцию `use` (`use(LogLevel.Error)`);
    * через унарный плюс (`+LogLevel.Error`).
  По умолчанию должен использоваться `LogLevel.Error`. 

```kotlin
val bot = chatBot(testClient) {
    use(LogLevel.INFO)
}
```

или

```kotlin
val bot = chatBot(testClient) {
    +LogLevel.INFO
}
```

## Базовое поведение

Внутри `chatBot` реализуйте конструкцию `behaviour` для определения поведения бота.
Определенные ниже конструкции далее будем называть обработчиками.
* конструкция `onCommand(command: String)` определяет поведение бота при получении сообщения,
  содержащего `/${command}` в качестве первого слова в сообщении;
* конструкция `onMessage(predicate: (Message) -> Bool)` определяет поведение бота при получении сообщения,
  удовлетворяющего предикату;
* конструкции `onMessagePrefix(preffix: String)`, `onMessageContains(text: String)`
  и `onMessage(messageTextExactly: String)` определяют поведение бота при получении сообщения, текст которого
  соответствует критерию;
* конструкцию `onMessage` для определения поведения бота при получении любого сообщения.

В качестве функции для обработки соответствующих событий используйте функцию с типом `MessageProcessor`.

```kotlin
val bot = chatBot(testClient) {
    use(LogLevel.INFO)

    behaviour {
        onCommand("help") {
            client.sendMessage(message.chatId, "How can i help you?")
        }

        onMessage("ping") {
            client.sendMessage(message.chatId, "pong", replyMessageId = message.id)
        }
    }
}
```

## Контексты внутри чатов

Разработайте возможность для каждого чата с ботом хранить некоторый контекст.
Это полезно при обработке последовательности сообщений от пользователя.

Пример использования:
```
Person: /generateDogMeme                <ChatContext>: DogMeme(breed=null, text=null)
Bot: What breed should a dog be?   
Perosn: Corgi                           <ChatContext>: DogMeme(breed=Corgi, text=null)
Bot: What text should be in picture?
Person: Kotlin programming is easy      <ChatContext>: DogMeme(breed=Corgi, text=Kotlin programming is easy)
Bot: <picture with meme>                <ChatContext>: null
```

* Внутри `chatBot` поддержите возможность подключения менеджера контекстов пользователей бота (объекта
  интерфейса `ChatContextsManager`)
    * через оператор присваивания (`contextManager = anyContextManager`);
    * через конструкцию `use` (`use(otherContextManager)`).

* Внутри `behaviour` поддержите возможность объявлять некоторые обработчики, которые должны срабатывать только, если чат
  имеет определенный контекст
    * для всех контекстов определенного типа `SomeChatContext` через конструкцию `into<SomeChatContext>` (`into<SomeChatContext> { ... }`)
    * для конкретного экземпляра контекста через конструкцию `T.into` (`someChatContextInstance.into { ... }`), в этом случае следует проверять текущий контекст на равенство `someChatContextInstance`.

  **Примечание**: для проверки типа контекста нельзя использовать java или kotlin reflection, примените для этого механизм для работы с generic, встроенный в kotlin.

```kotlin
object AskNameContext : ChatContext
class WithNameContext(val name: String) : ChatContext

val bot = chatBot {
    use(testClient)

    behaviour {
        into<NamedUserContext> {
            onMessage {
                client.sendMessage(message.chatId, "Hello, ${this.context.name}!")
            }
        }

        AskNameContext.into {
            onMessage {
                client.sendMessage(message.chatId, "ok")
                setContext(NamedUserContext(message.text))
            }
        }

        onCommand("start") {
            client.sendMessage(message.chatId, "Hello! Say your name!")
            setContext(AskNameContext)
        }
    }
}
```

## Билдер сообщений

Так же требуется поддержать DSL для отправки сообщения, с поддержкой кнопок.

Для работы с клавиатурой необходимо использовать параметр `keyboard` при отправке сообщения. 
* Клавиатуру можно убрать, отправив `Keyboard.Remove`;
* Клавиатура можно настроить с помощью `Keyboard.Markup`.

В рамках dsl необходимо подержать внутри блока обработчика сообщений (например, внутри `onMessage { ... }`):

* функцию `sendMessage(chatId: ChatId.Id, text: String) {}` которая отправляет сообщение с соответствующим текстом, используя client;
* блок `sendMessage(chatId: ChatId.Id) {}` в котором настраивается и отправляется сообщение, используя client;
* внутри блока `sendMessage` вызов `removeKeyboard()` должен устанавливать клавиатуру в значение `Keyboard.Remove` при отправке;
* внутри блока `sendMessage` вызов `withKeyboard {}` должен настраивать клавиатуру и устанавливать её в соответсвующее значение `Keyboard.Markup`
  при отправке;
* блок `row {}` внутри `withKeyboard` должен добавлять новую строку в конец клавиатуры;
* вызов `button(text = "text")` внутри `row {}` должен добавлять кнопку в конец строки;
* вызов `- "text"` должен добавлять кнопку в конец строки.

Так же должно быть возможно напрямую задать:

* разметку всей клавиатуры через матрицу, присвоив значение переменной `keyboard`;
* разметку строки, добавив её в массив в переменной `keyboard`.

Пустые сообщения, то есть с пустым текстом и без обновлений клавиатуры или с пустым текстом и устанавливающие Markup с пустой клавиатурой, должны игнорироваться и не отправляться.

```kotlin
val bot = chatBot(testClient) {
    behaviour {
        onCommand("help") {
            sendMessage(message.chatId) {
                text = "How can i help you?" // "" by default
                replyTo = message.id // must be available

                removeKeyboard() // will send Keyboard.Remove
                // or
                withKeyboard {
                    oneTime = true // false by default
                    
                    keyboard = mutableListOf(mutableListOf(Keyboard.Button(text = "1:1"), Keyboard.Button(text = "1:2")))
                    keyboard.add(mutableListOf(Keyboard.Button(text = "2:1"), Keyboard.Button(text = "2:2")))

                    row {
                        button(text = "3:1")
                        button(text = "3:2")
                    }
                    row {
                        -"4:1"
                        -"4:2"
                    }
                }
                // will send:
                // Keyboard.Murkup(
                //   oneTime = true,
                //   keyboard = listOf(
                //     listOf(Keyboard.Button(text = "1:1"), Keyboard.Button(text = "1:2"))),
                //     listOf(Keyboard.Button(text = "2:1"), Keyboard.Button(text = "2:2"))),
                //     listOf(Keyboard.Button(text = "3:1"), Keyboard.Button(text = "3:2"))),
                //     listOf(Keyboard.Button(text = "4:1"), Keyboard.Button(text = "4:2"))),
                //   ),
                // )
            }
        }
    }
}
```

## (Бонус) Предикатные контексты для обработчиков

Определим `MessageProcessorPredicate` следующим образом
```kotlin
typealias MessagePredicate = (message: Message) -> Boolean
```

* Реализуйте возможность определять обработчики для сообщений, удовлетворяющих предикатам, при помощи
  конструкции `MessageProcessorPredicate.into`.

```kotlin

val IS_ADMIN: MessagePredicate = { it.chatId.id == 316671439L }
chatBot(client) {
    behaviour {
        IS_ADMIN.into {
            onCommand("ban_user") {
                // ...
            }

            // and other admin commands
        }

        onCommand("help") {
            // ...
        }

        // and other user commands
    }
}
```

Обратите внимание, что в отличие от контекстов `into` конструкции могут вкладываться друг в друга, то есть
внутри `MessageProcessorPredicate.into` может быть еще одна такая же конструкция.
В таком случае обработчики должны срабатывать, когда все необходимые предикаты выполняются.

* Переопределите операцию умножения для двух `MessagePredicate`: в таком случае обработчики должны выполняться, когда оба предиката выполняются

```kotlin
val IS_ADMIN: MessagePredicate = { it.chatId.id == 316671439L }
val IS_EVEN_MESSAGE_ID: MessagePredicate = { it.id % 2 == 0L }

chatBot(client) {
    behaviour {
        (IS_ADMIN * IS_EVEN_MESSAGE_ID).into {
            // ... 
        }
    }
}
```
