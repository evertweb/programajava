-- Catalog Database Initialization
USE catalog_db;

CREATE TABLE IF NOT EXISTS oil_products (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    measurement_unit VARCHAR(50) NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_name (name),
    INDEX idx_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert sample data
INSERT INTO oil_products (id, name, measurement_unit, unit_price, description) VALUES
('PROD-001', 'ACPM', 'GALONES', 8500.00, 'Diesel para vehículos'),
('PROD-002', 'Gasolina Corriente', 'GALONES', 9200.00, 'Gasolina 87 octanos'),
('PROD-003', 'Gasolina Extra', 'GALONES', 10500.00, 'Gasolina 92 octanos');
