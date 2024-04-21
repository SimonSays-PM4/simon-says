package ch.zhaw.pm4.simonsays.repository

import ch.zhaw.pm4.simonsays.entity.FoodOrder
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderRepository: JpaRepository<FoodOrder, Long> {
}