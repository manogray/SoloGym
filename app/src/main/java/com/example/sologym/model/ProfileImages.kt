package com.example.sologym.model

import androidx.annotation.DrawableRes
import com.example.sologym.R

data class ProfileImage(
    val name: String,
    @DrawableRes val drawableRes: Int,
)

object ProfileImages {
    const val DEFAULT = "default_profile"

    val selectable = listOf(
        ProfileImage("bellion_profile", R.drawable.bellion_profile),
        ProfileImage("beru_profile", R.drawable.beru_profile),
        ProfileImage("igris_profile", R.drawable.igris_profile),
        ProfileImage("iron_profile", R.drawable.iron_profile),
        ProfileImage("tank_profile", R.drawable.tank_profile),
    )

    @DrawableRes
    fun drawableFor(name: String?): Int = when (name) {
        "bellion_profile" -> R.drawable.bellion_profile
        "beru_profile" -> R.drawable.beru_profile
        "igris_profile" -> R.drawable.igris_profile
        "iron_profile" -> R.drawable.iron_profile
        "tank_profile" -> R.drawable.tank_profile
        else -> R.drawable.default_profile
    }

    fun normalizedName(name: String?): String =
        name?.takeIf { candidate -> selectable.any { it.name == candidate } } ?: DEFAULT
}
