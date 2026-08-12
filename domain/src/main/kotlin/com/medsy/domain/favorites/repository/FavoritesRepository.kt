package com.medsy.domain.favorites.repository

import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.favorites.model.FavoriteProduct
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun getAllFavorites(userId: Long): Flow<MedsyResult<List<FavoriteProduct>, MedsyError.Local>>
    suspend fun addFavorite(product: FavoriteProduct, userId: Long): EmptyMedsyResult<MedsyError.Local>
    suspend fun removeFavorite(productId: Int, userId: Long): EmptyMedsyResult<MedsyError.Local>
    suspend fun isFavorite(productId: Int, userId: Long): MedsyResult<Boolean, MedsyError.Local>
}
