package com.bina.home.helper

import com.bina.home.domain.model.User


object TestUserBuilder {

    val JOHN_DOE = User(
        img = null,
        name = "John",
        id = "1",
        username = "john_doe"
    )

    val ALICE_JOHNSON = User(
        img = null,
        name = "Alice Johnson",
        id = "1",
        username = "alice_johnson"
    )

    val BOB_SMITH = User(
        img = null,
        name = "Bob Smith",
        id = "2",
        username = "bob_smith"
    )

    fun createUserList(vararg users: User): List<User> = users.toList()
}

