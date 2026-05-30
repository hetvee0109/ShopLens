package com.shoplens.shoplensbackend.repository;

import com.shoplens.shoplensbackend.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    // Get all inventory items for one owner
    List<Inventory> findByOwnerId(Long ownerId);

    // Get items that need restocking (stock < reorder level)
    // SELECT * FROM inventory WHERE owner_id = ? AND stock_quantity < ?
    List<Inventory> findByOwnerIdAndStockQuantityLessThan(Long ownerId, int quantity);

    // Find a specific item by owner and name
    // Used in "save or update" logic — check if item already exists before inserting
    Optional<Inventory> findByOwnerIdAndItemName(Long ownerId, String itemName);

    // Admin: findAll() is inherited from JpaRepository
}