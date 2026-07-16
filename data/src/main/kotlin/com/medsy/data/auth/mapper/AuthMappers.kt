package com.medsy.data.auth.mapper

import com.medsy.data.auth.remote.dto.AuthDataDto
import com.medsy.data.auth.remote.dto.RegisterRequestDto
import com.medsy.data.auth.remote.dto.UserDto
import com.medsy.domain.auth.model.AuthSession
import com.medsy.domain.auth.model.RegisterParams
import com.medsy.domain.auth.model.Role
import com.medsy.domain.auth.model.User

fun AuthDataDto.toDomain(): AuthSession = AuthSession(
    accessToken = accessToken,
    refreshToken = refreshToken,
    user = user.toDomain()
)

fun UserDto.toDomain(): User = User(
    id = id, 
    email = email, 
    firstName = firstName, 
    lastName = lastName,
    role = runCatching { Role.valueOf(role) }.getOrDefault(Role.CUSTOMER), 
    homeAddress = homeAddress, 
    dob = dob
)

fun RegisterParams.toDto() = RegisterRequestDto(
    email = email, 
    phoneNumber = phoneNumber, 
    firstName = firstName, 
    lastName = lastName,
    password = password, 
    role = role.name, 
    homeAddress = homeAddress, 
    dob = dob, 
    pharmacyId = pharmacyId
)
