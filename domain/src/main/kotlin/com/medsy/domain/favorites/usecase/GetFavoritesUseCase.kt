package com.medsy.domain.favorites.usecase

import com.medsy.domain.favorites.repository.FavoritesRepository
import com.medsy.domain.favorites.model.FavoriteProduct
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavoritesUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository
) {
    operator fun invoke(userId: Long): Flow<List<FavoriteProduct>> {
        return favoritesRepository.getAllFavorites(userId)
    }
}
