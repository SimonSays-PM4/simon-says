package ch.zhaw.pm4.simonsays.factory

import ch.zhaw.pm4.simonsays.entity.Event
import ch.zhaw.pm4.simonsays.entity.Ingredient
import ch.zhaw.pm4.simonsays.repository.IngredientRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class IngredientFactory(
    @Autowired private val ingredientRepository: IngredientRepository,
    @Autowired private val eventFactory: EventFactory
) {
    fun createIngredient(
        name: String = "Default Ingredient Name",
        event: Event = eventFactory.createEvent()
    ): Ingredient {
        val ingredient = Ingredient(
            name = name,
            event = event,
            menuItems = null,
            stations = null
        )
        return ingredientRepository.save(ingredient)
    }

}