package ru.alexbur.ai_lab_3

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform