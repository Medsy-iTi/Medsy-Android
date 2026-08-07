package com.medsy.domain.favorites.usecase

import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import com.medsy.domain.favorites.repository.FavoritesRepository
import javax.inject.Inject

class RemoveFavoriteUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository
) {
    suspend operator fun invoke(productId: Int): EmptyMedsyResult<MedsyError.Local> {
        return favoritesRepository.removeFavorite(productId)
    }
}
