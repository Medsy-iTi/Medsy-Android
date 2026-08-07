package com.medsy.domain.favorites.usecase

import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import com.medsy.domain.favorites.repository.FavoritesRepository
import com.medsy.domain.favorites.model.FavoriteProduct
import javax.inject.Inject

class AddFavoriteUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository
) {
    suspend operator fun invoke(product: FavoriteProduct, userId: Long): EmptyMedsyResult<MedsyError.Local> {
        return favoritesRepository.addFavorite(product, userId)
    }
}
