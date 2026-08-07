package com.medsy.domain.favorites.repository

import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.search.model.SearchProduct
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun getAllFavorites(): Flow<List<SearchProduct>>
    suspend fun addFavorite(product: SearchProduct): EmptyMedsyResult<MedsyError.Local>
    suspend fun removeFavorite(productId: Int): EmptyMedsyResult<MedsyError.Local>
    suspend fun isFavorite(productId: Int): MedsyResult<Boolean, MedsyError.Local>
}
