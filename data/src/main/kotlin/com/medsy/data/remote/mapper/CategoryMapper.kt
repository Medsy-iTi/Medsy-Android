package com.medsy.data.remote.mapper

import com.medsy.data.remote.dtos.categories.CategoryDto
import com.medsy.designsystem.R
import com.medsy.domain.categories.model.Category

fun CategoryDto.toDomain(): Category {
    return Category(
        id = this.id,
        name = this.name,
        image = getCategoryImageRes(this.id)
    )
}

private fun getCategoryImageRes(id: Int): Int? {
    return when (id) {
        1 -> R.drawable.ic_cat_mental_health
        2 -> R.drawable.ic_cat_pain_relief
        3 -> R.drawable.ic_cat_cold_cough
        4 -> R.drawable.ic_cat_brain_nerves
        5 -> R.drawable.ic_cat_skin_care
        6 -> R.drawable.ic_cat_heart_blood_pressure
        7 -> R.drawable.ic_cat_antivirals
        8 -> R.drawable.ic_cat_cancer_immunity
        9 -> R.drawable.ic_cat_allergy
        10 -> R.drawable.ic_cat_stomach_digestion
        11 -> R.drawable.ic_cat_antiparasitics
        12 -> R.drawable.ic_cat_vitamins_supplements
        13 -> R.drawable.ic_cat_antibiotics
        14 -> R.drawable.ic_cat_diabetes
        15 -> R.drawable.ic_cat_urinary_kidney
        16 -> R.drawable.ic_cat_womens_health
        17 -> R.drawable.ic_cat_mens_health
        18 -> R.drawable.ic_cat_gout
        19 -> R.drawable.ic_cat_cholesterol
        20 -> R.drawable.ic_cat_liver_gallbladder
        21 -> R.drawable.ic_cat_antifungals
        22 -> R.drawable.ic_cat_asthma_breathing
        23 -> R.drawable.ic_cat_hemorrhoids_veins
        24 -> R.drawable.ic_cat_eye_care
        else -> null
    }
}
