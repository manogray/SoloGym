package com.example.sologym.model

enum class WorkoutStatus(
    val displayName: String
) {
    NOT_STARTED("PENDENTE"),
    IN_PROGRESS("EM PROGRESSO"),
    FINISHED("CONCLUÍDO")
}
