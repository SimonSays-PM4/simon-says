package ch.zhaw.pm4.simonsays.factory

import ch.zhaw.pm4.simonsays.api.types.IngredientDTO
import ch.zhaw.pm4.simonsays.entity.Event
import ch.zhaw.pm4.simonsays.entity.Ingredient
import ch.zhaw.pm4.simonsays.repository.EventRepository
import ch.zhaw.pm4.simonsays.repository.IngredientRepository
import com.sun.java.accessibility.util.EventID
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
                menuItems = null
        )
        return ingredientRepository.save(ingredient)
    }

}