package com.example.sologym.model

enum class ExerciseType {
    STRENGTH,
    AEROBIC,
}

enum class AerobicExercise(val displayName: String) {
    WALKING("CAMINHADA"),
    RUNNING("CORRIDA"),
    CYCLING("BICICLETA"),
    ELLIPTICAL("ELÍPTICO"),
    STAIRS("ESCADA"),
}
