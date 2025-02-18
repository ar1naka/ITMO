class BankAccount(amount: Int) {
    // write solution here
    init {
        require(amount >= 0) { "Amount should be positive!" }
    }

    var balance: Int = amount
        private set(value) {
            logTransaction(field, value)
            field = value
        }

    fun deposit(amount: Int) {
        require(amount > 0) { "Amount should be positive and not a zero!" }
        balance += amount
    }

    fun withdraw(amount: Int) {
        require(amount > 0) { "Amount should be positive and not a zero!" }
        require(amount <= balance) { "Amount should be less than the balance!" }
        balance -= amount
    }
}

fun logTransaction(from: Int, to: Int) {
    println("$from -> $to")
}
