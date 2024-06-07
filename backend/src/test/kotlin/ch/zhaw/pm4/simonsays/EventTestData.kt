package ch.zhaw.pm4.simonsays

import ch.zhaw.pm4.simonsays.entity.*

fun getEvent(id: Long = 1,
             ingredients: Set<Ingredient>? = null,
             menuItems: Set<MenuItem>? = null,
             stations: Set<Station>? = null,
             menus: Set<Menu>? = null,
) = Event(
        id,"TestEvent",
        "TestPassword",
        1,
        ingredients= ingredients,
        menuItems = menuItems,
        stations = stations,
        menus = menus
)
