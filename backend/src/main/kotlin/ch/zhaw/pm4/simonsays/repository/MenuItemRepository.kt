package ch.zhaw.pm4.simonsays.repository

import ch.zhaw.pm4.simonsays.entity.MenuItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MenuItemRepository : JpaRepository<MenuItem, Long>
