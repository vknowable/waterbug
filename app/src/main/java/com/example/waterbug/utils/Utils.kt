package com.example.waterbug.utils

/// Show a truncated version of the address: "<first 5>...<last 5>"
fun truncateAddress(input: String, keep: Int = 5): String {
    return if (input.length > 4 + keep) {
        "${input.take(4)}...${input.takeLast(keep)}"
    } else {
        input
    }
}

/// Return the time remaining in the current epoch, H:MM format
fun epochDurRemaining(secs: Int): String {
    return "3H:29M"
}

/// Parse a denominated token amount from an indexer api response
fun parseDenom(amount: String, divisor: Int = 1000000): Double {
    return amount.toDouble() / divisor
}
