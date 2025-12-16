package com.bina.home.data.mapper

import com.bina.home.data.local.database.UserEntity
import com.bina.home.data.model.UserDto
import com.bina.home.domain.model.User

internal fun UserDto.toDomain(): User =
    User(
        img = img,
        name = name.orEmpty(),
        id = id,
        username = username.orEmpty()
    )

internal fun UserDto.toEntity(): UserEntity {
    require(id.isNotBlank()) { "UserDto.id cannot be blank" }

    return UserEntity(
        id = id,
        name = name,
        username = username,
        img = img
    )
}

internal fun UserEntity.toDto(): UserDto =
    UserDto(
        img = img,
        name = name,
        id = id,
        username = username
    )

