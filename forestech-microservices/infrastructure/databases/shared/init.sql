-- MySQL dump for Forestech Microservices
-- MIGRATION SCRIPT: Adapts Legacy Data to New Microservices Schema

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ==========================================
-- 1. Table: oil_products
-- Entity: com.forestech.catalog.model.Product
-- ==========================================
DROP TABLE IF EXISTS `oil_products`;
CREATE TABLE `oil_products` (
  `id` varchar(50) NOT NULL,
  `name` varchar(100) NOT NULL,
  `measurement_unit` varchar(20) NOT NULL,
  `unit_price` decimal(10,2) NOT NULL,
  `description` TEXT,
  `is_active` bit(1) DEFAULT 1,
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_name` (`name`),
  KEY `idx_active` (`is_active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Migrated Data from Legacy 'oil_products'
-- Mapping: name -> name, unidadDeMedida -> measurement_unit, priceXUnd -> unit_price
INSERT INTO `oil_products` (`id`, `name`, `measurement_unit`, `unit_price`, `description`, `is_active`, `created_at`) VALUES
('1','ACPM','GALON',0.00,'Migrated Data',1,NOW()),
('10','MOBIL DELVAC MX 15W40 MULTIGRADO GALON','GALON',0.00,'Migrated Data',1,NOW()),
('11','MOBIL FLUID 424 HIDRAULICO GALON','GALON',0.00,'Migrated Data',1,NOW()),
('12','MOBIL FLUID 424 HIDRAULICO GARRAFA','CANECA',0.00,'Migrated Data',1,NOW()),
('13','MOBIL SUPER MIL 20W50 CUARTO','CUARTO',0.00,'Migrated Data',1,NOW()),
('14','MOBIL SUPER MIL 20W50 GALON','GALON',0.00,'Migrated Data',1,NOW()),
('15','SUPERMOTO 2T GALON','GALON',0.00,'Migrated Data',1,NOW()),
('16','TERPEL HIDRAULICO 68 X 18.92 CANECA','CANECA',0.00,'Migrated Data',1,NOW()),
('17','TERPEL LIQUIDO PARA FRENO CUARTO','CUARTO',0.00,'Migrated Data',1,NOW()),
('18','TERPEL ULTREK 140 DIFERENCIALES GALON','GALON',0.00,'Migrated Data',1,NOW()),
('2','GASOLINA CORRIENTE','GALON',0.00,'Migrated Data',1,NOW()),
('3','BATERIA 31H MAC','UNIDAD',0.00,'Migrated Data',1,NOW()),
('4','BRIO UNO AXXIS EP-2 GRASA X 16KG CANECA','UNIDAD',0.00,'Migrated Data',1,NOW()),
('5','BRIO UNO REFRIGERANTE GALON','GALON',0.00,'Migrated Data',1,NOW()),
('6','LUBRIGRAS GRASA CHASIS ROJA X 16K CANECA','CANECA',0.00,'Migrated Data',1,NOW()),
('7','LUBRIGRAS GRASA RODAMIENTO LITIO X 16K CANECA','CANECA',0.00,'Migrated Data',1,NOW()),
('8','LUBRISTONE REFRIGERANTE ROJO GALON','GALON',0.00,'Migrated Data',1,NOW()),
('9','LUBRISTONE REFRIGERANTE VERDE GALON','GALON',0.00,'Migrated Data',1,NOW());

-- ==========================================
-- 2. Table: suppliers
-- Entity: com.forestech.partners.model.Supplier
-- ==========================================
DROP TABLE IF EXISTS `suppliers`;
CREATE TABLE `suppliers` (
  `id` varchar(255) NOT NULL,
  `name` varchar(150) NOT NULL,
  `nit` varchar(20) NOT NULL,
  `telephone` varchar(20),
  `email` varchar(100),
  `address` varchar(255),
  `created_at` datetime(6) DEFAULT CURRENT_TIMESTAMP(6),
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_nit` (`nit`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `suppliers` (`id`, `name`, `nit`, `created_at`) VALUES
('SUP-00000001','FORESTECH DE COLOMBIA SAS','901214780-4','2025-11-12 23:54:27');

-- ==========================================
-- 3. Table: vehicles
-- Entity: com.forestech.fleet.model.Vehicle
-- ==========================================
DROP TABLE IF EXISTS `vehicles`;
CREATE TABLE `vehicles` (
  `id` varchar(50) NOT NULL,
  `placa` varchar(20) NOT NULL,
  `marca` varchar(50) NOT NULL,
  `modelo` varchar(50) NOT NULL,
  `anio` int NOT NULL,
  `category` varchar(20) NOT NULL,
  `descripcion` TEXT,
  `is_active` bit(1) DEFAULT 1,
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_placa` (`placa`),
  KEY `idx_placa` (`placa`),
  KEY `idx_active` (`is_active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- No vehicle data in dump, creating empty table structure.

-- ==========================================
-- 4. Table: facturas
-- Entity: com.forestech.invoicing.model.Factura
-- ==========================================
DROP TABLE IF EXISTS `facturas`;
CREATE TABLE `facturas` (
  `id` varchar(255) NOT NULL,
  `numero_factura` varchar(20) NOT NULL,
  `supplier_id` varchar(255),
  `fecha_emision` date NOT NULL,
  `fecha_vencimiento` date NOT NULL,
  `cliente_nombre` varchar(150),
  `cliente_nit` varchar(20),
  `subtotal` decimal(12,2) NOT NULL,
  `iva` decimal(12,2) NOT NULL,
  `total` decimal(12,2) NOT NULL,
  `observaciones` TEXT,
  `forma_pago` varchar(50),
  `cuenta_bancaria` varchar(50),
  `estado` varchar(50) DEFAULT 'PENDIENTE',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_numero_factura` (`numero_factura`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Migrated Data from Legacy 'facturas'
-- Generating UUIDs for 'id'
INSERT INTO `facturas` (`id`, `numero_factura`, `supplier_id`, `fecha_emision`, `fecha_vencimiento`, `cliente_nombre`, `cliente_nit`, `subtotal`, `iva`, `total`, `observaciones`, `forma_pago`, `cuenta_bancaria`) VALUES
(UUID(),'10734','SUP-00000001','2025-02-05','2025-02-20','FORESTECH DE COLOMBIA SAS','901214780-4',963000.00,0.00,963000.00,'NOVECIENTOS SESENTA Y TRES MIL PESOS CON 00/100 M/L','CREDITO','BANCOLOMBIA No.28979390469'),
(UUID(),'10741','SUP-00000001','2025-02-07','2025-02-22','FORESTECH DE COLOMBIA SAS','901214780-4',9339000.00,0.00,9339000.00,'RETENCION: Nueve millones trescientos treinta y nueve mil pesos con 00/100 M/L','TRANSFERENCIA','BANCOLOMBIA No.28979390469'),
(UUID(),'10742','SUP-00000001','2025-02-07','2025-02-22','FORESTECH DE COLOMBIA SAS','901214780-4',781328.00,3672.00,785000.00,'SETECIENTOS OCHENTA Y CINCO MIL PESOS M/L.','Transferencia/Consignación','BANCOLOMBIA No.28979390469'),
(UUID(),'10785','SUP-00000001','2025-02-24','2025-03-11','FORESTECH DE COLOMBIA SAS','901214780-4',462000.00,0.00,462000.00,'CUATROCIENTOS SESENTA Y DOS MIL PESOS M/L.','Transferencia/Consignación','BANCOLOMBIA No.28979390469'),
(UUID(),'10825','SUP-00000001','2025-03-23','2025-04-07','FORESTECH DE COLOMBIA SAS','901214780-4',6759000.00,0.00,6759000.00,'SEIS MILLONES SETECIENTOS CINCUENTA Y NUEVE MIL PESOS M/L.','Transferencia/Consignación','BANCOLOMBIA No.28979390469'),
(UUID(),'10826','SUP-00000001','2025-03-23','2025-04-07','FORESTECH DE COLOMBIA SAS','901214780-4',395059.00,8941.00,404000.00,'CUATROCIENTOS CUATRO MIL PESOS M/L.','Transferencia/Consignación','BANCOLOMBIA No.28979390469'),
(UUID(),'10856','SUP-00000001','2025-04-23','2025-05-08','FORESTECH DE COLOMBIA SAS','901214780-4',8442000.00,0.00,8442000.00,'OCHO MILLONES CUATROCIENTOS CUARENTA Y DOS MIL PESOS M/L.','Transferencia/Consignación','BANCOLOMBIA No.28979390469'),
(UUID(),'10857','SUP-00000001','2025-04-23','2025-05-08','FORESTECH DE COLOMBIA SAS','901214780-4',544252.00,5748.00,550000.00,'QUINIENTOS CINCUENTA MIL PESOS M/L.','Transferencia/Consignación','BANCOLOMBIA No.28979390469'),
(UUID(),'10909','SUP-00000001','2025-05-06','2025-05-21','FORESTECH DE COLOMBIA SAS','901214780-4',5187000.00,0.00,5187000.00,'CINCO MILLONES CIENTO OCHENTA Y SIETE MIL PESOS M/L.','Transferencia/Consignación','BANCOLOMBIA No.28979390469'),
(UUID(),'10910','SUP-00000001','2025-05-06','2025-05-21','FORESTECH DE COLOMBIA SAS','901214780-4',1481084.00,20916.00,1502000.00,'UN MILLÓN QUINIENTOS DOS MIL PESOS M/L.','Transferencia/Consignación','BANCOLOMBIA No.28979390469'),
(UUID(),'10975','SUP-00000001','2025-06-05','2025-06-20','FORESTECH DE COLOMBIA SAS','901214780-4',4557000.00,0.00,4557000.00,'CUATRO MILLONES QUINIENTOS CINCUENTA Y SIETE MIL PESOS M/L.','Transferencia/Consignación','BANCOLOMBIA No.28979390469'),
(UUID(),'10978','SUP-00000001','2025-06-05','2025-06-20','FORESTECH DE COLOMBIA SAS','901214780-4',906387.00,107613.00,1014000.00,'UN MILLÓN CATORCE MIL PESOS M/L.','Transferencia/Consignación','BANCOLOMBIA No.28979390469'),
(UUID(),'11019','SUP-00000001','2025-06-28','2025-07-13','FORESTECH DE COLOMBIA SAS','901214780-4',218943.00,0.00,218943.00,'DOSCIENTOS DIECIOCHO MIL NOVECIENTOS CUARENTA Y TRES PESOS M/L.','Crédito','BANCOLOMBIA No.28979390469'),
(UUID(),'11038','SUP-00000001','2025-07-12','2025-07-27','FORESTECH DE COLOMBIA SAS','901214780-4',7365400.00,0.00,7365400.00,'SIETE MILLONES TRESCIENTOS SESENTA Y CINCO MIL CUATROCIENTOS PESOS M/L.','Crédito','BANCOLOMBIA No.28979390469'),
(UUID(),'11039','SUP-00000001','2025-07-12','2025-07-27','FORESTECH DE COLOMBIA SAS','901214780-4',443000.00,0.00,443000.00,'CUATROCIENTOS CUARENTA Y TRES MIL PESOS M/L.','Crédito','BANCOLOMBIA No.28979390469'),
(UUID(),'11100','SUP-00000001','2025-08-06','2025-08-21','FORESTECH DE COLOMBIA SAS','901214780-4',822000.00,0.00,822000.00,'OCHOCIENTOS VEINTIDÓS MIL PESOS M/L.','Crédito','BANCOLOMBIA No.28979390469'),
(UUID(),'11111','SUP-00000001','2025-08-11','2025-08-26','FORESTECH DE COLOMBIA SAS','901214780-4',4536000.00,0.00,4536000.00,'CUATRO MILLONES QUINIENTOS TREINTA Y SEIS MIL PESOS M/L.','Crédito','BANCOLOMBIA No.28979390469'),
(UUID(),'11196','SUP-00000001','2025-09-24','2025-10-09','FORESTECH DE COLOMBIA SAS','901214780-4',9372692.00,0.00,9372692.00,'NUEVE MILLONES TRESCIENTOS SETENTA Y DOS MIL SEISCIENTOS NOVENTA Y DOS PESOS M/L.','Crédito','BANCOLOMBIA No.28979390469'),
(UUID(),'11197','SUP-00000001','2025-09-24','2025-10-09','FORESTECH DE COLOMBIA SAS','901214780-4',680000.00,0.00,680000.00,'SEISCIENTOS OCHENTA MIL PESOS M/L.','Crédito','BANCOLOMBIA No.28979390469');

-- ==========================================
-- 5. Table: detalle_factura
-- Entity: com.forestech.invoicing.model.DetalleFactura
-- ==========================================
DROP TABLE IF EXISTS `detalle_factura`;
CREATE TABLE `detalle_factura` (
  `id_detalle` int NOT NULL AUTO_INCREMENT,
  `numero_factura` varchar(20) NOT NULL,
  `product_id` varchar(50),
  `producto` varchar(200) NOT NULL,
  `cantidad` decimal(10,2) NOT NULL,
  `precio_unitario` decimal(12,2) NOT NULL,
  PRIMARY KEY (`id_detalle`),
  KEY `idx_detalle_factura` (`numero_factura`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `detalle_factura` (`id_detalle`, `numero_factura`, `producto`, `cantidad`, `precio_unitario`) VALUES
(1,'10734','GASOLINA CORRIENTE',60.00,16050.00),
(2,'10741','ACPM',600.00,10750.00),
(3,'10741','Gasolina Corriente',180.00,16050.00),
(4,'10742','LUBRIGRAS GRASA CHASIS ROJA X 16K CANECA',1.00,300000.00),
(5,'10742','TERPEL LIQUIDO PARA FRENO CUARTO',1.00,19327.73),
(6,'10742','MOBIL FLUID 424 HIDRAULICO GARRAFA',1.00,462000.00),
(7,'10785','MOBIL FLUID 424 HIDRAULICO GARRAFA',1.00,462000.00),
(8,'10825','ACPM',360.00,10750.00),
(9,'10825','GASOLINA CORRIENTE',180.00,16050.00),
(10,'10826','MOBIL SUPER MIL 20W50 GALON',1.00,132000.00),
(11,'10826','BRIO UNO REFRIGERANTE GALON',2.00,23529.41),
(12,'10826','MOBIL FLUID 424 HIDRAULICO GALON',2.00,108000.00),
(13,'10856','ACPM',600.00,10850.00),
(14,'10856','GASOLINA CORRIENTE',120.00,16100.00),
(15,'10857','TERPEL ULTREK 140 DIFERENCIALES GALON',2.00,95000.00),
(16,'10857','LUBRISTONE REFRIGERANTE VERDE GALON',1.00,30252.10),
(17,'10857','MOBIL FLUID 424 HIDRAULICO GALON',3.00,108000.00),
(18,'10909','ACPM',300.00,10850.00),
(19,'10909','GASOLINA CORRIENTE',120.00,16100.00),
(20,'10910','TERPEL LIQUIDO PARA FRENO CUARTO',1.00,19327.73),
(21,'10910','MOBIL FLUID 424 HIDRAULICO GALON',3.00,108000.00),
(22,'10910','LUBRIGRAS GRASA RODAMIENTO LITIO X 16K CANECA',1.00,566000.00),
(23,'10910','TERPEL ULTREK 140 DIFERENCIALES GALON',3.00,95000.00),
(24,'10910','LUBRISTONE REFRIGERANTE VERDE GALON',3.00,30252.10),
(25,'10910','SUPERMOTO 2T GALON',1.00,120000.00),
(26,'10910','MOBIL SUPER MIL 20W50 CUARTO',2.00,38000.00),
(27,'10975','ACPM',420.00,10850.00),
(28,'10978','BATERIA 31H MAC',1.00,536134.45),
(29,'10978','LUBRISTONE REFRIGERANTE ROJO GALON',1.00,30252.10),
(30,'10978','TERPEL HIDRAULICO 68 X 18.92 CANECA',1.00,340000.00),
(31,'11019','GASOLINA CORRIENTE',13.50,16218.00),
(32,'11038','ACPM',480.00,10850.00),
(33,'11038','GASOLINA CORRIENTE',134.00,16100.00),
(34,'11039','MOBIL SUPER MIL 20W50 GALON',1.00,132000.00),
(35,'11039','MOBIL FLUID 424 HIDRAULICO GALON',2.00,108000.00),
(36,'11039','TERPEL ULTREK 140 DIFERENCIALES GALON',1.00,95000.00),
(37,'11100','SUPERMOTO 2T GALON',1.00,120000.00),
(38,'11100','TERPEL ULTREK 140 DIFERENCIALES GALON',2.00,93000.00),
(39,'11100','MOBIL FLUID 424 HIDRAULICO GARRAFA',1.00,516000.00),
(40,'11111','ACPM',240.00,10850.00),
(41,'11111','GASOLINA CORRIENTE',120.00,16100.00),
(42,'11196','ACPM',600.00,10850.00),
(43,'11196','GASOLINA CORRIENTE',1.00,2862692.00),
(44,'11197','BRIO UNO AXXIS EP-2 GRASA X 16KG CANECA',1.00,560000.00),
(45,'11197','MOBIL DELVAC MX 15W40 MULTIGRADO GALON',1.00,120000.00);

-- ==========================================
-- 6. Table: movements
-- Entity: com.forestech.inventory.model.Movement
-- ==========================================
DROP TABLE IF EXISTS `movements`;
CREATE TABLE `movements` (
  `id` varchar(255) NOT NULL,
  `movement_type` varchar(20),
  `product_id` varchar(255),
  `vehicle_id` varchar(255),
  `quantity` decimal(19,2),
  `unit_price` decimal(19,2),
  `subtotal` decimal(19,2),
  `description` varchar(255),
  `created_at` datetime(6),
  `updated_at` datetime(6),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- No movement data in dump.

SET FOREIGN_KEY_CHECKS = 1;
