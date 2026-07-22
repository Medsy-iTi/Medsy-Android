package com.medsy.domain.cart.usecase

import com.medsy.domain.cart.repository.CartRepository
import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import javax.inject.Inject

class UpdateCartNoteUseCase @Inject constructor(
    private val repository: CartRepository,
) {
    suspend operator fun invoke(
        note: String,
    ): EmptyMedsyResult<MedsyError.Local> = repository.updateNote(note)
}