package com.miu.bookhub.inventory.repository;

import com.miu.bookhub.inventory.repository.entity.Inventory;
import org.springframework.data.repository.CrudRepository;

public interface InventoryRepository extends CrudRepository<Inventory, Long> {
}
