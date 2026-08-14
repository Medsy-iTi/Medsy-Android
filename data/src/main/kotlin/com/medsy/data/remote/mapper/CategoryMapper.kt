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
        4 -> R.drawable.ic_cat_brain_nerves
        7 -> R.drawable.ic_cat_antivirals
        8 -> R.drawable.ic_cat_cancer_immunity
        9 -> R.drawable.ic_cat_allergy
        11 -> R.drawable.ic_cat_antiparasitics
        13 -> R.drawable.ic_cat_antibiotics
        19 -> R.drawable.ic_cat_cholesterol
        21 -> R.drawable.ic_cat_antifungals
        22 -> R.drawable.ic_cat_asthma_breathing
        else -> null
    }
}
