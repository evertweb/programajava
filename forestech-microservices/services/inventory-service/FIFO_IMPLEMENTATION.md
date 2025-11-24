# FIFO Implementation Documentation

## Overview
This document details the implementation of the First-In-First-Out (FIFO) logic for inventory management in the `inventory-service`.

## Core Components

### 1. Data Model
- **Movement**: Enhanced with `realCost`, `realUnitPrice`, and `@Version` for optimistic locking.
- **FifoAllocation**: New entity to track the relationship between input movements (entries) and output movements (exits).

### 2. Repository Layer
- **MovementRepository**: Added `findTopByProductIdAndMovementTypeAndRemainingQuantityGreaterThanOrderByCreatedAtAsc` with `PESSIMISTIC_WRITE` lock to ensure concurrency safety during stock allocation.
- **FifoAllocationRepository**: Manages allocation records.

### 3. Service Layer (`MovementService`)
- Extends `BaseService` from `shared-libs` for DRY compliance.
- **`processFifoOutput(Movement salida)`**:
    - Locks available entries.
    - Consumes stock from oldest entries first.
    - Creates `FifoAllocation` records.
    - Calculates `realCost` based on the specific batches consumed.
- **`restoreFifoStockFromAllocations(Movement salida)`**:
    - Triggered when a generic output movement is deleted.
    - Restores stock to the original entries using allocation records.

## Database Migration
- **Script**: `V2__add_fifo_allocations.sql`
- **Changes**:
    - Creates `fifo_allocations` table.
    - Adds `real_cost`, `real_unit_price`, `version` to `movements`.
    - Initializes existing records.

## Usage
- **Creating an Exit**: Simply create a movement with type `SALIDA`. The system automatically allocates stock and calculates costs.
- **Deleting an Exit**: Deleting a `SALIDA` movement automatically restores the stock to the original entries.

## Testing
- Unit tests in `MovementServiceTest` verify:
    - Correct consumption of multiple entries.
    - Accurate `realCost` calculation.
    - Creation of allocation records.
