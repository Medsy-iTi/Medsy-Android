package com.medsy.domain.favorites.usecase

import com.medsy.domain.favorites.repository.FavoritesRepository
import com.medsy.domain.search.model.SearchProduct
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavoritesUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository
) {
    operator fun invoke(): Flow<List<SearchProduct>> {
        return favoritesRepository.getAllFavorites()
    }
}
