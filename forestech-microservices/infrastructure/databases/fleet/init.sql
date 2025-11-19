-- Fleet Database Initialization
USE fleet_db;

CREATE TABLE IF NOT EXISTS vehicles (
    id VARCHAR(50) PRIMARY KEY,
    placa VARCHAR(20) UNIQUE NOT NULL,
    category ENUM('MEZCLADORAS', 'MONTACARGA', 'VOLQUETAS', 'CARGADORES', 'GENERAL') NOT NULL,
    brand VARCHAR(50),
    model VARCHAR(50),
    year INT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_placa (placa),
    INDEX idx_category (category),
    INDEX idx_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert sample data
INSERT INTO vehicles (id, placa, category, brand, model, year) VALUES
('VEH-001', 'ABC123', 'MONTACARGA', 'Toyota', 'Forklift', 2020),
('VEH-002', 'DEF456', 'VOLQUETAS', 'Kenworth', 'T800', 2019);
