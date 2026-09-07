package com.example.sologym.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import com.example.sologym.model.ProfileImages

@Entity(tableName = "player_profile")
data class PlayerProfile(
    @PrimaryKey val id: Int = SINGLE_PROFILE_ID,
    val nome: String = "",
    val dataNascimento: LocalDate? = null,
    val fotoUri: String = ProfileImages.DEFAULT,
) {
    companion object {
        const val SINGLE_PROFILE_ID = 1
    }
}
