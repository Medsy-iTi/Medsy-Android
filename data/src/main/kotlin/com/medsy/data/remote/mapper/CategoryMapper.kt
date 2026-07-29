package com.medsy.data.remote.mapper

import com.medsy.data.remote.dtos.categories.CategoryDto
import com.medsy.domain.categories.model.Category
import com.medsy.designsystem.R

fun CategoryDto.toDomain(): Category {
    return Category(
        id = this.id,
        name = this.name,
        image = getCategoryImageRes(this.id)
    )
}

private fun getCategoryImageRes(id: Int): Int? {
    return when (id) {
        1 -> null // MENTAL HEALTH (Pending AI 3D Generation)
        2 -> R.drawable.ic_cat_pain_relief // AI Generated 3D
        3 -> R.drawable.ic_cat_cold_cough // AI Generated 3D
        4 -> null // BRAIN & NERVES (Pending AI 3D Generation)
        5 -> R.drawable.ic_cat_skin_care // AI Generated 3D
        6 -> R.drawable.ic_cat_heart_blood_pressure // AI Generated 3D
        7 -> null // ANTIVIRALS (Pending AI 3D Generation)
        8 -> null // CANCER & IMMUNITY (Pending AI 3D Generation)
        9 -> null // ALLERGY (Pending AI 3D Generation)
        10 -> R.drawable.ic_cat_stomach_digestion // AI Generated 3D
        11 -> null // ANTIPARASITICS (Pending AI 3D Generation)
        12 -> R.drawable.ic_cat_vitamins_supplements // AI Generated 3D
        13 -> R.drawable.ic_cat_antibiotics // AI Generated 3D
        14 -> R.drawable.ic_cat_diabetes // AI Generated 3D
        15 -> null // URINARY & KIDNEY (Pending AI 3D Generation)
        16 -> null // WOMEN'S HEALTH (Pending AI 3D Generation)
        17 -> null // MEN'S HEALTH (Pending AI 3D Generation)
        18 -> null // GOUT (Pending AI 3D Generation)
        19 -> null // CHOLESTEROL (Pending AI 3D Generation)
        20 -> null // LIVER & GALLBLADDER (Pending AI 3D Generation)
        21 -> null // ANTIFUNGALS (Pending AI 3D Generation)
        22 -> null // ASTHMA & BREATHING (Pending AI 3D Generation)
        23 -> null // HEMORRHOIDS & VEINS (Pending AI 3D Generation)
        24 -> null // EYE CARE (Pending AI 3D Generation)
        else -> null
    }
}
