package com.medsy.domain.favorites.usecase

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.favorites.repository.FavoritesRepository
import com.medsy.domain.favorites.model.FavoriteProduct
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavoritesUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository
) {
    operator fun invoke(userId: Long): Flow<MedsyResult<List<FavoriteProduct>, MedsyError.Local>> {
        return favoritesRepository.getAllFavorites(userId)
    }
}
