-- ============================================================
-- Archivo SQL ordenado
-- Base de datos: Ventas_Facturacion
-- Criterio aplicado:
--   1) Se conservaron tablas y ALTER TABLE en orden lógico.
--   2) Se eliminaron procedimientos duplicados.
--   3) Para cada SP se conservó únicamente su última definición.
-- ============================================================
CREATE DATABASE Ventas_Facturacion CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE Ventas_Facturacion;

-- ============================================================
-- TABLAS Y ALTERACIONES
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
    id_user INT PRIMARY KEY AUTO_INCREMENT,
    user_name VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NULL,
    updated_at DATETIME NULL,

    UNIQUE KEY uk_users_user_name (user_name)
);

CREATE TABLE IF NOT EXISTS document_types (
    id_document_type INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    description TEXT NULL,
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NULL,
    updated_at DATETIME NULL,
    deleted_at DATETIME NULL
);

CREATE TABLE IF NOT EXISTS roles (
    id_rol INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT NULL,
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NULL,
    updated_at DATETIME NULL,
    deleted_at DATETIME NULL
);

ALTER TABLE roles
MODIFY id_rol INT NOT NULL AUTO_INCREMENT;

CREATE TABLE IF NOT EXISTS countries (
    id_country INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT NULL,
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NULL,
    updated_at DATETIME NULL,
    deleted_at DATETIME NULL
);

CREATE TABLE IF NOT EXISTS regions (
    id_region INT PRIMARY KEY AUTO_INCREMENT,
    id_country INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT NULL,
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NULL,
    updated_at DATETIME NULL,
    deleted_at DATETIME NULL,
    CONSTRAINT fk_regions_countries
        FOREIGN KEY (id_country) REFERENCES countries(id_country)
);

CREATE TABLE IF NOT EXISTS provinces (
    id_province INT PRIMARY KEY AUTO_INCREMENT,
    id_region INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT NULL,
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NULL,
    updated_at DATETIME NULL,
    deleted_at DATETIME NULL,
    CONSTRAINT fk_provinces_regions
        FOREIGN KEY (id_region) REFERENCES regions(id_region)
);

CREATE TABLE IF NOT EXISTS districts (
    id_district INT PRIMARY KEY AUTO_INCREMENT,
    id_province INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT NULL,
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NULL,
    updated_at DATETIME NULL,
    deleted_at DATETIME NULL,
    CONSTRAINT fk_districts_provinces
        FOREIGN KEY (id_province) REFERENCES provinces(id_province)
);

CREATE TABLE IF NOT EXISTS employees (
    id_employee INT PRIMARY KEY AUTO_INCREMENT,

    name VARCHAR(100) NOT NULL,
    last_name_paternal VARCHAR(100) NOT NULL,
    last_name_maternal VARCHAR(100) NOT NULL,

    id_document_type INT NULL,
    document_number VARCHAR(30) NULL,
    phone VARCHAR(20) NULL,
    email VARCHAR(150) NULL,
    address TEXT NULL,

    id_regions INT NULL,
    id_provinces INT NULL,
    id_district INT NULL,

    id_rol INT NOT NULL,

    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NULL,
    updated_at DATETIME NULL,
    deleted_at DATETIME NULL,

    UNIQUE KEY uk_employees_email (email),

    CONSTRAINT fk_employees_document_type
        FOREIGN KEY (id_document_type)
        REFERENCES document_types(id_document_type),

    CONSTRAINT fk_employees_region
        FOREIGN KEY (id_regions)
        REFERENCES regions(id_region),

    CONSTRAINT fk_employees_province
        FOREIGN KEY (id_provinces)
        REFERENCES provinces(id_province),

    CONSTRAINT fk_employees_district
        FOREIGN KEY (id_district)
        REFERENCES districts(id_district),

    CONSTRAINT fk_employees_role
        FOREIGN KEY (id_rol)
        REFERENCES roles(id_rol)
);

ALTER TABLE employees
MODIFY id_employee INT NOT NULL AUTO_INCREMENT;

CREATE TABLE `clients` (
  `id_client` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `last_name_paternal` VARCHAR(100) NULL,
  `last_name_maternal` VARCHAR(100) NULL,
  `id_document_type` INT NULL,
  `document_number` VARCHAR(30) NULL,
  `phone` VARCHAR(20) NULL,
  `email` VARCHAR(150) NULL,
  `address` TEXT NULL,
  `id_regions` INT NULL,
  `id_provinces` INT NULL,
  `id_district` INT NULL,
  `status` TINYINT NOT NULL DEFAULT 1,
  `created_at` DATETIME NULL,
  `updated_at` DATETIME NULL,
  `deleted_at` DATETIME NULL,
  PRIMARY KEY (`id_client`),
  CONSTRAINT `fk_clients_document_type`
    FOREIGN KEY (`id_document_type`) REFERENCES `document_types` (`id_document_type`),
  CONSTRAINT `fk_clients_region`
    FOREIGN KEY (`id_regions`) REFERENCES `regions` (`id_region`),
  CONSTRAINT `fk_clients_province`
    FOREIGN KEY (`id_provinces`) REFERENCES `provinces` (`id_province`),
  CONSTRAINT `fk_clients_district`
    FOREIGN KEY (`id_district`) REFERENCES `districts` (`id_district`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS categories (
    id_category INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT NULL,
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NULL,
    updated_at DATETIME NULL,
    deleted_at DATETIME NULL
);

ALTER TABLE categories
MODIFY id_category INT NOT NULL AUTO_INCREMENT;

CREATE TABLE IF NOT EXISTS brands (
    id_brand INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NULL,
    updated_at DATETIME NULL,
    deleted_at DATETIME NULL
);

ALTER TABLE brands
MODIFY id_brand INT NOT NULL AUTO_INCREMENT;

CREATE TABLE IF NOT EXISTS products (
    id_product INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(150) NOT NULL,
    description TEXT NULL,
    cost DECIMAL(10,2) NOT NULL DEFAULT 0,
    profit_margin DECIMAL(10,2) NOT NULL DEFAULT 0,
    price DECIMAL(10,2) NOT NULL DEFAULT 0,
    stock INT NOT NULL DEFAULT 0,
    image LONGTEXT NULL,
    id_category INT NOT NULL,
    id_brand INT NOT NULL,
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NULL,
    updated_at DATETIME NULL,
    deleted_at DATETIME NULL,

    CONSTRAINT fk_products_category
        FOREIGN KEY (id_category)
        REFERENCES categories(id_category),

    CONSTRAINT fk_products_brand
        FOREIGN KEY (id_brand)
        REFERENCES brands(id_brand)
);

ALTER TABLE products
MODIFY id_product INT NOT NULL AUTO_INCREMENT;

CREATE TABLE IF NOT EXISTS suppliers (
    id_supplier INT PRIMARY KEY AUTO_INCREMENT,

    business_name VARCHAR(150) NOT NULL,
    trade_name VARCHAR(150) NULL,

    id_document_type INT NOT NULL,
    document_number VARCHAR(30) NOT NULL,

    phone VARCHAR(20) NULL,
    email VARCHAR(150) NULL,
    address TEXT NULL,

    id_region INT NULL,
    id_province INT NULL,
    id_district INT NULL,

    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NULL,
    updated_at DATETIME NULL,
    deleted_at DATETIME NULL,

    CONSTRAINT fk_suppliers_document_type
        FOREIGN KEY (id_document_type)
        REFERENCES document_types(id_document_type),

    CONSTRAINT fk_suppliers_region
        FOREIGN KEY (id_region)
        REFERENCES regions(id_region),

    CONSTRAINT fk_suppliers_province
        FOREIGN KEY (id_province)
        REFERENCES provinces(id_province),

    CONSTRAINT fk_suppliers_district
        FOREIGN KEY (id_district)
        REFERENCES districts(id_district)
);

ALTER TABLE suppliers
MODIFY id_supplier INT NOT NULL AUTO_INCREMENT;

CREATE TABLE IF NOT EXISTS payment_methods (
    id_payment_method INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    description TEXT NULL,
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NULL,
    updated_at DATETIME NULL,
    deleted_at DATETIME NULL
);

ALTER TABLE payment_methods
MODIFY id_payment_method INT NOT NULL AUTO_INCREMENT;

CREATE TABLE IF NOT EXISTS sales (
    id_sale INT PRIMARY KEY AUTO_INCREMENT,
    id_user INT NOT NULL,
    id_payment_method INT NOT NULL,
    sale_date DATETIME NOT NULL,
    document_kind VARCHAR(20) NOT NULL DEFAULT 'TICKET',
    document_label VARCHAR(100) NOT NULL DEFAULT 'Ticket de venta',
    voucher_series VARCHAR(10) NOT NULL DEFAULT 'T001',
    voucher_number INT NOT NULL DEFAULT 1,
    voucher_code VARCHAR(30) NOT NULL DEFAULT 'T001-00000001',
    customer_document_type_id INT NULL,
    customer_document_number VARCHAR(20) NULL,
    subtotal DECIMAL(10,2) NOT NULL DEFAULT 0,
    discount_amount DECIMAL(10,2) NOT NULL DEFAULT 0,
    igv_amount DECIMAL(10,2) NOT NULL DEFAULT 0,
    total DECIMAL(10,2) NOT NULL DEFAULT 0,
    paid_amount DECIMAL(10,2) NOT NULL DEFAULT 0,
    change_amount DECIMAL(10,2) NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NULL,
    updated_at DATETIME NULL,
    deleted_at DATETIME NULL,

    CONSTRAINT fk_sales_user
        FOREIGN KEY (id_user)
        REFERENCES users(id_user),

    CONSTRAINT fk_sales_payment_method
        FOREIGN KEY (id_payment_method)
        REFERENCES payment_methods(id_payment_method),

    CONSTRAINT fk_sales_document_type
        FOREIGN KEY (customer_document_type_id)
        REFERENCES document_types(id_document_type)
);

CREATE TABLE IF NOT EXISTS sale_details (
    id_sale_detail INT PRIMARY KEY AUTO_INCREMENT,
    id_sale INT NOT NULL,
    id_product INT NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    original_unit_price DECIMAL(10,2) NOT NULL DEFAULT 0,
    discount_type VARCHAR(20) NOT NULL DEFAULT 'NONE',
    discount_value DECIMAL(10,2) NOT NULL DEFAULT 0,
    discount_amount DECIMAL(10,2) NOT NULL DEFAULT 0,
    igv_amount DECIMAL(10,2) NOT NULL DEFAULT 0,
    unit_price DECIMAL(10,2) NOT NULL DEFAULT 0,
    subtotal_before_discount DECIMAL(10,2) NOT NULL DEFAULT 0,
    subtotal DECIMAL(10,2) NOT NULL DEFAULT 0,

    CONSTRAINT fk_sale_details_sale
        FOREIGN KEY (id_sale)
        REFERENCES sales(id_sale),

    CONSTRAINT fk_sale_details_product
        FOREIGN KEY (id_product)
        REFERENCES products(id_product)
);

ALTER TABLE sales MODIFY id_sale INT NOT NULL AUTO_INCREMENT;
ALTER TABLE sale_details MODIFY id_sale_detail INT NOT NULL AUTO_INCREMENT;

CREATE TABLE IF NOT EXISTS stock_movements (
    id_stock_movement INT PRIMARY KEY AUTO_INCREMENT,
    id_product INT NOT NULL,
    movement_type VARCHAR(10) NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    description TEXT NULL,
    reference VARCHAR(80) NULL,
    reference_id INT NULL,
    movement_date DATETIME NOT NULL,
    CONSTRAINT fk_stock_movements_product
        FOREIGN KEY (id_product)
        REFERENCES products(id_product)
);

ALTER TABLE stock_movements
MODIFY id_stock_movement INT NOT NULL AUTO_INCREMENT;

-- IMPORTANTE:
-- Si quieres que las ventas también aparezcan en movimientos de stock,
-- dentro de tu sp_sale_create, después de descontar el stock, agrega:
--
-- INSERT INTO stock_movements (
--     id_product,
--     movement_type,
--     quantity,
--     description,
--     reference,
--     reference_id,
--     movement_date
-- )
-- SELECT
--     id_product,
--     'SALIDA',
--     quantity,
--     'Salida por venta',
--     'SALE',
--     v_id_sale,
--     NOW()
-- FROM tmp_sale_details;

ALTER TABLE sales
ADD COLUMN customer_name VARCHAR(150) NULL AFTER voucher_code;

ALTER TABLE sales
    ADD COLUMN id_client INT NULL AFTER voucher_code,
    ADD INDEX idx_sales_id_client (id_client),
    ADD CONSTRAINT fk_sales_client
        FOREIGN KEY (id_client) REFERENCES clients(id_client);

UPDATE sales s
INNER JOIN clients c
    ON c.id_document_type = s.customer_document_type_id
   AND c.document_number = s.customer_document_number
   AND c.status = 1
   AND c.deleted_at IS NULL
SET
    s.id_client = c.id_client,
    s.customer_name = LEFT(TRIM(CONCAT_WS(' ', c.name, c.last_name_paternal, c.last_name_maternal)), 150)
WHERE s.id_client IS NULL
  AND s.document_kind IN ('BOLETA', 'FACTURA');

ALTER TABLE users
    ADD COLUMN full_name VARCHAR(150) NULL AFTER user_name,
    ADD COLUMN email VARCHAR(150) NULL AFTER full_name,
    ADD COLUMN phone VARCHAR(20) NULL AFTER email,
    ADD COLUMN profile_image_path VARCHAR(500) NULL AFTER phone;

-- ============================================================
-- PROCEDIMIENTOS ALMACENADOS
-- Cada DROP va junto a su CREATE y usa la última versión encontrada.
-- ============================================================

DELIMITER $$

-- ------------------------------------------------------------
-- Usuarios
-- ------------------------------------------------------------

DROP PROCEDURE IF EXISTS `sp_user_register`$$
CREATE PROCEDURE sp_user_register(
    IN p_user_name VARCHAR(100),
    IN p_password VARCHAR(255)
)
BEGIN
    IF EXISTS (
        SELECT 1 
        FROM users 
        WHERE user_name = p_user_name
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El nombre de usuario ya existe';
    ELSE
        INSERT INTO users (
            user_name,
            password,
            status,
            created_at,
            updated_at
        )
        VALUES (
            p_user_name,
            p_password,
            1,
            NOW(),
            NULL
        );
    END IF;
END$$

DROP PROCEDURE IF EXISTS `sp_user_login`$$
CREATE PROCEDURE sp_user_login(
    IN p_user_name VARCHAR(100)
)
BEGIN
    SELECT 
        id_user,
        user_name,
        password,
        status,
        created_at,
        updated_at
    FROM users
    WHERE user_name = p_user_name
      AND status = 1
    LIMIT 1;
END$$


-- ------------------------------------------------------------
-- Tipos de documento
-- ------------------------------------------------------------

DROP PROCEDURE IF EXISTS `sp_document_type_create`$$
CREATE PROCEDURE sp_document_type_create(
    IN p_name VARCHAR(50),
    IN p_description TEXT
)
BEGIN
    IF p_name IS NULL OR TRIM(p_name) = '' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El nombre del tipo de documento es obligatorio';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM document_types
        WHERE LOWER(name) = LOWER(TRIM(p_name))
          AND status = 1
          AND deleted_at IS NULL
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Ya existe un tipo de documento activo con ese nombre';
    END IF;

    INSERT INTO document_types (
        name,
        description,
        status,
        created_at,
        updated_at,
        deleted_at
    )
    VALUES (
        TRIM(p_name),
        p_description,
        1,
        NOW(),
        NULL,
        NULL
    );
END$$

DROP PROCEDURE IF EXISTS `sp_document_type_update`$$
CREATE PROCEDURE sp_document_type_update(
    IN p_id_document_type INT,
    IN p_name VARCHAR(50),
    IN p_description TEXT
)
BEGIN
    IF p_name IS NULL OR TRIM(p_name) = '' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El nombre del tipo de documento es obligatorio';
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM document_types
        WHERE id_document_type = p_id_document_type
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El tipo de documento no existe';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM document_types
        WHERE LOWER(name) = LOWER(TRIM(p_name))
          AND id_document_type <> p_id_document_type
          AND status = 1
          AND deleted_at IS NULL
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Ya existe otro tipo de documento activo con ese nombre';
    END IF;

    UPDATE document_types
    SET name = TRIM(p_name),
        description = p_description,
        updated_at = NOW()
    WHERE id_document_type = p_id_document_type;
END$$

DROP PROCEDURE IF EXISTS `sp_document_type_find_by_id`$$
CREATE PROCEDURE sp_document_type_find_by_id(
    IN p_id_document_type INT
)
BEGIN
    SELECT
        id_document_type,
        name,
        description,
        status,
        created_at,
        updated_at,
        deleted_at
    FROM document_types
    WHERE id_document_type = p_id_document_type
    LIMIT 1;
END$$

DROP PROCEDURE IF EXISTS `sp_document_type_list_active`$$
CREATE PROCEDURE sp_document_type_list_active(
    IN p_search VARCHAR(100),
    IN p_page INT,
    IN p_limit INT
)
BEGIN
    DECLARE v_offset INT DEFAULT 0;

    IF p_page IS NULL OR p_page < 1 THEN
        SET p_page = 1;
    END IF;

    IF p_limit NOT IN (10, 20, 50) THEN
        SET p_limit = 10;
    END IF;

    SET v_offset = (p_page - 1) * p_limit;

    SELECT
        id_document_type,
        name,
        description,
        status,
        created_at,
        updated_at,
        deleted_at
    FROM document_types
    WHERE status = 1
      AND deleted_at IS NULL
      AND (
            p_search IS NULL
            OR TRIM(p_search) = ''
            OR name LIKE CONCAT('%', TRIM(p_search), '%')
            OR description LIKE CONCAT('%', TRIM(p_search), '%')
      )
    ORDER BY id_document_type DESC
    LIMIT v_offset, p_limit;
END$$

DROP PROCEDURE IF EXISTS `sp_document_type_count_active`$$
CREATE PROCEDURE sp_document_type_count_active(
    IN p_search VARCHAR(100)
)
BEGIN
    SELECT COUNT(*) AS total
    FROM document_types
    WHERE status = 1
      AND deleted_at IS NULL
      AND (
            p_search IS NULL
            OR TRIM(p_search) = ''
            OR name LIKE CONCAT('%', TRIM(p_search), '%')
            OR description LIKE CONCAT('%', TRIM(p_search), '%')
      );
END$$

DROP PROCEDURE IF EXISTS `sp_document_type_list_inactive`$$
CREATE PROCEDURE sp_document_type_list_inactive(
    IN p_search VARCHAR(100),
    IN p_page INT,
    IN p_limit INT
)
BEGIN
    DECLARE v_offset INT DEFAULT 0;

    IF p_page IS NULL OR p_page < 1 THEN
        SET p_page = 1;
    END IF;

    IF p_limit NOT IN (10, 20, 50) THEN
        SET p_limit = 10;
    END IF;

    SET v_offset = (p_page - 1) * p_limit;

    SELECT
        id_document_type,
        name,
        description,
        status,
        created_at,
        updated_at,
        deleted_at
    FROM document_types
    WHERE status = 0
      AND (
            p_search IS NULL
            OR TRIM(p_search) = ''
            OR name LIKE CONCAT('%', TRIM(p_search), '%')
            OR description LIKE CONCAT('%', TRIM(p_search), '%')
      )
    ORDER BY deleted_at DESC, id_document_type DESC
    LIMIT v_offset, p_limit;
END$$

DROP PROCEDURE IF EXISTS `sp_document_type_count_inactive`$$
CREATE PROCEDURE sp_document_type_count_inactive(
    IN p_search VARCHAR(100)
)
BEGIN
    SELECT COUNT(*) AS total
    FROM document_types
    WHERE status = 0
      AND (
            p_search IS NULL
            OR TRIM(p_search) = ''
            OR name LIKE CONCAT('%', TRIM(p_search), '%')
            OR description LIKE CONCAT('%', TRIM(p_search), '%')
      );
END$$

DROP PROCEDURE IF EXISTS `sp_document_type_delete_logical`$$
CREATE PROCEDURE sp_document_type_delete_logical(
    IN p_id_document_type INT
)
BEGIN
    UPDATE document_types
    SET status = 0,
        updated_at = NOW(),
        deleted_at = NOW()
    WHERE id_document_type = p_id_document_type
      AND status = 1;
END$$

DROP PROCEDURE IF EXISTS `sp_document_type_restore`$$
CREATE PROCEDURE sp_document_type_restore(
    IN p_id_document_type INT
)
BEGIN
    DECLARE v_name VARCHAR(50);

    SELECT name
    INTO v_name
    FROM document_types
    WHERE id_document_type = p_id_document_type
    LIMIT 1;

    IF v_name IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El tipo de documento no existe';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM document_types
        WHERE LOWER(name) = LOWER(v_name)
          AND id_document_type <> p_id_document_type
          AND status = 1
          AND deleted_at IS NULL
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'No se puede restaurar porque ya existe un tipo activo con el mismo nombre';
    END IF;

    UPDATE document_types
    SET status = 1,
        updated_at = NOW(),
        deleted_at = NULL
    WHERE id_document_type = p_id_document_type;
END$$

DROP PROCEDURE IF EXISTS `sp_document_type_delete_physical`$$
CREATE PROCEDURE sp_document_type_delete_physical(
    IN p_id_document_type INT
)
BEGIN
    DELETE FROM document_types
    WHERE id_document_type = p_id_document_type;
END$$


-- ------------------------------------------------------------
-- Roles
-- ------------------------------------------------------------

DROP PROCEDURE IF EXISTS `sp_role_create`$$
CREATE PROCEDURE sp_role_create(
    IN p_name VARCHAR(100),
    IN p_description TEXT
)
BEGIN
    IF p_name IS NULL OR TRIM(p_name) = '' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El nombre del rol es obligatorio';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM roles
        WHERE LOWER(name) = LOWER(TRIM(p_name))
          AND status = 1
          AND deleted_at IS NULL
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Ya existe un rol activo con ese nombre';
    END IF;

    INSERT INTO roles (
        name,
        description,
        status,
        created_at,
        updated_at,
        deleted_at
    )
    VALUES (
        TRIM(p_name),
        p_description,
        1,
        NOW(),
        NULL,
        NULL
    );
END$$

DROP PROCEDURE IF EXISTS `sp_role_update`$$
CREATE PROCEDURE sp_role_update(
    IN p_id_rol INT,
    IN p_name VARCHAR(100),
    IN p_description TEXT
)
BEGIN
    IF p_name IS NULL OR TRIM(p_name) = '' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El nombre del rol es obligatorio';
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM roles
        WHERE id_rol = p_id_rol
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El rol no existe';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM roles
        WHERE LOWER(name) = LOWER(TRIM(p_name))
          AND id_rol <> p_id_rol
          AND status = 1
          AND deleted_at IS NULL
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Ya existe otro rol activo con ese nombre';
    END IF;

    UPDATE roles
    SET name = TRIM(p_name),
        description = p_description,
        updated_at = NOW()
    WHERE id_rol = p_id_rol;
END$$

DROP PROCEDURE IF EXISTS `sp_role_find_by_id`$$
CREATE PROCEDURE sp_role_find_by_id(
    IN p_id_rol INT
)
BEGIN
    SELECT
        id_rol,
        name,
        description,
        status,
        created_at,
        updated_at,
        deleted_at
    FROM roles
    WHERE id_rol = p_id_rol
    LIMIT 1;
END$$

DROP PROCEDURE IF EXISTS `sp_role_list_active`$$
CREATE PROCEDURE sp_role_list_active(
    IN p_search VARCHAR(150),
    IN p_page INT,
    IN p_limit INT
)
BEGIN
    DECLARE v_offset INT DEFAULT 0;

    IF p_page IS NULL OR p_page < 1 THEN
        SET p_page = 1;
    END IF;

    IF p_limit NOT IN (10, 20, 50) THEN
        SET p_limit = 10;
    END IF;

    SET v_offset = (p_page - 1) * p_limit;

    SELECT
        id_rol,
        name,
        description,
        status,
        created_at,
        updated_at,
        deleted_at
    FROM roles
    WHERE status = 1
      AND deleted_at IS NULL
      AND (
            p_search IS NULL
            OR TRIM(p_search) = ''
            OR name LIKE CONCAT('%', TRIM(p_search), '%')
            OR description LIKE CONCAT('%', TRIM(p_search), '%')
      )
    ORDER BY id_rol DESC
    LIMIT v_offset, p_limit;
END$$

DROP PROCEDURE IF EXISTS `sp_role_count_active`$$
CREATE PROCEDURE sp_role_count_active(
    IN p_search VARCHAR(150)
)
BEGIN
    SELECT COUNT(*) AS total
    FROM roles
    WHERE status = 1
      AND deleted_at IS NULL
      AND (
            p_search IS NULL
            OR TRIM(p_search) = ''
            OR name LIKE CONCAT('%', TRIM(p_search), '%')
            OR description LIKE CONCAT('%', TRIM(p_search), '%')
      );
END$$

DROP PROCEDURE IF EXISTS `sp_role_list_inactive`$$
CREATE PROCEDURE sp_role_list_inactive(
    IN p_search VARCHAR(150),
    IN p_page INT,
    IN p_limit INT
)
BEGIN
    DECLARE v_offset INT DEFAULT 0;

    IF p_page IS NULL OR p_page < 1 THEN
        SET p_page = 1;
    END IF;

    IF p_limit NOT IN (10, 20, 50) THEN
        SET p_limit = 10;
    END IF;

    SET v_offset = (p_page - 1) * p_limit;

    SELECT
        id_rol,
        name,
        description,
        status,
        created_at,
        updated_at,
        deleted_at
    FROM roles
    WHERE status = 0
      AND (
            p_search IS NULL
            OR TRIM(p_search) = ''
            OR name LIKE CONCAT('%', TRIM(p_search), '%')
            OR description LIKE CONCAT('%', TRIM(p_search), '%')
      )
    ORDER BY deleted_at DESC, id_rol DESC
    LIMIT v_offset, p_limit;
END$$

DROP PROCEDURE IF EXISTS `sp_role_count_inactive`$$
CREATE PROCEDURE sp_role_count_inactive(
    IN p_search VARCHAR(150)
)
BEGIN
    SELECT COUNT(*) AS total
    FROM roles
    WHERE status = 0
      AND (
            p_search IS NULL
            OR TRIM(p_search) = ''
            OR name LIKE CONCAT('%', TRIM(p_search), '%')
            OR description LIKE CONCAT('%', TRIM(p_search), '%')
      );
END$$

DROP PROCEDURE IF EXISTS `sp_role_delete_logical`$$
CREATE PROCEDURE sp_role_delete_logical(
    IN p_id_rol INT
)
BEGIN
    UPDATE roles
    SET status = 0,
        updated_at = NOW(),
        deleted_at = NOW()
    WHERE id_rol = p_id_rol
      AND status = 1;
END$$

DROP PROCEDURE IF EXISTS `sp_role_restore`$$
CREATE PROCEDURE sp_role_restore(
    IN p_id_rol INT
)
BEGIN
    DECLARE v_name VARCHAR(100);

    SELECT name
    INTO v_name
    FROM roles
    WHERE id_rol = p_id_rol
    LIMIT 1;

    IF v_name IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El rol no existe';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM roles
        WHERE LOWER(name) = LOWER(v_name)
          AND id_rol <> p_id_rol
          AND status = 1
          AND deleted_at IS NULL
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'No se puede restaurar porque ya existe un rol activo con el mismo nombre';
    END IF;

    UPDATE roles
    SET status = 1,
        updated_at = NOW(),
        deleted_at = NULL
    WHERE id_rol = p_id_rol;
END$$

DROP PROCEDURE IF EXISTS `sp_role_delete_physical`$$
CREATE PROCEDURE sp_role_delete_physical(
    IN p_id_rol INT
)
BEGIN
    DELETE FROM roles
    WHERE id_rol = p_id_rol;
END$$


-- ------------------------------------------------------------
-- Ubicaciones
-- ------------------------------------------------------------

DROP PROCEDURE IF EXISTS `sp_location_create`$$
CREATE PROCEDURE sp_location_create(
    IN p_entity VARCHAR(20),
    IN p_parent_id INT,
    IN p_name VARCHAR(100),
    IN p_description TEXT
)
BEGIN
    IF p_name IS NULL OR TRIM(p_name) = '' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El nombre es obligatorio';
    END IF;

    IF p_entity = 'COUNTRY' THEN

        IF EXISTS (
            SELECT 1 FROM countries
            WHERE LOWER(name) = LOWER(TRIM(p_name))
              AND status = 1
              AND deleted_at IS NULL
        ) THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Ya existe un país activo con ese nombre';
        END IF;

        INSERT INTO countries(name, description, status, created_at)
        VALUES(TRIM(p_name), p_description, 1, NOW());

    ELSEIF p_entity = 'REGION' THEN

        IF p_parent_id IS NULL OR NOT EXISTS (
            SELECT 1 FROM countries
            WHERE id_country = p_parent_id
              AND status = 1
              AND deleted_at IS NULL
        ) THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Seleccione un país activo válido';
        END IF;

        IF EXISTS (
            SELECT 1 FROM regions
            WHERE id_country = p_parent_id
              AND LOWER(name) = LOWER(TRIM(p_name))
              AND status = 1
              AND deleted_at IS NULL
        ) THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Ya existe una región activa con ese nombre en el país seleccionado';
        END IF;

        INSERT INTO regions(id_country, name, description, status, created_at)
        VALUES(p_parent_id, TRIM(p_name), p_description, 1, NOW());

    ELSEIF p_entity = 'PROVINCE' THEN

        IF p_parent_id IS NULL OR NOT EXISTS (
            SELECT 1 FROM regions
            WHERE id_region = p_parent_id
              AND status = 1
              AND deleted_at IS NULL
        ) THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Seleccione una región activa válida';
        END IF;

        IF EXISTS (
            SELECT 1 FROM provinces
            WHERE id_region = p_parent_id
              AND LOWER(name) = LOWER(TRIM(p_name))
              AND status = 1
              AND deleted_at IS NULL
        ) THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Ya existe una provincia activa con ese nombre en la región seleccionada';
        END IF;

        INSERT INTO provinces(id_region, name, description, status, created_at)
        VALUES(p_parent_id, TRIM(p_name), p_description, 1, NOW());

    ELSEIF p_entity = 'DISTRICT' THEN

        IF p_parent_id IS NULL OR NOT EXISTS (
            SELECT 1 FROM provinces
            WHERE id_province = p_parent_id
              AND status = 1
              AND deleted_at IS NULL
        ) THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Seleccione una provincia activa válida';
        END IF;

        IF EXISTS (
            SELECT 1 FROM districts
            WHERE id_province = p_parent_id
              AND LOWER(name) = LOWER(TRIM(p_name))
              AND status = 1
              AND deleted_at IS NULL
        ) THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Ya existe un distrito activo con ese nombre en la provincia seleccionada';
        END IF;

        INSERT INTO districts(id_province, name, description, status, created_at)
        VALUES(p_parent_id, TRIM(p_name), p_description, 1, NOW());

    ELSE
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Entidad de ubicación no válida';
    END IF;
END$$

DROP PROCEDURE IF EXISTS `sp_location_update`$$
CREATE PROCEDURE sp_location_update(
    IN p_entity VARCHAR(20),
    IN p_id INT,
    IN p_parent_id INT,
    IN p_name VARCHAR(100),
    IN p_description TEXT
)
BEGIN
    IF p_name IS NULL OR TRIM(p_name) = '' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El nombre es obligatorio';
    END IF;

    IF p_entity = 'COUNTRY' THEN

        IF EXISTS (
            SELECT 1 FROM countries
            WHERE LOWER(name) = LOWER(TRIM(p_name))
              AND id_country <> p_id
              AND status = 1
              AND deleted_at IS NULL
        ) THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Ya existe otro país activo con ese nombre';
        END IF;

        UPDATE countries
        SET name = TRIM(p_name),
            description = p_description,
            updated_at = NOW()
        WHERE id_country = p_id;

    ELSEIF p_entity = 'REGION' THEN

        IF p_parent_id IS NULL OR NOT EXISTS (
            SELECT 1 FROM countries
            WHERE id_country = p_parent_id
              AND status = 1
              AND deleted_at IS NULL
        ) THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Seleccione un país activo válido';
        END IF;

        IF EXISTS (
            SELECT 1 FROM regions
            WHERE id_country = p_parent_id
              AND LOWER(name) = LOWER(TRIM(p_name))
              AND id_region <> p_id
              AND status = 1
              AND deleted_at IS NULL
        ) THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Ya existe otra región activa con ese nombre';
        END IF;

        UPDATE regions
        SET id_country = p_parent_id,
            name = TRIM(p_name),
            description = p_description,
            updated_at = NOW()
        WHERE id_region = p_id;

    ELSEIF p_entity = 'PROVINCE' THEN

        IF p_parent_id IS NULL OR NOT EXISTS (
            SELECT 1 FROM regions
            WHERE id_region = p_parent_id
              AND status = 1
              AND deleted_at IS NULL
        ) THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Seleccione una región activa válida';
        END IF;

        IF EXISTS (
            SELECT 1 FROM provinces
            WHERE id_region = p_parent_id
              AND LOWER(name) = LOWER(TRIM(p_name))
              AND id_province <> p_id
              AND status = 1
              AND deleted_at IS NULL
        ) THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Ya existe otra provincia activa con ese nombre';
        END IF;

        UPDATE provinces
        SET id_region = p_parent_id,
            name = TRIM(p_name),
            description = p_description,
            updated_at = NOW()
        WHERE id_province = p_id;

    ELSEIF p_entity = 'DISTRICT' THEN

        IF p_parent_id IS NULL OR NOT EXISTS (
            SELECT 1 FROM provinces
            WHERE id_province = p_parent_id
              AND status = 1
              AND deleted_at IS NULL
        ) THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Seleccione una provincia activa válida';
        END IF;

        IF EXISTS (
            SELECT 1 FROM districts
            WHERE id_province = p_parent_id
              AND LOWER(name) = LOWER(TRIM(p_name))
              AND id_district <> p_id
              AND status = 1
              AND deleted_at IS NULL
        ) THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Ya existe otro distrito activo con ese nombre';
        END IF;

        UPDATE districts
        SET id_province = p_parent_id,
            name = TRIM(p_name),
            description = p_description,
            updated_at = NOW()
        WHERE id_district = p_id;

    ELSE
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Entidad de ubicación no válida';
    END IF;
END$$

DROP PROCEDURE IF EXISTS `sp_location_find_by_id`$$
CREATE PROCEDURE sp_location_find_by_id(
    IN p_entity VARCHAR(20),
    IN p_id INT
)
BEGIN
    IF p_entity = 'COUNTRY' THEN
        SELECT
            id_country AS id,
            NULL AS parent_id,
            NULL AS parent_name,
            name,
            description,
            status,
            created_at,
            updated_at,
            deleted_at
        FROM countries
        WHERE id_country = p_id
        LIMIT 1;

    ELSEIF p_entity = 'REGION' THEN
        SELECT
            r.id_region AS id,
            r.id_country AS parent_id,
            c.name AS parent_name,
            r.name,
            r.description,
            r.status,
            r.created_at,
            r.updated_at,
            r.deleted_at
        FROM regions r
        INNER JOIN countries c ON c.id_country = r.id_country
        WHERE r.id_region = p_id
        LIMIT 1;

    ELSEIF p_entity = 'PROVINCE' THEN
        SELECT
            p.id_province AS id,
            p.id_region AS parent_id,
            r.name AS parent_name,
            p.name,
            p.description,
            p.status,
            p.created_at,
            p.updated_at,
            p.deleted_at
        FROM provinces p
        INNER JOIN regions r ON r.id_region = p.id_region
        WHERE p.id_province = p_id
        LIMIT 1;

    ELSEIF p_entity = 'DISTRICT' THEN
        SELECT
            d.id_district AS id,
            d.id_province AS parent_id,
            p.name AS parent_name,
            d.name,
            d.description,
            d.status,
            d.created_at,
            d.updated_at,
            d.deleted_at
        FROM districts d
        INNER JOIN provinces p ON p.id_province = d.id_province
        WHERE d.id_district = p_id
        LIMIT 1;
    END IF;
END$$

DROP PROCEDURE IF EXISTS `sp_location_list_active`$$
CREATE PROCEDURE sp_location_list_active(
    IN p_entity VARCHAR(20),
    IN p_search VARCHAR(150),
    IN p_page INT,
    IN p_limit INT
)
BEGIN
    DECLARE v_offset INT DEFAULT 0;

    IF p_page IS NULL OR p_page < 1 THEN
        SET p_page = 1;
    END IF;

    IF p_limit NOT IN (10, 20, 50) THEN
        SET p_limit = 10;
    END IF;

    SET v_offset = (p_page - 1) * p_limit;

    IF p_entity = 'COUNTRY' THEN
        SELECT
            id_country AS id,
            NULL AS parent_id,
            NULL AS parent_name,
            name,
            description,
            status,
            created_at,
            updated_at,
            deleted_at
        FROM countries
        WHERE status = 1
          AND deleted_at IS NULL
          AND (
                p_search IS NULL
                OR TRIM(p_search) = ''
                OR name LIKE CONCAT('%', TRIM(p_search), '%')
                OR description LIKE CONCAT('%', TRIM(p_search), '%')
          )
        ORDER BY id_country DESC
        LIMIT v_offset, p_limit;

    ELSEIF p_entity = 'REGION' THEN
        SELECT
            r.id_region AS id,
            r.id_country AS parent_id,
            c.name AS parent_name,
            r.name,
            r.description,
            r.status,
            r.created_at,
            r.updated_at,
            r.deleted_at
        FROM regions r
        INNER JOIN countries c ON c.id_country = r.id_country
        WHERE r.status = 1
          AND r.deleted_at IS NULL
          AND (
                p_search IS NULL
                OR TRIM(p_search) = ''
                OR r.name LIKE CONCAT('%', TRIM(p_search), '%')
                OR r.description LIKE CONCAT('%', TRIM(p_search), '%')
                OR c.name LIKE CONCAT('%', TRIM(p_search), '%')
          )
        ORDER BY r.id_region DESC
        LIMIT v_offset, p_limit;

    ELSEIF p_entity = 'PROVINCE' THEN
        SELECT
            p.id_province AS id,
            p.id_region AS parent_id,
            r.name AS parent_name,
            p.name,
            p.description,
            p.status,
            p.created_at,
            p.updated_at,
            p.deleted_at
        FROM provinces p
        INNER JOIN regions r ON r.id_region = p.id_region
        WHERE p.status = 1
          AND p.deleted_at IS NULL
          AND (
                p_search IS NULL
                OR TRIM(p_search) = ''
                OR p.name LIKE CONCAT('%', TRIM(p_search), '%')
                OR p.description LIKE CONCAT('%', TRIM(p_search), '%')
                OR r.name LIKE CONCAT('%', TRIM(p_search), '%')
          )
        ORDER BY p.id_province DESC
        LIMIT v_offset, p_limit;

    ELSEIF p_entity = 'DISTRICT' THEN
        SELECT
            d.id_district AS id,
            d.id_province AS parent_id,
            p.name AS parent_name,
            d.name,
            d.description,
            d.status,
            d.created_at,
            d.updated_at,
            d.deleted_at
        FROM districts d
        INNER JOIN provinces p ON p.id_province = d.id_province
        WHERE d.status = 1
          AND d.deleted_at IS NULL
          AND (
                p_search IS NULL
                OR TRIM(p_search) = ''
                OR d.name LIKE CONCAT('%', TRIM(p_search), '%')
                OR d.description LIKE CONCAT('%', TRIM(p_search), '%')
                OR p.name LIKE CONCAT('%', TRIM(p_search), '%')
          )
        ORDER BY d.id_district DESC
        LIMIT v_offset, p_limit;
    END IF;
END$$

DROP PROCEDURE IF EXISTS `sp_location_count_active`$$
CREATE PROCEDURE sp_location_count_active(
    IN p_entity VARCHAR(20),
    IN p_search VARCHAR(150)
)
BEGIN
    IF p_entity = 'COUNTRY' THEN
        SELECT COUNT(*) AS total
        FROM countries
        WHERE status = 1
          AND deleted_at IS NULL
          AND (
                p_search IS NULL
                OR TRIM(p_search) = ''
                OR name LIKE CONCAT('%', TRIM(p_search), '%')
                OR description LIKE CONCAT('%', TRIM(p_search), '%')
          );

    ELSEIF p_entity = 'REGION' THEN
        SELECT COUNT(*) AS total
        FROM regions r
        INNER JOIN countries c ON c.id_country = r.id_country
        WHERE r.status = 1
          AND r.deleted_at IS NULL
          AND (
                p_search IS NULL
                OR TRIM(p_search) = ''
                OR r.name LIKE CONCAT('%', TRIM(p_search), '%')
                OR r.description LIKE CONCAT('%', TRIM(p_search), '%')
                OR c.name LIKE CONCAT('%', TRIM(p_search), '%')
          );

    ELSEIF p_entity = 'PROVINCE' THEN
        SELECT COUNT(*) AS total
        FROM provinces p
        INNER JOIN regions r ON r.id_region = p.id_region
        WHERE p.status = 1
          AND p.deleted_at IS NULL
          AND (
                p_search IS NULL
                OR TRIM(p_search) = ''
                OR p.name LIKE CONCAT('%', TRIM(p_search), '%')
                OR p.description LIKE CONCAT('%', TRIM(p_search), '%')
                OR r.name LIKE CONCAT('%', TRIM(p_search), '%')
          );

    ELSEIF p_entity = 'DISTRICT' THEN
        SELECT COUNT(*) AS total
        FROM districts d
        INNER JOIN provinces p ON p.id_province = d.id_province
        WHERE d.status = 1
          AND d.deleted_at IS NULL
          AND (
                p_search IS NULL
                OR TRIM(p_search) = ''
                OR d.name LIKE CONCAT('%', TRIM(p_search), '%')
                OR d.description LIKE CONCAT('%', TRIM(p_search), '%')
                OR p.name LIKE CONCAT('%', TRIM(p_search), '%')
          );
    END IF;
END$$

DROP PROCEDURE IF EXISTS `sp_location_list_inactive`$$
CREATE PROCEDURE sp_location_list_inactive(
    IN p_entity VARCHAR(20),
    IN p_search VARCHAR(150),
    IN p_page INT,
    IN p_limit INT
)
BEGIN
    DECLARE v_offset INT DEFAULT 0;

    IF p_page IS NULL OR p_page < 1 THEN
        SET p_page = 1;
    END IF;

    IF p_limit NOT IN (10, 20, 50) THEN
        SET p_limit = 10;
    END IF;

    SET v_offset = (p_page - 1) * p_limit;

    IF p_entity = 'COUNTRY' THEN
        SELECT
            id_country AS id,
            NULL AS parent_id,
            NULL AS parent_name,
            name,
            description,
            status,
            created_at,
            updated_at,
            deleted_at
        FROM countries
        WHERE status = 0
          AND (
                p_search IS NULL
                OR TRIM(p_search) = ''
                OR name LIKE CONCAT('%', TRIM(p_search), '%')
                OR description LIKE CONCAT('%', TRIM(p_search), '%')
          )
        ORDER BY deleted_at DESC, id_country DESC
        LIMIT v_offset, p_limit;

    ELSEIF p_entity = 'REGION' THEN
        SELECT
            r.id_region AS id,
            r.id_country AS parent_id,
            c.name AS parent_name,
            r.name,
            r.description,
            r.status,
            r.created_at,
            r.updated_at,
            r.deleted_at
        FROM regions r
        INNER JOIN countries c ON c.id_country = r.id_country
        WHERE r.status = 0
          AND (
                p_search IS NULL
                OR TRIM(p_search) = ''
                OR r.name LIKE CONCAT('%', TRIM(p_search), '%')
                OR r.description LIKE CONCAT('%', TRIM(p_search), '%')
                OR c.name LIKE CONCAT('%', TRIM(p_search), '%')
          )
        ORDER BY r.deleted_at DESC, r.id_region DESC
        LIMIT v_offset, p_limit;

    ELSEIF p_entity = 'PROVINCE' THEN
        SELECT
            p.id_province AS id,
            p.id_region AS parent_id,
            r.name AS parent_name,
            p.name,
            p.description,
            p.status,
            p.created_at,
            p.updated_at,
            p.deleted_at
        FROM provinces p
        INNER JOIN regions r ON r.id_region = p.id_region
        WHERE p.status = 0
          AND (
                p_search IS NULL
                OR TRIM(p_search) = ''
                OR p.name LIKE CONCAT('%', TRIM(p_search), '%')
                OR p.description LIKE CONCAT('%', TRIM(p_search), '%')
                OR r.name LIKE CONCAT('%', TRIM(p_search), '%')
          )
        ORDER BY p.deleted_at DESC, p.id_province DESC
        LIMIT v_offset, p_limit;

    ELSEIF p_entity = 'DISTRICT' THEN
        SELECT
            d.id_district AS id,
            d.id_province AS parent_id,
            p.name AS parent_name,
            d.name,
            d.description,
            d.status,
            d.created_at,
            d.updated_at,
            d.deleted_at
        FROM districts d
        INNER JOIN provinces p ON p.id_province = d.id_province
        WHERE d.status = 0
          AND (
                p_search IS NULL
                OR TRIM(p_search) = ''
                OR d.name LIKE CONCAT('%', TRIM(p_search), '%')
                OR d.description LIKE CONCAT('%', TRIM(p_search), '%')
                OR p.name LIKE CONCAT('%', TRIM(p_search), '%')
          )
        ORDER BY d.deleted_at DESC, d.id_district DESC
        LIMIT v_offset, p_limit;
    END IF;
END$$

DROP PROCEDURE IF EXISTS `sp_location_count_inactive`$$
CREATE PROCEDURE sp_location_count_inactive(
    IN p_entity VARCHAR(20),
    IN p_search VARCHAR(150)
)
BEGIN
    IF p_entity = 'COUNTRY' THEN
        SELECT COUNT(*) AS total
        FROM countries
        WHERE status = 0
          AND (
                p_search IS NULL
                OR TRIM(p_search) = ''
                OR name LIKE CONCAT('%', TRIM(p_search), '%')
                OR description LIKE CONCAT('%', TRIM(p_search), '%')
          );

    ELSEIF p_entity = 'REGION' THEN
        SELECT COUNT(*) AS total
        FROM regions r
        INNER JOIN countries c ON c.id_country = r.id_country
        WHERE r.status = 0
          AND (
                p_search IS NULL
                OR TRIM(p_search) = ''
                OR r.name LIKE CONCAT('%', TRIM(p_search), '%')
                OR r.description LIKE CONCAT('%', TRIM(p_search), '%')
                OR c.name LIKE CONCAT('%', TRIM(p_search), '%')
          );

    ELSEIF p_entity = 'PROVINCE' THEN
        SELECT COUNT(*) AS total
        FROM provinces p
        INNER JOIN regions r ON r.id_region = p.id_region
        WHERE p.status = 0
          AND (
                p_search IS NULL
                OR TRIM(p_search) = ''
                OR p.name LIKE CONCAT('%', TRIM(p_search), '%')
                OR p.description LIKE CONCAT('%', TRIM(p_search), '%')
                OR r.name LIKE CONCAT('%', TRIM(p_search), '%')
          );

    ELSEIF p_entity = 'DISTRICT' THEN
        SELECT COUNT(*) AS total
        FROM districts d
        INNER JOIN provinces p ON p.id_province = d.id_province
        WHERE d.status = 0
          AND (
                p_search IS NULL
                OR TRIM(p_search) = ''
                OR d.name LIKE CONCAT('%', TRIM(p_search), '%')
                OR d.description LIKE CONCAT('%', TRIM(p_search), '%')
                OR p.name LIKE CONCAT('%', TRIM(p_search), '%')
          );
    END IF;
END$$

DROP PROCEDURE IF EXISTS `sp_location_delete_logical`$$
CREATE PROCEDURE sp_location_delete_logical(
    IN p_entity VARCHAR(20),
    IN p_id INT
)
BEGIN
    IF p_entity = 'COUNTRY' THEN
        UPDATE countries
        SET status = 0,
            updated_at = NOW(),
            deleted_at = NOW()
        WHERE id_country = p_id;

    ELSEIF p_entity = 'REGION' THEN
        UPDATE regions
        SET status = 0,
            updated_at = NOW(),
            deleted_at = NOW()
        WHERE id_region = p_id;

    ELSEIF p_entity = 'PROVINCE' THEN
        UPDATE provinces
        SET status = 0,
            updated_at = NOW(),
            deleted_at = NOW()
        WHERE id_province = p_id;

    ELSEIF p_entity = 'DISTRICT' THEN
        UPDATE districts
        SET status = 0,
            updated_at = NOW(),
            deleted_at = NOW()
        WHERE id_district = p_id;
    END IF;
END$$

DROP PROCEDURE IF EXISTS `sp_location_restore`$$
CREATE PROCEDURE sp_location_restore(
    IN p_entity VARCHAR(20),
    IN p_id INT
)
BEGIN
    IF p_entity = 'COUNTRY' THEN
        UPDATE countries
        SET status = 1,
            updated_at = NOW(),
            deleted_at = NULL
        WHERE id_country = p_id;

    ELSEIF p_entity = 'REGION' THEN
        IF NOT EXISTS (
            SELECT 1
            FROM regions r
            INNER JOIN countries c ON c.id_country = r.id_country
            WHERE r.id_region = p_id
              AND c.status = 1
              AND c.deleted_at IS NULL
        ) THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'No se puede restaurar porque su país está inactivo';
        END IF;

        UPDATE regions
        SET status = 1,
            updated_at = NOW(),
            deleted_at = NULL
        WHERE id_region = p_id;

    ELSEIF p_entity = 'PROVINCE' THEN
        IF NOT EXISTS (
            SELECT 1
            FROM provinces p
            INNER JOIN regions r ON r.id_region = p.id_region
            WHERE p.id_province = p_id
              AND r.status = 1
              AND r.deleted_at IS NULL
        ) THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'No se puede restaurar porque su región está inactiva';
        END IF;

        UPDATE provinces
        SET status = 1,
            updated_at = NOW(),
            deleted_at = NULL
        WHERE id_province = p_id;

    ELSEIF p_entity = 'DISTRICT' THEN
        IF NOT EXISTS (
            SELECT 1
            FROM districts d
            INNER JOIN provinces p ON p.id_province = d.id_province
            WHERE d.id_district = p_id
              AND p.status = 1
              AND p.deleted_at IS NULL
        ) THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'No se puede restaurar porque su provincia está inactiva';
        END IF;

        UPDATE districts
        SET status = 1,
            updated_at = NOW(),
            deleted_at = NULL
        WHERE id_district = p_id;
    END IF;
END$$

DROP PROCEDURE IF EXISTS `sp_location_delete_physical`$$
CREATE PROCEDURE sp_location_delete_physical(
    IN p_entity VARCHAR(20),
    IN p_id INT
)
BEGIN
    IF p_entity = 'COUNTRY' THEN
        DELETE FROM countries WHERE id_country = p_id;
    ELSEIF p_entity = 'REGION' THEN
        DELETE FROM regions WHERE id_region = p_id;
    ELSEIF p_entity = 'PROVINCE' THEN
        DELETE FROM provinces WHERE id_province = p_id;
    ELSEIF p_entity = 'DISTRICT' THEN
        DELETE FROM districts WHERE id_district = p_id;
    END IF;
END$$

DROP PROCEDURE IF EXISTS `sp_location_parent_options`$$
CREATE PROCEDURE sp_location_parent_options(
    IN p_entity VARCHAR(20)
)
BEGIN
    IF p_entity = 'REGION' THEN
        SELECT id_country AS id, name
        FROM countries
        WHERE status = 1
          AND deleted_at IS NULL
        ORDER BY name ASC;

    ELSEIF p_entity = 'PROVINCE' THEN
        SELECT id_region AS id, name
        FROM regions
        WHERE status = 1
          AND deleted_at IS NULL
        ORDER BY name ASC;

    ELSEIF p_entity = 'DISTRICT' THEN
        SELECT id_province AS id, name
        FROM provinces
        WHERE status = 1
          AND deleted_at IS NULL
        ORDER BY name ASC;

    ELSE
        SELECT 0 AS id, '' AS name
        WHERE FALSE;
    END IF;
END$$


-- ------------------------------------------------------------
-- Empleados
-- ------------------------------------------------------------

DROP PROCEDURE IF EXISTS `sp_employee_create`$$
CREATE PROCEDURE sp_employee_create(
    IN p_name VARCHAR(100),
    IN p_last_name_paternal VARCHAR(100),
    IN p_last_name_maternal VARCHAR(100),
    IN p_id_document_type INT,
    IN p_document_number VARCHAR(30),
    IN p_phone VARCHAR(20),
    IN p_email VARCHAR(150),
    IN p_address TEXT,
    IN p_id_regions INT,
    IN p_id_provinces INT,
    IN p_id_district INT,
    IN p_id_rol INT
)
BEGIN
    IF p_name IS NULL OR TRIM(p_name) = '' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El nombre del empleado es obligatorio';
    END IF;

    IF p_last_name_paternal IS NULL OR TRIM(p_last_name_paternal) = '' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El apellido paterno es obligatorio';
    END IF;

    IF p_last_name_maternal IS NULL OR TRIM(p_last_name_maternal) = '' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El apellido materno es obligatorio';
    END IF;

    IF p_id_rol IS NULL OR NOT EXISTS (
        SELECT 1
        FROM roles
        WHERE id_rol = p_id_rol
          AND status = 1
          AND deleted_at IS NULL
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Seleccione un rol activo válido';
    END IF;

    IF p_id_document_type IS NOT NULL AND NOT EXISTS (
        SELECT 1
        FROM document_types
        WHERE id_document_type = p_id_document_type
          AND status = 1
          AND deleted_at IS NULL
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Seleccione un tipo de documento activo válido';
    END IF;

    IF p_id_regions IS NOT NULL AND NOT EXISTS (
        SELECT 1
        FROM regions
        WHERE id_region = p_id_regions
          AND status = 1
          AND deleted_at IS NULL
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Seleccione una región activa válida';
    END IF;

    IF p_id_provinces IS NOT NULL AND NOT EXISTS (
        SELECT 1
        FROM provinces
        WHERE id_province = p_id_provinces
          AND id_region = p_id_regions
          AND status = 1
          AND deleted_at IS NULL
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Seleccione una provincia activa relacionada a la región';
    END IF;

    IF p_id_district IS NOT NULL AND NOT EXISTS (
        SELECT 1
        FROM districts
        WHERE id_district = p_id_district
          AND id_province = p_id_provinces
          AND status = 1
          AND deleted_at IS NULL
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Seleccione un distrito activo relacionado a la provincia';
    END IF;

    IF p_email IS NOT NULL AND TRIM(p_email) <> '' AND EXISTS (
        SELECT 1
        FROM employees
        WHERE LOWER(email) = LOWER(TRIM(p_email))
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Ya existe un empleado con ese correo';
    END IF;

    INSERT INTO employees (
        name,
        last_name_paternal,
        last_name_maternal,
        id_document_type,
        document_number,
        phone,
        email,
        address,
        id_regions,
        id_provinces,
        id_district,
        id_rol,
        status,
        created_at
    )
    VALUES (
        TRIM(p_name),
        TRIM(p_last_name_paternal),
        TRIM(p_last_name_maternal),
        p_id_document_type,
        NULLIF(TRIM(p_document_number), ''),
        NULLIF(TRIM(p_phone), ''),
        NULLIF(TRIM(p_email), ''),
        p_address,
        p_id_regions,
        p_id_provinces,
        p_id_district,
        p_id_rol,
        1,
        NOW()
    );
END$$

DROP PROCEDURE IF EXISTS `sp_employee_update`$$
CREATE PROCEDURE sp_employee_update(
    IN p_id_employee INT,
    IN p_name VARCHAR(100),
    IN p_last_name_paternal VARCHAR(100),
    IN p_last_name_maternal VARCHAR(100),
    IN p_id_document_type INT,
    IN p_document_number VARCHAR(30),
    IN p_phone VARCHAR(20),
    IN p_email VARCHAR(150),
    IN p_address TEXT,
    IN p_id_regions INT,
    IN p_id_provinces INT,
    IN p_id_district INT,
    IN p_id_rol INT
)
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM employees
        WHERE id_employee = p_id_employee
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El empleado no existe';
    END IF;

    IF p_name IS NULL OR TRIM(p_name) = '' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El nombre del empleado es obligatorio';
    END IF;

    IF p_last_name_paternal IS NULL OR TRIM(p_last_name_paternal) = '' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El apellido paterno es obligatorio';
    END IF;

    IF p_last_name_maternal IS NULL OR TRIM(p_last_name_maternal) = '' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El apellido materno es obligatorio';
    END IF;

    IF p_id_rol IS NULL OR NOT EXISTS (
        SELECT 1
        FROM roles
        WHERE id_rol = p_id_rol
          AND status = 1
          AND deleted_at IS NULL
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Seleccione un rol activo válido';
    END IF;

    IF p_id_document_type IS NOT NULL AND NOT EXISTS (
        SELECT 1
        FROM document_types
        WHERE id_document_type = p_id_document_type
          AND status = 1
          AND deleted_at IS NULL
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Seleccione un tipo de documento activo válido';
    END IF;

    IF p_id_regions IS NOT NULL AND NOT EXISTS (
        SELECT 1
        FROM regions
        WHERE id_region = p_id_regions
          AND status = 1
          AND deleted_at IS NULL
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Seleccione una región activa válida';
    END IF;

    IF p_id_provinces IS NOT NULL AND NOT EXISTS (
        SELECT 1
        FROM provinces
        WHERE id_province = p_id_provinces
          AND id_region = p_id_regions
          AND status = 1
          AND deleted_at IS NULL
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Seleccione una provincia activa relacionada a la región';
    END IF;

    IF p_id_district IS NOT NULL AND NOT EXISTS (
        SELECT 1
        FROM districts
        WHERE id_district = p_id_district
          AND id_province = p_id_provinces
          AND status = 1
          AND deleted_at IS NULL
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Seleccione un distrito activo relacionado a la provincia';
    END IF;

    IF p_email IS NOT NULL AND TRIM(p_email) <> '' AND EXISTS (
        SELECT 1
        FROM employees
        WHERE LOWER(email) = LOWER(TRIM(p_email))
          AND id_employee <> p_id_employee
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Ya existe otro empleado con ese correo';
    END IF;

    UPDATE employees
    SET name = TRIM(p_name),
        last_name_paternal = TRIM(p_last_name_paternal),
        last_name_maternal = TRIM(p_last_name_maternal),
        id_document_type = p_id_document_type,
        document_number = NULLIF(TRIM(p_document_number), ''),
        phone = NULLIF(TRIM(p_phone), ''),
        email = NULLIF(TRIM(p_email), ''),
        address = p_address,
        id_regions = p_id_regions,
        id_provinces = p_id_provinces,
        id_district = p_id_district,
        id_rol = p_id_rol,
        updated_at = NOW()
    WHERE id_employee = p_id_employee;
END$$

DROP PROCEDURE IF EXISTS `sp_employee_find_by_id`$$
CREATE PROCEDURE sp_employee_find_by_id(
    IN p_id_employee INT
)
BEGIN
    SELECT
        e.id_employee,
        e.name,
        e.last_name_paternal,
        e.last_name_maternal,

        e.id_document_type,
        dt.name AS document_type_name,
        e.document_number,

        e.phone,
        e.email,
        e.address,

        e.id_regions,
        r.name AS region_name,

        e.id_provinces,
        p.name AS province_name,

        e.id_district,
        d.name AS district_name,

        e.id_rol,
        ro.name AS role_name,

        e.status,
        e.created_at,
        e.updated_at,
        e.deleted_at
    FROM employees e
    LEFT JOIN document_types dt ON dt.id_document_type = e.id_document_type
    LEFT JOIN regions r ON r.id_region = e.id_regions
    LEFT JOIN provinces p ON p.id_province = e.id_provinces
    LEFT JOIN districts d ON d.id_district = e.id_district
    INNER JOIN roles ro ON ro.id_rol = e.id_rol
    WHERE e.id_employee = p_id_employee
    LIMIT 1;
END$$

DROP PROCEDURE IF EXISTS `sp_employee_list_active`$$
CREATE PROCEDURE sp_employee_list_active(
    IN p_search VARCHAR(150),
    IN p_page INT,
    IN p_limit INT
)
BEGIN
    DECLARE v_offset INT DEFAULT 0;

    IF p_page IS NULL OR p_page < 1 THEN
        SET p_page = 1;
    END IF;

    IF p_limit NOT IN (10, 20, 50) THEN
        SET p_limit = 10;
    END IF;

    SET v_offset = (p_page - 1) * p_limit;

    SELECT
        e.id_employee,
        e.name,
        e.last_name_paternal,
        e.last_name_maternal,

        e.id_document_type,
        dt.name AS document_type_name,
        e.document_number,

        e.phone,
        e.email,
        e.address,

        e.id_regions,
        r.name AS region_name,

        e.id_provinces,
        p.name AS province_name,

        e.id_district,
        d.name AS district_name,

        e.id_rol,
        ro.name AS role_name,

        e.status,
        e.created_at,
        e.updated_at,
        e.deleted_at
    FROM employees e
    LEFT JOIN document_types dt ON dt.id_document_type = e.id_document_type
    LEFT JOIN regions r ON r.id_region = e.id_regions
    LEFT JOIN provinces p ON p.id_province = e.id_provinces
    LEFT JOIN districts d ON d.id_district = e.id_district
    INNER JOIN roles ro ON ro.id_rol = e.id_rol
    WHERE e.status = 1
      AND e.deleted_at IS NULL
      AND (
            p_search IS NULL
            OR TRIM(p_search) = ''
            OR e.name LIKE CONCAT('%', TRIM(p_search), '%')
            OR e.last_name_paternal LIKE CONCAT('%', TRIM(p_search), '%')
            OR e.last_name_maternal LIKE CONCAT('%', TRIM(p_search), '%')
            OR e.email LIKE CONCAT('%', TRIM(p_search), '%')
            OR e.document_number LIKE CONCAT('%', TRIM(p_search), '%')
            OR ro.name LIKE CONCAT('%', TRIM(p_search), '%')
      )
    ORDER BY e.id_employee DESC
    LIMIT v_offset, p_limit;
END$$

DROP PROCEDURE IF EXISTS `sp_employee_count_active`$$
CREATE PROCEDURE sp_employee_count_active(
    IN p_search VARCHAR(150)
)
BEGIN
    SELECT COUNT(*) AS total
    FROM employees e
    INNER JOIN roles ro ON ro.id_rol = e.id_rol
    WHERE e.status = 1
      AND e.deleted_at IS NULL
      AND (
            p_search IS NULL
            OR TRIM(p_search) = ''
            OR e.name LIKE CONCAT('%', TRIM(p_search), '%')
            OR e.last_name_paternal LIKE CONCAT('%', TRIM(p_search), '%')
            OR e.last_name_maternal LIKE CONCAT('%', TRIM(p_search), '%')
            OR e.email LIKE CONCAT('%', TRIM(p_search), '%')
            OR e.document_number LIKE CONCAT('%', TRIM(p_search), '%')
            OR ro.name LIKE CONCAT('%', TRIM(p_search), '%')
      );
END$$

DROP PROCEDURE IF EXISTS `sp_employee_list_inactive`$$
CREATE PROCEDURE sp_employee_list_inactive(
    IN p_search VARCHAR(150),
    IN p_page INT,
    IN p_limit INT
)
BEGIN
    DECLARE v_offset INT DEFAULT 0;

    IF p_page IS NULL OR p_page < 1 THEN
        SET p_page = 1;
    END IF;

    IF p_limit NOT IN (10, 20, 50) THEN
        SET p_limit = 10;
    END IF;

    SET v_offset = (p_page - 1) * p_limit;

    SELECT
        e.id_employee,
        e.name,
        e.last_name_paternal,
        e.last_name_maternal,

        e.id_document_type,
        dt.name AS document_type_name,
        e.document_number,

        e.phone,
        e.email,
        e.address,

        e.id_regions,
        r.name AS region_name,

        e.id_provinces,
        p.name AS province_name,

        e.id_district,
        d.name AS district_name,

        e.id_rol,
        ro.name AS role_name,

        e.status,
        e.created_at,
        e.updated_at,
        e.deleted_at
    FROM employees e
    LEFT JOIN document_types dt ON dt.id_document_type = e.id_document_type
    LEFT JOIN regions r ON r.id_region = e.id_regions
    LEFT JOIN provinces p ON p.id_province = e.id_provinces
    LEFT JOIN districts d ON d.id_district = e.id_district
    INNER JOIN roles ro ON ro.id_rol = e.id_rol
    WHERE e.status = 0
      AND (
            p_search IS NULL
            OR TRIM(p_search) = ''
            OR e.name LIKE CONCAT('%', TRIM(p_search), '%')
            OR e.last_name_paternal LIKE CONCAT('%', TRIM(p_search), '%')
            OR e.last_name_maternal LIKE CONCAT('%', TRIM(p_search), '%')
            OR e.email LIKE CONCAT('%', TRIM(p_search), '%')
            OR e.document_number LIKE CONCAT('%', TRIM(p_search), '%')
            OR ro.name LIKE CONCAT('%', TRIM(p_search), '%')
      )
    ORDER BY e.deleted_at DESC, e.id_employee DESC
    LIMIT v_offset, p_limit;
END$$

DROP PROCEDURE IF EXISTS `sp_employee_count_inactive`$$
CREATE PROCEDURE sp_employee_count_inactive(
    IN p_search VARCHAR(150)
)
BEGIN
    SELECT COUNT(*) AS total
    FROM employees e
    INNER JOIN roles ro ON ro.id_rol = e.id_rol
    WHERE e.status = 0
      AND (
            p_search IS NULL
            OR TRIM(p_search) = ''
            OR e.name LIKE CONCAT('%', TRIM(p_search), '%')
            OR e.last_name_paternal LIKE CONCAT('%', TRIM(p_search), '%')
            OR e.last_name_maternal LIKE CONCAT('%', TRIM(p_search), '%')
            OR e.email LIKE CONCAT('%', TRIM(p_search), '%')
            OR e.document_number LIKE CONCAT('%', TRIM(p_search), '%')
            OR ro.name LIKE CONCAT('%', TRIM(p_search), '%')
      );
END$$

DROP PROCEDURE IF EXISTS `sp_employee_delete_logical`$$
CREATE PROCEDURE sp_employee_delete_logical(
    IN p_id_employee INT
)
BEGIN
    UPDATE employees
    SET status = 0,
        updated_at = NOW(),
        deleted_at = NOW()
    WHERE id_employee = p_id_employee
      AND status = 1;
END$$

DROP PROCEDURE IF EXISTS `sp_employee_restore`$$
CREATE PROCEDURE sp_employee_restore(
    IN p_id_employee INT
)
BEGIN
    UPDATE employees
    SET status = 1,
        updated_at = NOW(),
        deleted_at = NULL
    WHERE id_employee = p_id_employee;
END$$

DROP PROCEDURE IF EXISTS `sp_employee_delete_physical`$$
CREATE PROCEDURE sp_employee_delete_physical(
    IN p_id_employee INT
)
BEGIN
    DELETE FROM employees
    WHERE id_employee = p_id_employee;
END$$

DROP PROCEDURE IF EXISTS `sp_employee_document_type_options`$$
CREATE PROCEDURE sp_employee_document_type_options()
BEGIN
    SELECT id_document_type AS id, name
    FROM document_types
    WHERE status = 1
      AND deleted_at IS NULL
    ORDER BY name ASC;
END$$

DROP PROCEDURE IF EXISTS `sp_employee_role_options`$$
CREATE PROCEDURE sp_employee_role_options()
BEGIN
    SELECT id_rol AS id, name
    FROM roles
    WHERE status = 1
      AND deleted_at IS NULL
    ORDER BY name ASC;
END$$

DROP PROCEDURE IF EXISTS `sp_employee_region_options`$$
CREATE PROCEDURE sp_employee_region_options()
BEGIN
    SELECT id_region AS id, name
    FROM regions
    WHERE status = 1
      AND deleted_at IS NULL
    ORDER BY name ASC;
END$$

DROP PROCEDURE IF EXISTS `sp_employee_province_options`$$
CREATE PROCEDURE sp_employee_province_options(
    IN p_id_region INT
)
BEGIN
    SELECT id_province AS id, name
    FROM provinces
    WHERE id_region = p_id_region
      AND status = 1
      AND deleted_at IS NULL
    ORDER BY name ASC;
END$$

DROP PROCEDURE IF EXISTS `sp_employee_district_options`$$
CREATE PROCEDURE sp_employee_district_options(
    IN p_id_province INT
)
BEGIN
    SELECT id_district AS id, name
    FROM districts
    WHERE id_province = p_id_province
      AND status = 1
      AND deleted_at IS NULL
    ORDER BY name ASC;
END$$


-- ------------------------------------------------------------
-- Clientes
-- ------------------------------------------------------------

DROP PROCEDURE IF EXISTS `sp_client_create`$$
CREATE PROCEDURE `sp_client_create`(
    IN p_name VARCHAR(100),
    IN p_last_name_paternal VARCHAR(100),
    IN p_last_name_maternal VARCHAR(100),
    IN p_id_document_type INT,
    IN p_document_number VARCHAR(30),
    IN p_phone VARCHAR(20),
    IN p_email VARCHAR(150),
    IN p_address TEXT,
    IN p_id_regions INT,
    IN p_id_provinces INT,
    IN p_id_district INT
)
BEGIN
    INSERT INTO clients (
        name,
        last_name_paternal,
        last_name_maternal,
        id_document_type,
        document_number,
        phone,
        email,
        address,
        id_regions,
        id_provinces,
        id_district,
        status,
        created_at,
        updated_at,
        deleted_at
    )
    VALUES (
        p_name,
        p_last_name_paternal,
        p_last_name_maternal,
        p_id_document_type,
        p_document_number,
        p_phone,
        p_email,
        p_address,
        p_id_regions,
        p_id_provinces,
        p_id_district,
        1,
        NOW(),
        NOW(),
        NULL
    );
END$$

DROP PROCEDURE IF EXISTS `sp_client_update`$$
CREATE PROCEDURE `sp_client_update`(
    IN p_id_client INT,
    IN p_name VARCHAR(100),
    IN p_last_name_paternal VARCHAR(100),
    IN p_last_name_maternal VARCHAR(100),
    IN p_id_document_type INT,
    IN p_document_number VARCHAR(30),
    IN p_phone VARCHAR(20),
    IN p_email VARCHAR(150),
    IN p_address TEXT,
    IN p_id_regions INT,
    IN p_id_provinces INT,
    IN p_id_district INT
)
BEGIN
    UPDATE clients
    SET
        name = p_name,
        last_name_paternal = p_last_name_paternal,
        last_name_maternal = p_last_name_maternal,
        id_document_type = p_id_document_type,
        document_number = p_document_number,
        phone = p_phone,
        email = p_email,
        address = p_address,
        id_regions = p_id_regions,
        id_provinces = p_id_provinces,
        id_district = p_id_district,
        updated_at = NOW()
    WHERE id_client = p_id_client;
END$$

DROP PROCEDURE IF EXISTS `sp_client_find_by_id`$$
CREATE PROCEDURE `sp_client_find_by_id`(
    IN p_id_client INT
)
BEGIN
    SELECT
        c.id_client,
        c.name,
        c.last_name_paternal,
        c.last_name_maternal,
        c.id_document_type,
        dt.name AS document_type_name,
        c.document_number,
        c.phone,
        c.email,
        c.address,
        c.id_regions,
        r.name AS region_name,
        c.id_provinces,
        p.name AS province_name,
        c.id_district,
        d.name AS district_name,
        c.status,
        c.created_at,
        c.updated_at,
        c.deleted_at
    FROM clients c
    LEFT JOIN document_types dt ON dt.id_document_type = c.id_document_type
    LEFT JOIN regions r ON r.id_region = c.id_regions
    LEFT JOIN provinces p ON p.id_province = c.id_provinces
    LEFT JOIN districts d ON d.id_district = c.id_district
    WHERE c.id_client = p_id_client;
END$$

DROP PROCEDURE IF EXISTS `sp_client_list_active`$$
CREATE PROCEDURE `sp_client_list_active`(
    IN p_search VARCHAR(100),
    IN p_page INT,
    IN p_limit INT
)
BEGIN
    DECLARE v_search VARCHAR(100) DEFAULT '';
    DECLARE v_page INT DEFAULT 1;
    DECLARE v_limit INT DEFAULT 10;
    DECLARE v_offset INT DEFAULT 0;

    SET v_search = IFNULL(TRIM(p_search), '');
    SET v_page = IFNULL(p_page, 1);
    SET v_limit = IFNULL(p_limit, 10);

    IF v_page < 1 THEN
        SET v_page = 1;
    END IF;

    IF v_limit <= 0 THEN
        SET v_limit = 10;
    END IF;

    SET v_offset = (v_page - 1) * v_limit;

    SELECT
        c.id_client,
        c.name,
        c.last_name_paternal,
        c.last_name_maternal,
        c.id_document_type,
        dt.name AS document_type_name,
        c.document_number,
        c.phone,
        c.email,
        c.address,
        c.id_regions,
        r.name AS region_name,
        c.id_provinces,
        p.name AS province_name,
        c.id_district,
        d.name AS district_name,
        c.status,
        c.created_at,
        c.updated_at,
        c.deleted_at
    FROM clients c
    LEFT JOIN document_types dt ON dt.id_document_type = c.id_document_type
    LEFT JOIN regions r ON r.id_region = c.id_regions
    LEFT JOIN provinces p ON p.id_province = c.id_provinces
    LEFT JOIN districts d ON d.id_district = c.id_district
    WHERE c.status = 1
      AND c.deleted_at IS NULL
      AND (
        v_search = ''
        OR CONCAT_WS(' ', c.name, c.last_name_paternal, c.last_name_maternal) LIKE CONCAT('%', v_search, '%')
        OR IFNULL(c.document_number, '') LIKE CONCAT('%', v_search, '%')
        OR IFNULL(c.phone, '') LIKE CONCAT('%', v_search, '%')
        OR IFNULL(c.email, '') LIKE CONCAT('%', v_search, '%')
      )
    ORDER BY c.id_client DESC
    LIMIT v_offset, v_limit;
END$$

DROP PROCEDURE IF EXISTS `sp_client_count_active`$$
CREATE PROCEDURE `sp_client_count_active`(
    IN p_search VARCHAR(100)
)
BEGIN
    DECLARE v_search VARCHAR(100) DEFAULT '';

    SET v_search = IFNULL(TRIM(p_search), '');

    SELECT COUNT(*) AS total
    FROM clients c
    WHERE c.status = 1
      AND c.deleted_at IS NULL
      AND (
        v_search = ''
        OR CONCAT_WS(' ', c.name, c.last_name_paternal, c.last_name_maternal) LIKE CONCAT('%', v_search, '%')
        OR IFNULL(c.document_number, '') LIKE CONCAT('%', v_search, '%')
        OR IFNULL(c.phone, '') LIKE CONCAT('%', v_search, '%')
        OR IFNULL(c.email, '') LIKE CONCAT('%', v_search, '%')
      );
END$$

DROP PROCEDURE IF EXISTS `sp_client_list_inactive`$$
CREATE PROCEDURE `sp_client_list_inactive`(
    IN p_search VARCHAR(100),
    IN p_page INT,
    IN p_limit INT
)
BEGIN
    DECLARE v_search VARCHAR(100) DEFAULT '';
    DECLARE v_page INT DEFAULT 1;
    DECLARE v_limit INT DEFAULT 10;
    DECLARE v_offset INT DEFAULT 0;

    SET v_search = IFNULL(TRIM(p_search), '');
    SET v_page = IFNULL(p_page, 1);
    SET v_limit = IFNULL(p_limit, 10);

    IF v_page < 1 THEN
        SET v_page = 1;
    END IF;

    IF v_limit <= 0 THEN
        SET v_limit = 10;
    END IF;

    SET v_offset = (v_page - 1) * v_limit;

    SELECT
        c.id_client,
        c.name,
        c.last_name_paternal,
        c.last_name_maternal,
        c.id_document_type,
        dt.name AS document_type_name,
        c.document_number,
        c.phone,
        c.email,
        c.address,
        c.id_regions,
        r.name AS region_name,
        c.id_provinces,
        p.name AS province_name,
        c.id_district,
        d.name AS district_name,
        c.status,
        c.created_at,
        c.updated_at,
        c.deleted_at
    FROM clients c
    LEFT JOIN document_types dt ON dt.id_document_type = c.id_document_type
    LEFT JOIN regions r ON r.id_region = c.id_regions
    LEFT JOIN provinces p ON p.id_province = c.id_provinces
    LEFT JOIN districts d ON d.id_district = c.id_district
    WHERE c.status = 0
      AND c.deleted_at IS NOT NULL
      AND (
        v_search = ''
        OR CONCAT_WS(' ', c.name, c.last_name_paternal, c.last_name_maternal) LIKE CONCAT('%', v_search, '%')
        OR IFNULL(c.document_number, '') LIKE CONCAT('%', v_search, '%')
        OR IFNULL(c.phone, '') LIKE CONCAT('%', v_search, '%')
        OR IFNULL(c.email, '') LIKE CONCAT('%', v_search, '%')
      )
    ORDER BY c.id_client DESC
    LIMIT v_offset, v_limit;
END$$

DROP PROCEDURE IF EXISTS `sp_client_count_inactive`$$
CREATE PROCEDURE `sp_client_count_inactive`(
    IN p_search VARCHAR(100)
)
BEGIN
    DECLARE v_search VARCHAR(100) DEFAULT '';

    SET v_search = IFNULL(TRIM(p_search), '');

    SELECT COUNT(*) AS total
    FROM clients c
    WHERE c.status = 0
      AND c.deleted_at IS NOT NULL
      AND (
        v_search = ''
        OR CONCAT_WS(' ', c.name, c.last_name_paternal, c.last_name_maternal) LIKE CONCAT('%', v_search, '%')
        OR IFNULL(c.document_number, '') LIKE CONCAT('%', v_search, '%')
        OR IFNULL(c.phone, '') LIKE CONCAT('%', v_search, '%')
        OR IFNULL(c.email, '') LIKE CONCAT('%', v_search, '%')
      );
END$$

DROP PROCEDURE IF EXISTS `sp_client_delete_logical`$$
CREATE PROCEDURE `sp_client_delete_logical`(
    IN p_id_client INT
)
BEGIN
    UPDATE clients
    SET
        status = 0,
        updated_at = NOW(),
        deleted_at = NOW()
    WHERE id_client = p_id_client;
END$$

DROP PROCEDURE IF EXISTS `sp_client_restore`$$
CREATE PROCEDURE `sp_client_restore`(
    IN p_id_client INT
)
BEGIN
    UPDATE clients
    SET
        status = 1,
        updated_at = NOW(),
        deleted_at = NULL
    WHERE id_client = p_id_client;
END$$

DROP PROCEDURE IF EXISTS `sp_client_delete_physical`$$
CREATE PROCEDURE `sp_client_delete_physical`(
    IN p_id_client INT
)
BEGIN
    DELETE FROM clients
    WHERE id_client = p_id_client;
END$$

DROP PROCEDURE IF EXISTS `sp_client_document_type_options`$$
CREATE PROCEDURE `sp_client_document_type_options`()
BEGIN
    SELECT
        dt.id_document_type AS id,
        dt.name AS name
    FROM document_types dt
    WHERE dt.status = 1
    ORDER BY dt.name ASC;
END$$

DROP PROCEDURE IF EXISTS `sp_client_region_options`$$
CREATE PROCEDURE `sp_client_region_options`()
BEGIN
    SELECT
        r.id_region AS id,
        r.name AS name
    FROM regions r
    WHERE r.status = 1
    ORDER BY r.name ASC;
END$$

DROP PROCEDURE IF EXISTS `sp_client_province_options`$$
CREATE PROCEDURE `sp_client_province_options`(
    IN p_id_region INT
)
BEGIN
    SELECT
        p.id_province AS id,
        p.name AS name
    FROM provinces p
    WHERE p.status = 1
      AND p.id_region = p_id_region
    ORDER BY p.name ASC;
END$$

DROP PROCEDURE IF EXISTS `sp_client_district_options`$$
CREATE PROCEDURE `sp_client_district_options`(
    IN p_id_province INT
)
BEGIN
    SELECT
        d.id_district AS id,
        d.name AS name
    FROM districts d
    WHERE d.status = 1
      AND d.id_province = p_id_province
    ORDER BY d.name ASC;
END$$


-- ------------------------------------------------------------
-- Categorias
-- ------------------------------------------------------------

DROP PROCEDURE IF EXISTS `sp_category_create`$$
CREATE PROCEDURE sp_category_create(
    IN p_name VARCHAR(100),
    IN p_description TEXT
)
BEGIN
    IF p_name IS NULL OR TRIM(p_name) = '' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El nombre de la categoría es obligatorio';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM categories
        WHERE LOWER(name) = LOWER(TRIM(p_name))
          AND status = 1
          AND deleted_at IS NULL
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Ya existe una categoría activa con ese nombre';
    END IF;

    INSERT INTO categories (
        name,
        description,
        status,
        created_at,
        updated_at,
        deleted_at
    )
    VALUES (
        TRIM(p_name),
        p_description,
        1,
        NOW(),
        NULL,
        NULL
    );
END$$

DROP PROCEDURE IF EXISTS `sp_category_update`$$
CREATE PROCEDURE sp_category_update(
    IN p_id_category INT,
    IN p_name VARCHAR(100),
    IN p_description TEXT
)
BEGIN
    IF p_name IS NULL OR TRIM(p_name) = '' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El nombre de la categoría es obligatorio';
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM categories
        WHERE id_category = p_id_category
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'La categoría no existe';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM categories
        WHERE LOWER(name) = LOWER(TRIM(p_name))
          AND id_category <> p_id_category
          AND status = 1
          AND deleted_at IS NULL
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Ya existe otra categoría activa con ese nombre';
    END IF;

    UPDATE categories
    SET name = TRIM(p_name),
        description = p_description,
        updated_at = NOW()
    WHERE id_category = p_id_category;
END$$

DROP PROCEDURE IF EXISTS `sp_category_find_by_id`$$
CREATE PROCEDURE sp_category_find_by_id(
    IN p_id_category INT
)
BEGIN
    SELECT
        id_category,
        name,
        description,
        status,
        created_at,
        updated_at,
        deleted_at
    FROM categories
    WHERE id_category = p_id_category
    LIMIT 1;
END$$

DROP PROCEDURE IF EXISTS `sp_category_list_active`$$
CREATE PROCEDURE sp_category_list_active(
    IN p_search VARCHAR(150),
    IN p_page INT,
    IN p_limit INT
)
BEGIN
    DECLARE v_offset INT DEFAULT 0;

    IF p_page IS NULL OR p_page < 1 THEN
        SET p_page = 1;
    END IF;

    IF p_limit NOT IN (10, 20, 50) THEN
        SET p_limit = 10;
    END IF;

    SET v_offset = (p_page - 1) * p_limit;

    SELECT
        id_category,
        name,
        description,
        status,
        created_at,
        updated_at,
        deleted_at
    FROM categories
    WHERE status = 1
      AND deleted_at IS NULL
      AND (
            p_search IS NULL
            OR TRIM(p_search) = ''
            OR name LIKE CONCAT('%', TRIM(p_search), '%')
            OR description LIKE CONCAT('%', TRIM(p_search), '%')
      )
    ORDER BY id_category DESC
    LIMIT v_offset, p_limit;
END$$

DROP PROCEDURE IF EXISTS `sp_category_count_active`$$
CREATE PROCEDURE sp_category_count_active(
    IN p_search VARCHAR(150)
)
BEGIN
    SELECT COUNT(*) AS total
    FROM categories
    WHERE status = 1
      AND deleted_at IS NULL
      AND (
            p_search IS NULL
            OR TRIM(p_search) = ''
            OR name LIKE CONCAT('%', TRIM(p_search), '%')
            OR description LIKE CONCAT('%', TRIM(p_search), '%')
      );
END$$

DROP PROCEDURE IF EXISTS `sp_category_list_inactive`$$
CREATE PROCEDURE sp_category_list_inactive(
    IN p_search VARCHAR(150),
    IN p_page INT,
    IN p_limit INT
)
BEGIN
    DECLARE v_offset INT DEFAULT 0;

    IF p_page IS NULL OR p_page < 1 THEN
        SET p_page = 1;
    END IF;

    IF p_limit NOT IN (10, 20, 50) THEN
        SET p_limit = 10;
    END IF;

    SET v_offset = (p_page - 1) * p_limit;

    SELECT
        id_category,
        name,
        description,
        status,
        created_at,
        updated_at,
        deleted_at
    FROM categories
    WHERE status = 0
      AND (
            p_search IS NULL
            OR TRIM(p_search) = ''
            OR name LIKE CONCAT('%', TRIM(p_search), '%')
            OR description LIKE CONCAT('%', TRIM(p_search), '%')
      )
    ORDER BY deleted_at DESC, id_category DESC
    LIMIT v_offset, p_limit;
END$$

DROP PROCEDURE IF EXISTS `sp_category_count_inactive`$$
CREATE PROCEDURE sp_category_count_inactive(
    IN p_search VARCHAR(150)
)
BEGIN
    SELECT COUNT(*) AS total
    FROM categories
    WHERE status = 0
      AND (
            p_search IS NULL
            OR TRIM(p_search) = ''
            OR name LIKE CONCAT('%', TRIM(p_search), '%')
            OR description LIKE CONCAT('%', TRIM(p_search), '%')
      );
END$$

DROP PROCEDURE IF EXISTS `sp_category_delete_logical`$$
CREATE PROCEDURE sp_category_delete_logical(
    IN p_id_category INT
)
BEGIN
    UPDATE categories
    SET status = 0,
        updated_at = NOW(),
        deleted_at = NOW()
    WHERE id_category = p_id_category
      AND status = 1;
END$$

DROP PROCEDURE IF EXISTS `sp_category_restore`$$
CREATE PROCEDURE sp_category_restore(
    IN p_id_category INT
)
BEGIN
    DECLARE v_name VARCHAR(100);

    SELECT name
    INTO v_name
    FROM categories
    WHERE id_category = p_id_category
    LIMIT 1;

    IF v_name IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'La categoría no existe';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM categories
        WHERE LOWER(name) = LOWER(v_name)
          AND id_category <> p_id_category
          AND status = 1
          AND deleted_at IS NULL
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'No se puede restaurar porque ya existe una categoría activa con el mismo nombre';
    END IF;

    UPDATE categories
    SET status = 1,
        updated_at = NOW(),
        deleted_at = NULL
    WHERE id_category = p_id_category;
END$$

DROP PROCEDURE IF EXISTS `sp_category_delete_physical`$$
CREATE PROCEDURE sp_category_delete_physical(
    IN p_id_category INT
)
BEGIN
    DELETE FROM categories
    WHERE id_category = p_id_category;
END$$


-- ------------------------------------------------------------
-- Marcas
-- ------------------------------------------------------------

DROP PROCEDURE IF EXISTS `sp_brand_create`$$
CREATE PROCEDURE sp_brand_create(
    IN p_name VARCHAR(100)
)
BEGIN
    IF p_name IS NULL OR TRIM(p_name) = '' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El nombre de la marca es obligatorio';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM brands
        WHERE LOWER(name) = LOWER(TRIM(p_name))
          AND status = 1
          AND deleted_at IS NULL
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Ya existe una marca activa con ese nombre';
    END IF;

    INSERT INTO brands (
        name,
        status,
        created_at,
        updated_at,
        deleted_at
    )
    VALUES (
        TRIM(p_name),
        1,
        NOW(),
        NULL,
        NULL
    );
END$$

DROP PROCEDURE IF EXISTS `sp_brand_update`$$
CREATE PROCEDURE sp_brand_update(
    IN p_id_brand INT,
    IN p_name VARCHAR(100)
)
BEGIN
    IF p_name IS NULL OR TRIM(p_name) = '' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El nombre de la marca es obligatorio';
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM brands
        WHERE id_brand = p_id_brand
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'La marca no existe';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM brands
        WHERE LOWER(name) = LOWER(TRIM(p_name))
          AND id_brand <> p_id_brand
          AND status = 1
          AND deleted_at IS NULL
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Ya existe otra marca activa con ese nombre';
    END IF;

    UPDATE brands
    SET name = TRIM(p_name),
        updated_at = NOW()
    WHERE id_brand = p_id_brand;
END$$

DROP PROCEDURE IF EXISTS `sp_brand_find_by_id`$$
CREATE PROCEDURE sp_brand_find_by_id(
    IN p_id_brand INT
)
BEGIN
    SELECT
        id_brand,
        name,
        status,
        created_at,
        updated_at,
        deleted_at
    FROM brands
    WHERE id_brand = p_id_brand
    LIMIT 1;
END$$

DROP PROCEDURE IF EXISTS `sp_brand_list_active`$$
CREATE PROCEDURE sp_brand_list_active(
    IN p_search VARCHAR(150),
    IN p_page INT,
    IN p_limit INT
)
BEGIN
    DECLARE v_offset INT DEFAULT 0;

    IF p_page IS NULL OR p_page < 1 THEN
        SET p_page = 1;
    END IF;

    IF p_limit NOT IN (10, 20, 50) THEN
        SET p_limit = 10;
    END IF;

    SET v_offset = (p_page - 1) * p_limit;

    SELECT
        id_brand,
        name,
        status,
        created_at,
        updated_at,
        deleted_at
    FROM brands
    WHERE status = 1
      AND deleted_at IS NULL
      AND (
            p_search IS NULL
            OR TRIM(p_search) = ''
            OR name LIKE CONCAT('%', TRIM(p_search), '%')
      )
    ORDER BY id_brand DESC
    LIMIT v_offset, p_limit;
END$$

DROP PROCEDURE IF EXISTS `sp_brand_count_active`$$
CREATE PROCEDURE sp_brand_count_active(
    IN p_search VARCHAR(150)
)
BEGIN
    SELECT COUNT(*) AS total
    FROM brands
    WHERE status = 1
      AND deleted_at IS NULL
      AND (
            p_search IS NULL
            OR TRIM(p_search) = ''
            OR name LIKE CONCAT('%', TRIM(p_search), '%')
      );
END$$

DROP PROCEDURE IF EXISTS `sp_brand_list_inactive`$$
CREATE PROCEDURE sp_brand_list_inactive(
    IN p_search VARCHAR(150),
    IN p_page INT,
    IN p_limit INT
)
BEGIN
    DECLARE v_offset INT DEFAULT 0;

    IF p_page IS NULL OR p_page < 1 THEN
        SET p_page = 1;
    END IF;

    IF p_limit NOT IN (10, 20, 50) THEN
        SET p_limit = 10;
    END IF;

    SET v_offset = (p_page - 1) * p_limit;

    SELECT
        id_brand,
        name,
        status,
        created_at,
        updated_at,
        deleted_at
    FROM brands
    WHERE status = 0
      AND (
            p_search IS NULL
            OR TRIM(p_search) = ''
            OR name LIKE CONCAT('%', TRIM(p_search), '%')
      )
    ORDER BY deleted_at DESC, id_brand DESC
    LIMIT v_offset, p_limit;
END$$

DROP PROCEDURE IF EXISTS `sp_brand_count_inactive`$$
CREATE PROCEDURE sp_brand_count_inactive(
    IN p_search VARCHAR(150)
)
BEGIN
    SELECT COUNT(*) AS total
    FROM brands
    WHERE status = 0
      AND (
            p_search IS NULL
            OR TRIM(p_search) = ''
            OR name LIKE CONCAT('%', TRIM(p_search), '%')
      );
END$$

DROP PROCEDURE IF EXISTS `sp_brand_delete_logical`$$
CREATE PROCEDURE sp_brand_delete_logical(
    IN p_id_brand INT
)
BEGIN
    UPDATE brands
    SET status = 0,
        updated_at = NOW(),
        deleted_at = NOW()
    WHERE id_brand = p_id_brand
      AND status = 1;
END$$

DROP PROCEDURE IF EXISTS `sp_brand_restore`$$
CREATE PROCEDURE sp_brand_restore(
    IN p_id_brand INT
)
BEGIN
    DECLARE v_name VARCHAR(100);

    SELECT name
    INTO v_name
    FROM brands
    WHERE id_brand = p_id_brand
    LIMIT 1;

    IF v_name IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'La marca no existe';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM brands
        WHERE LOWER(name) = LOWER(v_name)
          AND id_brand <> p_id_brand
          AND status = 1
          AND deleted_at IS NULL
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'No se puede restaurar porque ya existe una marca activa con el mismo nombre';
    END IF;

    UPDATE brands
    SET status = 1,
        updated_at = NOW(),
        deleted_at = NULL
    WHERE id_brand = p_id_brand;
END$$

DROP PROCEDURE IF EXISTS `sp_brand_delete_physical`$$
CREATE PROCEDURE sp_brand_delete_physical(
    IN p_id_brand INT
)
BEGIN
    DELETE FROM brands
    WHERE id_brand = p_id_brand;
END$$


-- ------------------------------------------------------------
-- Productos
-- ------------------------------------------------------------

DROP PROCEDURE IF EXISTS `sp_product_create`$$
CREATE PROCEDURE sp_product_create(
    IN p_name VARCHAR(150),
    IN p_description TEXT,
    IN p_cost DECIMAL(10,2),
    IN p_profit_margin DECIMAL(10,2),
    IN p_price DECIMAL(10,2),
    IN p_stock INT,
    IN p_image LONGTEXT,
    IN p_id_category INT,
    IN p_id_brand INT
)
BEGIN
    DECLARE v_new_id_product INT;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    START TRANSACTION;

    IF p_name IS NULL OR TRIM(p_name) = '' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El nombre del producto es obligatorio';
    END IF;

    IF p_cost IS NULL OR p_cost < 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El costo no puede ser negativo';
    END IF;

    IF p_profit_margin IS NULL OR p_profit_margin < 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El margen de ganancia no puede ser negativo';
    END IF;

    IF p_price IS NULL OR p_price < 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El precio no puede ser negativo';
    END IF;

    IF p_stock IS NULL OR p_stock < 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El stock no puede ser negativo';
    END IF;

    IF p_id_category IS NULL OR NOT EXISTS (
        SELECT 1
        FROM categories
        WHERE id_category = p_id_category
          AND status = 1
          AND deleted_at IS NULL
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Seleccione una categoría activa válida';
    END IF;

    IF p_id_brand IS NULL OR NOT EXISTS (
        SELECT 1
        FROM brands
        WHERE id_brand = p_id_brand
          AND status = 1
          AND deleted_at IS NULL
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Seleccione una marca activa válida';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM products
        WHERE LOWER(name) = LOWER(TRIM(p_name))
          AND status = 1
          AND deleted_at IS NULL
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Ya existe un producto activo con ese nombre';
    END IF;

    INSERT INTO products (
        name,
        description,
        cost,
        profit_margin,
        price,
        stock,
        image,
        id_category,
        id_brand,
        status,
        created_at,
        updated_at,
        deleted_at
    )
    VALUES (
        TRIM(p_name),
        NULLIF(TRIM(p_description), ''),
        p_cost,
        p_profit_margin,
        p_price,
        p_stock,
        p_image,
        p_id_category,
        p_id_brand,
        1,
        NOW(),
        NULL,
        NULL
    );

    SET v_new_id_product = LAST_INSERT_ID();

    IF p_stock > 0 THEN
        INSERT INTO stock_movements (
            id_product,
            movement_type,
            quantity,
            description,
            reference,
            reference_id,
            movement_date
        )
        VALUES (
            v_new_id_product,
            'ENTRADA',
            p_stock,
            'Stock inicial al registrar producto',
            'PRODUCT_CREATE',
            v_new_id_product,
            NOW()
        );
    END IF;

    COMMIT;
END$$

DROP PROCEDURE IF EXISTS `sp_product_update`$$
CREATE PROCEDURE sp_product_update(
    IN p_id_product INT,
    IN p_name VARCHAR(150),
    IN p_description TEXT,
    IN p_cost DECIMAL(10,2),
    IN p_profit_margin DECIMAL(10,2),
    IN p_price DECIMAL(10,2),
    IN p_stock INT,
    IN p_image LONGTEXT,
    IN p_id_category INT,
    IN p_id_brand INT
)
BEGIN
    DECLARE v_old_stock INT DEFAULT 0;
    DECLARE v_difference INT DEFAULT 0;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    START TRANSACTION;

    IF NOT EXISTS (
        SELECT 1
        FROM products
        WHERE id_product = p_id_product
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El producto no existe';
    END IF;

    IF p_name IS NULL OR TRIM(p_name) = '' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El nombre del producto es obligatorio';
    END IF;

    IF p_cost IS NULL OR p_cost < 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El costo no puede ser negativo';
    END IF;

    IF p_profit_margin IS NULL OR p_profit_margin < 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El margen de ganancia no puede ser negativo';
    END IF;

    IF p_price IS NULL OR p_price < 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El precio no puede ser negativo';
    END IF;

    IF p_stock IS NULL OR p_stock < 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El stock no puede ser negativo';
    END IF;

    IF p_id_category IS NULL OR NOT EXISTS (
        SELECT 1
        FROM categories
        WHERE id_category = p_id_category
          AND status = 1
          AND deleted_at IS NULL
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Seleccione una categoría activa válida';
    END IF;

    IF p_id_brand IS NULL OR NOT EXISTS (
        SELECT 1
        FROM brands
        WHERE id_brand = p_id_brand
          AND status = 1
          AND deleted_at IS NULL
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Seleccione una marca activa válida';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM products
        WHERE LOWER(name) = LOWER(TRIM(p_name))
          AND id_product <> p_id_product
          AND status = 1
          AND deleted_at IS NULL
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Ya existe otro producto activo con ese nombre';
    END IF;

    SELECT stock
    INTO v_old_stock
    FROM products
    WHERE id_product = p_id_product
    FOR UPDATE;

    UPDATE products
    SET name = TRIM(p_name),
        description = NULLIF(TRIM(p_description), ''),
        cost = p_cost,
        profit_margin = p_profit_margin,
        price = p_price,
        stock = p_stock,
        image = p_image,
        id_category = p_id_category,
        id_brand = p_id_brand,
        updated_at = NOW()
    WHERE id_product = p_id_product;

    IF p_stock > v_old_stock THEN
        SET v_difference = p_stock - v_old_stock;

        INSERT INTO stock_movements (
            id_product,
            movement_type,
            quantity,
            description,
            reference,
            reference_id,
            movement_date
        )
        VALUES (
            p_id_product,
            'ENTRADA',
            v_difference,
            CONCAT('Entrada por aumento de stock. Stock anterior: ', v_old_stock, ', stock nuevo: ', p_stock),
            'PRODUCT_UPDATE',
            p_id_product,
            NOW()
        );
    END IF;

    IF p_stock < v_old_stock THEN
        SET v_difference = v_old_stock - p_stock;

        INSERT INTO stock_movements (
            id_product,
            movement_type,
            quantity,
            description,
            reference,
            reference_id,
            movement_date
        )
        VALUES (
            p_id_product,
            'SALIDA',
            v_difference,
            CONCAT('Salida por disminución de stock. Stock anterior: ', v_old_stock, ', stock nuevo: ', p_stock),
            'PRODUCT_UPDATE',
            p_id_product,
            NOW()
        );
    END IF;

    COMMIT;
END$$

DROP PROCEDURE IF EXISTS `sp_product_find_by_id`$$
CREATE PROCEDURE sp_product_find_by_id(
    IN p_id_product INT
)
BEGIN
    SELECT
        p.id_product,
        p.name,
        p.description,
        p.cost,
        p.profit_margin,
        p.price,
        p.stock,
        p.image,
        p.id_category,
        c.name AS category_name,
        p.id_brand,
        b.name AS brand_name,
        p.status,
        p.created_at,
        p.updated_at,
        p.deleted_at
    FROM products p
    INNER JOIN categories c ON c.id_category = p.id_category
    INNER JOIN brands b ON b.id_brand = p.id_brand
    WHERE p.id_product = p_id_product
    LIMIT 1;
END$$

DROP PROCEDURE IF EXISTS `sp_product_list_active`$$
CREATE PROCEDURE sp_product_list_active(
    IN p_search VARCHAR(200),
    IN p_page INT,
    IN p_limit INT
)
BEGIN
    DECLARE v_offset INT DEFAULT 0;

    IF p_page IS NULL OR p_page < 1 THEN
        SET p_page = 1;
    END IF;

    IF p_limit NOT IN (10, 20, 50) THEN
        SET p_limit = 10;
    END IF;

    SET v_offset = (p_page - 1) * p_limit;

    SELECT
        p.id_product,
        p.name,
        p.description,
        p.cost,
        p.profit_margin,
        p.price,
        p.stock,
        NULL AS image,
        p.id_category,
        c.name AS category_name,
        p.id_brand,
        b.name AS brand_name,
        p.status,
        p.created_at,
        p.updated_at,
        p.deleted_at
    FROM products p
    INNER JOIN categories c ON c.id_category = p.id_category
    INNER JOIN brands b ON b.id_brand = p.id_brand
    WHERE p.status = 1
      AND p.deleted_at IS NULL
      AND (
            p_search IS NULL
            OR TRIM(p_search) = ''
            OR p.name LIKE CONCAT('%', TRIM(p_search), '%')
            OR p.description LIKE CONCAT('%', TRIM(p_search), '%')
            OR c.name LIKE CONCAT('%', TRIM(p_search), '%')
            OR b.name LIKE CONCAT('%', TRIM(p_search), '%')
      )
    ORDER BY p.id_product DESC
    LIMIT v_offset, p_limit;
END$$

DROP PROCEDURE IF EXISTS `sp_product_count_active`$$
CREATE PROCEDURE sp_product_count_active(
    IN p_search VARCHAR(200)
)
BEGIN
    SELECT COUNT(*) AS total
    FROM products p
    INNER JOIN categories c ON c.id_category = p.id_category
    INNER JOIN brands b ON b.id_brand = p.id_brand
    WHERE p.status = 1
      AND p.deleted_at IS NULL
      AND (
            p_search IS NULL
            OR TRIM(p_search) = ''
            OR p.name LIKE CONCAT('%', TRIM(p_search), '%')
            OR p.description LIKE CONCAT('%', TRIM(p_search), '%')
            OR c.name LIKE CONCAT('%', TRIM(p_search), '%')
            OR b.name LIKE CONCAT('%', TRIM(p_search), '%')
      );
END$$

DROP PROCEDURE IF EXISTS `sp_product_list_inactive`$$
CREATE PROCEDURE sp_product_list_inactive(
    IN p_search VARCHAR(200),
    IN p_page INT,
    IN p_limit INT
)
BEGIN
    DECLARE v_offset INT DEFAULT 0;

    IF p_page IS NULL OR p_page < 1 THEN
        SET p_page = 1;
    END IF;

    IF p_limit NOT IN (10, 20, 50) THEN
        SET p_limit = 10;
    END IF;

    SET v_offset = (p_page - 1) * p_limit;

    SELECT
        p.id_product,
        p.name,
        p.description,
        p.cost,
        p.profit_margin,
        p.price,
        p.stock,
        NULL AS image,
        p.id_category,
        c.name AS category_name,
        p.id_brand,
        b.name AS brand_name,
        p.status,
        p.created_at,
        p.updated_at,
        p.deleted_at
    FROM products p
    INNER JOIN categories c ON c.id_category = p.id_category
    INNER JOIN brands b ON b.id_brand = p.id_brand
    WHERE p.status = 0
      AND (
            p_search IS NULL
            OR TRIM(p_search) = ''
            OR p.name LIKE CONCAT('%', TRIM(p_search), '%')
            OR p.description LIKE CONCAT('%', TRIM(p_search), '%')
            OR c.name LIKE CONCAT('%', TRIM(p_search), '%')
            OR b.name LIKE CONCAT('%', TRIM(p_search), '%')
      )
    ORDER BY p.deleted_at DESC, p.id_product DESC
    LIMIT v_offset, p_limit;
END$$

DROP PROCEDURE IF EXISTS `sp_product_count_inactive`$$
CREATE PROCEDURE sp_product_count_inactive(
    IN p_search VARCHAR(200)
)
BEGIN
    SELECT COUNT(*) AS total
    FROM products p
    INNER JOIN categories c ON c.id_category = p.id_category
    INNER JOIN brands b ON b.id_brand = p.id_brand
    WHERE p.status = 0
      AND (
            p_search IS NULL
            OR TRIM(p_search) = ''
            OR p.name LIKE CONCAT('%', TRIM(p_search), '%')
            OR p.description LIKE CONCAT('%', TRIM(p_search), '%')
            OR c.name LIKE CONCAT('%', TRIM(p_search), '%')
            OR b.name LIKE CONCAT('%', TRIM(p_search), '%')
      );
END$$

DROP PROCEDURE IF EXISTS `sp_product_delete_logical`$$
CREATE PROCEDURE sp_product_delete_logical(
    IN p_id_product INT
)
BEGIN
    UPDATE products
    SET status = 0,
        updated_at = NOW(),
        deleted_at = NOW()
    WHERE id_product = p_id_product
      AND status = 1;
END$$

DROP PROCEDURE IF EXISTS `sp_product_restore`$$
CREATE PROCEDURE sp_product_restore(
    IN p_id_product INT
)
BEGIN
    DECLARE v_name VARCHAR(150);
    DECLARE v_id_category INT;
    DECLARE v_id_brand INT;

    SELECT name, id_category, id_brand
    INTO v_name, v_id_category, v_id_brand
    FROM products
    WHERE id_product = p_id_product
    LIMIT 1;

    IF v_name IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El producto no existe';
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM categories
        WHERE id_category = v_id_category
          AND status = 1
          AND deleted_at IS NULL
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'No se puede restaurar porque la categoría está inactiva';
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM brands
        WHERE id_brand = v_id_brand
          AND status = 1
          AND deleted_at IS NULL
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'No se puede restaurar porque la marca está inactiva';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM products
        WHERE LOWER(name) = LOWER(v_name)
          AND id_product <> p_id_product
          AND status = 1
          AND deleted_at IS NULL
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'No se puede restaurar porque ya existe un producto activo con el mismo nombre';
    END IF;

    UPDATE products
    SET status = 1,
        updated_at = NOW(),
        deleted_at = NULL
    WHERE id_product = p_id_product;
END$$

DROP PROCEDURE IF EXISTS `sp_product_delete_physical`$$
CREATE PROCEDURE sp_product_delete_physical(
    IN p_id_product INT
)
BEGIN
    DELETE FROM products
    WHERE id_product = p_id_product;
END$$

DROP PROCEDURE IF EXISTS `sp_product_category_options`$$
CREATE PROCEDURE sp_product_category_options()
BEGIN
    SELECT id_category AS id, name
    FROM categories
    WHERE status = 1
      AND deleted_at IS NULL
    ORDER BY name ASC;
END$$

DROP PROCEDURE IF EXISTS `sp_product_brand_options`$$
CREATE PROCEDURE sp_product_brand_options()
BEGIN
    SELECT id_brand AS id, name
    FROM brands
    WHERE status = 1
      AND deleted_at IS NULL
    ORDER BY name ASC;
END$$


-- ------------------------------------------------------------
-- Proveedores
-- ------------------------------------------------------------

DROP PROCEDURE IF EXISTS `sp_supplier_create`$$
CREATE PROCEDURE sp_supplier_create(
    IN p_business_name VARCHAR(150),
    IN p_trade_name VARCHAR(150),
    IN p_id_document_type INT,
    IN p_document_number VARCHAR(30),
    IN p_phone VARCHAR(20),
    IN p_email VARCHAR(150),
    IN p_address TEXT,
    IN p_id_region INT,
    IN p_id_province INT,
    IN p_id_district INT
)
BEGIN
    IF p_business_name IS NULL OR TRIM(p_business_name) = '' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'La razón social es obligatoria';
    END IF;

    IF p_id_document_type IS NULL OR NOT EXISTS (
        SELECT 1
        FROM document_types
        WHERE id_document_type = p_id_document_type
          AND status = 1
          AND deleted_at IS NULL
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Seleccione un tipo de documento activo válido';
    END IF;

    IF p_document_number IS NULL OR TRIM(p_document_number) = '' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El número de documento es obligatorio';
    END IF;

    IF p_id_region IS NOT NULL AND NOT EXISTS (
        SELECT 1
        FROM regions
        WHERE id_region = p_id_region
          AND status = 1
          AND deleted_at IS NULL
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Seleccione una región activa válida';
    END IF;

    IF p_id_province IS NOT NULL AND NOT EXISTS (
        SELECT 1
        FROM provinces
        WHERE id_province = p_id_province
          AND id_region = p_id_region
          AND status = 1
          AND deleted_at IS NULL
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Seleccione una provincia activa relacionada a la región';
    END IF;

    IF p_id_district IS NOT NULL AND NOT EXISTS (
        SELECT 1
        FROM districts
        WHERE id_district = p_id_district
          AND id_province = p_id_province
          AND status = 1
          AND deleted_at IS NULL
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Seleccione un distrito activo relacionado a la provincia';
    END IF;

    INSERT INTO suppliers (
        business_name,
        trade_name,
        id_document_type,
        document_number,
        phone,
        email,
        address,
        id_region,
        id_province,
        id_district,
        status,
        created_at
    )
    VALUES (
        TRIM(p_business_name),
        NULLIF(TRIM(p_trade_name), ''),
        p_id_document_type,
        TRIM(p_document_number),
        NULLIF(TRIM(p_phone), ''),
        NULLIF(TRIM(p_email), ''),
        p_address,
        p_id_region,
        p_id_province,
        p_id_district,
        1,
        NOW()
    );
END$$

DROP PROCEDURE IF EXISTS `sp_supplier_update`$$
CREATE PROCEDURE sp_supplier_update(
    IN p_id_supplier INT,
    IN p_business_name VARCHAR(150),
    IN p_trade_name VARCHAR(150),
    IN p_id_document_type INT,
    IN p_document_number VARCHAR(30),
    IN p_phone VARCHAR(20),
    IN p_email VARCHAR(150),
    IN p_address TEXT,
    IN p_id_region INT,
    IN p_id_province INT,
    IN p_id_district INT
)
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM suppliers
        WHERE id_supplier = p_id_supplier
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El proveedor no existe';
    END IF;

    IF p_business_name IS NULL OR TRIM(p_business_name) = '' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'La razón social es obligatoria';
    END IF;

    IF p_id_document_type IS NULL OR NOT EXISTS (
        SELECT 1
        FROM document_types
        WHERE id_document_type = p_id_document_type
          AND status = 1
          AND deleted_at IS NULL
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Seleccione un tipo de documento activo válido';
    END IF;

    IF p_document_number IS NULL OR TRIM(p_document_number) = '' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El número de documento es obligatorio';
    END IF;

    IF p_id_region IS NOT NULL AND NOT EXISTS (
        SELECT 1
        FROM regions
        WHERE id_region = p_id_region
          AND status = 1
          AND deleted_at IS NULL
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Seleccione una región activa válida';
    END IF;

    IF p_id_province IS NOT NULL AND NOT EXISTS (
        SELECT 1
        FROM provinces
        WHERE id_province = p_id_province
          AND id_region = p_id_region
          AND status = 1
          AND deleted_at IS NULL
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Seleccione una provincia activa relacionada a la región';
    END IF;

    IF p_id_district IS NOT NULL AND NOT EXISTS (
        SELECT 1
        FROM districts
        WHERE id_district = p_id_district
          AND id_province = p_id_province
          AND status = 1
          AND deleted_at IS NULL
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Seleccione un distrito activo relacionado a la provincia';
    END IF;

    UPDATE suppliers
    SET business_name = TRIM(p_business_name),
        trade_name = NULLIF(TRIM(p_trade_name), ''),
        id_document_type = p_id_document_type,
        document_number = TRIM(p_document_number),
        phone = NULLIF(TRIM(p_phone), ''),
        email = NULLIF(TRIM(p_email), ''),
        address = p_address,
        id_region = p_id_region,
        id_province = p_id_province,
        id_district = p_id_district,
        updated_at = NOW()
    WHERE id_supplier = p_id_supplier;
END$$

DROP PROCEDURE IF EXISTS `sp_supplier_find_by_id`$$
CREATE PROCEDURE sp_supplier_find_by_id(
    IN p_id_supplier INT
)
BEGIN
    SELECT
        s.id_supplier,
        s.business_name,
        s.trade_name,

        s.id_document_type,
        dt.name AS document_type_name,
        s.document_number,

        s.phone,
        s.email,
        s.address,

        s.id_region,
        r.name AS region_name,

        s.id_province,
        p.name AS province_name,

        s.id_district,
        d.name AS district_name,

        s.status,
        s.created_at,
        s.updated_at,
        s.deleted_at
    FROM suppliers s
    INNER JOIN document_types dt ON dt.id_document_type = s.id_document_type
    LEFT JOIN regions r ON r.id_region = s.id_region
    LEFT JOIN provinces p ON p.id_province = s.id_province
    LEFT JOIN districts d ON d.id_district = s.id_district
    WHERE s.id_supplier = p_id_supplier
    LIMIT 1;
END$$

DROP PROCEDURE IF EXISTS `sp_supplier_list_active`$$
CREATE PROCEDURE sp_supplier_list_active(
    IN p_search VARCHAR(200),
    IN p_page INT,
    IN p_limit INT
)
BEGIN
    DECLARE v_offset INT DEFAULT 0;

    IF p_page IS NULL OR p_page < 1 THEN
        SET p_page = 1;
    END IF;

    IF p_limit NOT IN (10, 20, 50) THEN
        SET p_limit = 10;
    END IF;

    SET v_offset = (p_page - 1) * p_limit;

    SELECT
        s.id_supplier,
        s.business_name,
        s.trade_name,

        s.id_document_type,
        dt.name AS document_type_name,
        s.document_number,

        s.phone,
        s.email,
        s.address,

        s.id_region,
        r.name AS region_name,

        s.id_province,
        p.name AS province_name,

        s.id_district,
        d.name AS district_name,

        s.status,
        s.created_at,
        s.updated_at,
        s.deleted_at
    FROM suppliers s
    INNER JOIN document_types dt ON dt.id_document_type = s.id_document_type
    LEFT JOIN regions r ON r.id_region = s.id_region
    LEFT JOIN provinces p ON p.id_province = s.id_province
    LEFT JOIN districts d ON d.id_district = s.id_district
    WHERE s.status = 1
      AND s.deleted_at IS NULL
      AND (
            p_search IS NULL
            OR TRIM(p_search) = ''
            OR s.business_name LIKE CONCAT('%', TRIM(p_search), '%')
            OR s.trade_name LIKE CONCAT('%', TRIM(p_search), '%')
            OR s.document_number LIKE CONCAT('%', TRIM(p_search), '%')
            OR s.phone LIKE CONCAT('%', TRIM(p_search), '%')
            OR s.email LIKE CONCAT('%', TRIM(p_search), '%')
      )
    ORDER BY s.id_supplier DESC
    LIMIT v_offset, p_limit;
END$$

DROP PROCEDURE IF EXISTS `sp_supplier_count_active`$$
CREATE PROCEDURE sp_supplier_count_active(
    IN p_search VARCHAR(200)
)
BEGIN
    SELECT COUNT(*) AS total
    FROM suppliers s
    WHERE s.status = 1
      AND s.deleted_at IS NULL
      AND (
            p_search IS NULL
            OR TRIM(p_search) = ''
            OR s.business_name LIKE CONCAT('%', TRIM(p_search), '%')
            OR s.trade_name LIKE CONCAT('%', TRIM(p_search), '%')
            OR s.document_number LIKE CONCAT('%', TRIM(p_search), '%')
            OR s.phone LIKE CONCAT('%', TRIM(p_search), '%')
            OR s.email LIKE CONCAT('%', TRIM(p_search), '%')
      );
END$$

DROP PROCEDURE IF EXISTS `sp_supplier_list_inactive`$$
CREATE PROCEDURE sp_supplier_list_inactive(
    IN p_search VARCHAR(200),
    IN p_page INT,
    IN p_limit INT
)
BEGIN
    DECLARE v_offset INT DEFAULT 0;

    IF p_page IS NULL OR p_page < 1 THEN
        SET p_page = 1;
    END IF;

    IF p_limit NOT IN (10, 20, 50) THEN
        SET p_limit = 10;
    END IF;

    SET v_offset = (p_page - 1) * p_limit;

    SELECT
        s.id_supplier,
        s.business_name,
        s.trade_name,

        s.id_document_type,
        dt.name AS document_type_name,
        s.document_number,

        s.phone,
        s.email,
        s.address,

        s.id_region,
        r.name AS region_name,

        s.id_province,
        p.name AS province_name,

        s.id_district,
        d.name AS district_name,

        s.status,
        s.created_at,
        s.updated_at,
        s.deleted_at
    FROM suppliers s
    INNER JOIN document_types dt ON dt.id_document_type = s.id_document_type
    LEFT JOIN regions r ON r.id_region = s.id_region
    LEFT JOIN provinces p ON p.id_province = s.id_province
    LEFT JOIN districts d ON d.id_district = s.id_district
    WHERE s.status = 0
      AND (
            p_search IS NULL
            OR TRIM(p_search) = ''
            OR s.business_name LIKE CONCAT('%', TRIM(p_search), '%')
            OR s.trade_name LIKE CONCAT('%', TRIM(p_search), '%')
            OR s.document_number LIKE CONCAT('%', TRIM(p_search), '%')
            OR s.phone LIKE CONCAT('%', TRIM(p_search), '%')
            OR s.email LIKE CONCAT('%', TRIM(p_search), '%')
      )
    ORDER BY s.deleted_at DESC, s.id_supplier DESC
    LIMIT v_offset, p_limit;
END$$

DROP PROCEDURE IF EXISTS `sp_supplier_count_inactive`$$
CREATE PROCEDURE sp_supplier_count_inactive(
    IN p_search VARCHAR(200)
)
BEGIN
    SELECT COUNT(*) AS total
    FROM suppliers s
    WHERE s.status = 0
      AND (
            p_search IS NULL
            OR TRIM(p_search) = ''
            OR s.business_name LIKE CONCAT('%', TRIM(p_search), '%')
            OR s.trade_name LIKE CONCAT('%', TRIM(p_search), '%')
            OR s.document_number LIKE CONCAT('%', TRIM(p_search), '%')
            OR s.phone LIKE CONCAT('%', TRIM(p_search), '%')
            OR s.email LIKE CONCAT('%', TRIM(p_search), '%')
      );
END$$

DROP PROCEDURE IF EXISTS `sp_supplier_delete_logical`$$
CREATE PROCEDURE sp_supplier_delete_logical(
    IN p_id_supplier INT
)
BEGIN
    UPDATE suppliers
    SET status = 0,
        updated_at = NOW(),
        deleted_at = NOW()
    WHERE id_supplier = p_id_supplier
      AND status = 1;
END$$

DROP PROCEDURE IF EXISTS `sp_supplier_restore`$$
CREATE PROCEDURE sp_supplier_restore(
    IN p_id_supplier INT
)
BEGIN
    DECLARE v_id_document_type INT;
    DECLARE v_id_region INT;
    DECLARE v_id_province INT;
    DECLARE v_id_district INT;

    SELECT id_document_type, id_region, id_province, id_district
    INTO v_id_document_type, v_id_region, v_id_province, v_id_district
    FROM suppliers
    WHERE id_supplier = p_id_supplier
    LIMIT 1;

    IF v_id_document_type IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El proveedor no existe';
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM document_types
        WHERE id_document_type = v_id_document_type
          AND status = 1
          AND deleted_at IS NULL
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'No se puede restaurar porque el tipo de documento está inactivo';
    END IF;

    IF v_id_region IS NOT NULL AND NOT EXISTS (
        SELECT 1
        FROM regions
        WHERE id_region = v_id_region
          AND status = 1
          AND deleted_at IS NULL
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'No se puede restaurar porque la región está inactiva';
    END IF;

    IF v_id_province IS NOT NULL AND NOT EXISTS (
        SELECT 1
        FROM provinces
        WHERE id_province = v_id_province
          AND status = 1
          AND deleted_at IS NULL
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'No se puede restaurar porque la provincia está inactiva';
    END IF;

    IF v_id_district IS NOT NULL AND NOT EXISTS (
        SELECT 1
        FROM districts
        WHERE id_district = v_id_district
          AND status = 1
          AND deleted_at IS NULL
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'No se puede restaurar porque el distrito está inactivo';
    END IF;

    UPDATE suppliers
    SET status = 1,
        updated_at = NOW(),
        deleted_at = NULL
    WHERE id_supplier = p_id_supplier;
END$$

DROP PROCEDURE IF EXISTS `sp_supplier_delete_physical`$$
CREATE PROCEDURE sp_supplier_delete_physical(
    IN p_id_supplier INT
)
BEGIN
    DELETE FROM suppliers
    WHERE id_supplier = p_id_supplier;
END$$

DROP PROCEDURE IF EXISTS `sp_supplier_document_type_options`$$
CREATE PROCEDURE sp_supplier_document_type_options()
BEGIN
    SELECT id_document_type AS id, name
    FROM document_types
    WHERE status = 1
      AND deleted_at IS NULL
    ORDER BY name ASC;
END$$

DROP PROCEDURE IF EXISTS `sp_supplier_region_options`$$
CREATE PROCEDURE sp_supplier_region_options()
BEGIN
    SELECT id_region AS id, name
    FROM regions
    WHERE status = 1
      AND deleted_at IS NULL
    ORDER BY name ASC;
END$$

DROP PROCEDURE IF EXISTS `sp_supplier_province_options`$$
CREATE PROCEDURE sp_supplier_province_options(
    IN p_id_region INT
)
BEGIN
    SELECT id_province AS id, name
    FROM provinces
    WHERE id_region = p_id_region
      AND status = 1
      AND deleted_at IS NULL
    ORDER BY name ASC;
END$$

DROP PROCEDURE IF EXISTS `sp_supplier_district_options`$$
CREATE PROCEDURE sp_supplier_district_options(
    IN p_id_province INT
)
BEGIN
    SELECT id_district AS id, name
    FROM districts
    WHERE id_province = p_id_province
      AND status = 1
      AND deleted_at IS NULL
    ORDER BY name ASC;
END$$


-- ------------------------------------------------------------
-- Metodos de pago
-- ------------------------------------------------------------

DROP PROCEDURE IF EXISTS `sp_payment_method_create`$$
CREATE PROCEDURE sp_payment_method_create(
    IN p_name VARCHAR(50),
    IN p_description TEXT
)
BEGIN
    IF p_name IS NULL OR TRIM(p_name) = '' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El nombre del método de pago es obligatorio';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM payment_methods
        WHERE LOWER(name) = LOWER(TRIM(p_name))
          AND status = 1
          AND deleted_at IS NULL
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Ya existe un método de pago activo con ese nombre';
    END IF;

    INSERT INTO payment_methods (
        name,
        description,
        status,
        created_at,
        updated_at,
        deleted_at
    )
    VALUES (
        TRIM(p_name),
        NULLIF(TRIM(p_description), ''),
        1,
        NOW(),
        NULL,
        NULL
    );
END$$

DROP PROCEDURE IF EXISTS `sp_payment_method_update`$$
CREATE PROCEDURE sp_payment_method_update(
    IN p_id_payment_method INT,
    IN p_name VARCHAR(50),
    IN p_description TEXT
)
BEGIN
    IF p_name IS NULL OR TRIM(p_name) = '' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El nombre del método de pago es obligatorio';
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM payment_methods
        WHERE id_payment_method = p_id_payment_method
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El método de pago no existe';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM payment_methods
        WHERE LOWER(name) = LOWER(TRIM(p_name))
          AND id_payment_method <> p_id_payment_method
          AND status = 1
          AND deleted_at IS NULL
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Ya existe otro método de pago activo con ese nombre';
    END IF;

    UPDATE payment_methods
    SET name = TRIM(p_name),
        description = NULLIF(TRIM(p_description), ''),
        updated_at = NOW()
    WHERE id_payment_method = p_id_payment_method;
END$$

DROP PROCEDURE IF EXISTS `sp_payment_method_find_by_id`$$
CREATE PROCEDURE sp_payment_method_find_by_id(
    IN p_id_payment_method INT
)
BEGIN
    SELECT
        id_payment_method,
        name,
        description,
        status,
        created_at,
        updated_at,
        deleted_at
    FROM payment_methods
    WHERE id_payment_method = p_id_payment_method
    LIMIT 1;
END$$

DROP PROCEDURE IF EXISTS `sp_payment_method_list_active`$$
CREATE PROCEDURE sp_payment_method_list_active(
    IN p_search VARCHAR(150),
    IN p_page INT,
    IN p_limit INT
)
BEGIN
    DECLARE v_offset INT DEFAULT 0;

    IF p_page IS NULL OR p_page < 1 THEN
        SET p_page = 1;
    END IF;

    IF p_limit NOT IN (10, 20, 50) THEN
        SET p_limit = 10;
    END IF;

    SET v_offset = (p_page - 1) * p_limit;

    SELECT
        id_payment_method,
        name,
        description,
        status,
        created_at,
        updated_at,
        deleted_at
    FROM payment_methods
    WHERE status = 1
      AND deleted_at IS NULL
      AND (
            p_search IS NULL
            OR TRIM(p_search) = ''
            OR name LIKE CONCAT('%', TRIM(p_search), '%')
            OR description LIKE CONCAT('%', TRIM(p_search), '%')
      )
    ORDER BY id_payment_method DESC
    LIMIT v_offset, p_limit;
END$$

DROP PROCEDURE IF EXISTS `sp_payment_method_count_active`$$
CREATE PROCEDURE sp_payment_method_count_active(
    IN p_search VARCHAR(150)
)
BEGIN
    SELECT COUNT(*) AS total
    FROM payment_methods
    WHERE status = 1
      AND deleted_at IS NULL
      AND (
            p_search IS NULL
            OR TRIM(p_search) = ''
            OR name LIKE CONCAT('%', TRIM(p_search), '%')
            OR description LIKE CONCAT('%', TRIM(p_search), '%')
      );
END$$

DROP PROCEDURE IF EXISTS `sp_payment_method_list_inactive`$$
CREATE PROCEDURE sp_payment_method_list_inactive(
    IN p_search VARCHAR(150),
    IN p_page INT,
    IN p_limit INT
)
BEGIN
    DECLARE v_offset INT DEFAULT 0;

    IF p_page IS NULL OR p_page < 1 THEN
        SET p_page = 1;
    END IF;

    IF p_limit NOT IN (10, 20, 50) THEN
        SET p_limit = 10;
    END IF;

    SET v_offset = (p_page - 1) * p_limit;

    SELECT
        id_payment_method,
        name,
        description,
        status,
        created_at,
        updated_at,
        deleted_at
    FROM payment_methods
    WHERE status = 0
      AND (
            p_search IS NULL
            OR TRIM(p_search) = ''
            OR name LIKE CONCAT('%', TRIM(p_search), '%')
            OR description LIKE CONCAT('%', TRIM(p_search), '%')
      )
    ORDER BY deleted_at DESC, id_payment_method DESC
    LIMIT v_offset, p_limit;
END$$

DROP PROCEDURE IF EXISTS `sp_payment_method_count_inactive`$$
CREATE PROCEDURE sp_payment_method_count_inactive(
    IN p_search VARCHAR(150)
)
BEGIN
    SELECT COUNT(*) AS total
    FROM payment_methods
    WHERE status = 0
      AND (
            p_search IS NULL
            OR TRIM(p_search) = ''
            OR name LIKE CONCAT('%', TRIM(p_search), '%')
            OR description LIKE CONCAT('%', TRIM(p_search), '%')
      );
END$$

DROP PROCEDURE IF EXISTS `sp_payment_method_delete_logical`$$
CREATE PROCEDURE sp_payment_method_delete_logical(
    IN p_id_payment_method INT
)
BEGIN
    UPDATE payment_methods
    SET status = 0,
        updated_at = NOW(),
        deleted_at = NOW()
    WHERE id_payment_method = p_id_payment_method
      AND status = 1;
END$$

DROP PROCEDURE IF EXISTS `sp_payment_method_restore`$$
CREATE PROCEDURE sp_payment_method_restore(
    IN p_id_payment_method INT
)
BEGIN
    DECLARE v_name VARCHAR(50);

    SELECT name
    INTO v_name
    FROM payment_methods
    WHERE id_payment_method = p_id_payment_method
    LIMIT 1;

    IF v_name IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El método de pago no existe';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM payment_methods
        WHERE LOWER(name) = LOWER(v_name)
          AND id_payment_method <> p_id_payment_method
          AND status = 1
          AND deleted_at IS NULL
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'No se puede restaurar porque ya existe un método de pago activo con el mismo nombre';
    END IF;

    UPDATE payment_methods
    SET status = 1,
        updated_at = NOW(),
        deleted_at = NULL
    WHERE id_payment_method = p_id_payment_method;
END$$

DROP PROCEDURE IF EXISTS `sp_payment_method_delete_physical`$$
CREATE PROCEDURE sp_payment_method_delete_physical(
    IN p_id_payment_method INT
)
BEGIN
    DELETE FROM payment_methods
    WHERE id_payment_method = p_id_payment_method;
END$$


-- ------------------------------------------------------------
-- Ventas
-- ------------------------------------------------------------

DROP PROCEDURE IF EXISTS `sp_sale_product_catalog`$$
CREATE PROCEDURE sp_sale_product_catalog(
    IN p_search VARCHAR(200),
    IN p_id_category INT,
    IN p_id_brand INT
)
BEGIN
    SELECT
        p.id_product,
        p.name,
        p.description,
        p.price,
        p.stock,
        p.image,
        p.id_category,
        c.name AS category_name,
        p.id_brand,
        b.name AS brand_name
    FROM products p
    INNER JOIN categories c ON c.id_category = p.id_category
    INNER JOIN brands b ON b.id_brand = p.id_brand
    WHERE p.status = 1
      AND p.deleted_at IS NULL
      AND p.stock > 0
      AND (p_id_category IS NULL OR p_id_category = 0 OR p.id_category = p_id_category)
      AND (p_id_brand IS NULL OR p_id_brand = 0 OR p.id_brand = p_id_brand)
      AND (
            p_search IS NULL
            OR TRIM(p_search) = ''
            OR p.name LIKE CONCAT('%', TRIM(p_search), '%')
            OR p.description LIKE CONCAT('%', TRIM(p_search), '%')
            OR c.name LIKE CONCAT('%', TRIM(p_search), '%')
            OR b.name LIKE CONCAT('%', TRIM(p_search), '%')
      )
    ORDER BY p.id_product DESC;
END$$

DROP PROCEDURE IF EXISTS `sp_sale_category_options`$$
CREATE PROCEDURE sp_sale_category_options()
BEGIN
    SELECT id_category AS id, name
    FROM categories
    WHERE status = 1
      AND deleted_at IS NULL
    ORDER BY name ASC;
END$$

DROP PROCEDURE IF EXISTS `sp_sale_brand_options`$$
CREATE PROCEDURE sp_sale_brand_options()
BEGIN
    SELECT id_brand AS id, name
    FROM brands
    WHERE status = 1
      AND deleted_at IS NULL
    ORDER BY name ASC;
END$$

DROP PROCEDURE IF EXISTS `sp_sale_payment_method_options`$$
CREATE PROCEDURE sp_sale_payment_method_options()
BEGIN
    SELECT id_payment_method AS id, name
    FROM payment_methods
    WHERE status = 1
      AND deleted_at IS NULL
    ORDER BY name ASC;
END$$

DROP PROCEDURE IF EXISTS `sp_sale_document_type_options`$$
CREATE PROCEDURE sp_sale_document_type_options()
BEGIN
    SELECT id_document_type AS id, name
    FROM document_types
    WHERE status = 1
      AND deleted_at IS NULL
    ORDER BY name ASC;
END$$

DROP PROCEDURE IF EXISTS `sp_sale_user_options`$$
CREATE PROCEDURE sp_sale_user_options()
BEGIN
    SELECT id_user AS id, user_name AS name
    FROM users
    WHERE status = 1
    ORDER BY user_name ASC;
END$$

DROP PROCEDURE IF EXISTS `sp_sale_create`$$
CREATE PROCEDURE sp_sale_create(
    IN p_id_user INT,
    IN p_id_payment_method INT,
    IN p_document_kind VARCHAR(20),
    IN p_customer_document_type_id INT,
    IN p_customer_document_number VARCHAR(20),
    IN p_paid_amount DECIMAL(10,2),
    IN p_details_json JSON
)
BEGIN
    DECLARE v_id_sale INT;
    DECLARE v_document_kind VARCHAR(20);
    DECLARE v_document_label VARCHAR(100);
    DECLARE v_series VARCHAR(10);
    DECLARE v_number INT;
    DECLARE v_code VARCHAR(30);

    DECLARE v_id_client INT DEFAULT NULL;
    DECLARE v_customer_name VARCHAR(150) DEFAULT NULL;
    DECLARE v_customer_document_type_id INT DEFAULT NULL;
    DECLARE v_customer_document_number VARCHAR(20) DEFAULT NULL;

    DECLARE v_total DECIMAL(10,2) DEFAULT 0;
    DECLARE v_subtotal DECIMAL(10,2) DEFAULT 0;
    DECLARE v_igv DECIMAL(10,2) DEFAULT 0;
    DECLARE v_discount DECIMAL(10,2) DEFAULT 0;
    DECLARE v_change DECIMAL(10,2) DEFAULT 0;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    START TRANSACTION;

    IF NOT EXISTS (
        SELECT 1
        FROM users
        WHERE id_user = p_id_user
          AND status = 1
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Usuario no valido';
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM payment_methods
        WHERE id_payment_method = p_id_payment_method
          AND status = 1
          AND deleted_at IS NULL
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Metodo de pago no valido';
    END IF;

    IF p_details_json IS NULL OR JSON_LENGTH(p_details_json) = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Agregue productos al carrito';
    END IF;

    SET v_document_kind = UPPER(TRIM(IFNULL(p_document_kind, 'TICKET')));

    IF v_document_kind = 'BOLETA' THEN
        SET v_series = 'B001';
        SET v_document_label = 'Boleta de venta';
    ELSEIF v_document_kind = 'FACTURA' THEN
        SET v_series = 'F001';
        SET v_document_label = 'Factura de venta';
    ELSE
        SET v_document_kind = 'TICKET';
        SET v_series = 'T001';
        SET v_document_label = 'Ticket de venta';
    END IF;

    IF v_document_kind IN ('BOLETA', 'FACTURA') THEN
        IF p_customer_document_type_id IS NULL OR NOT EXISTS (
            SELECT 1
            FROM document_types
            WHERE id_document_type = p_customer_document_type_id
              AND status = 1
              AND deleted_at IS NULL
        ) THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Seleccione el tipo de documento del cliente';
        END IF;

        IF p_customer_document_number IS NULL OR TRIM(p_customer_document_number) = '' THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Ingrese el numero de documento del cliente';
        END IF;

        SELECT
            c.id_client,
            LEFT(TRIM(CONCAT_WS(' ', c.name, c.last_name_paternal, c.last_name_maternal)), 150),
            c.id_document_type,
            LEFT(TRIM(c.document_number), 20)
        INTO
            v_id_client,
            v_customer_name,
            v_customer_document_type_id,
            v_customer_document_number
        FROM clients c
        WHERE c.status = 1
          AND c.deleted_at IS NULL
          AND c.id_document_type = p_customer_document_type_id
          AND c.document_number = TRIM(p_customer_document_number)
        LIMIT 1;

        IF v_id_client IS NULL THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'No existe un cliente activo registrado con ese documento';
        END IF;
    END IF;

    SELECT IFNULL(MAX(voucher_number), 0) + 1
    INTO v_number
    FROM sales
    WHERE voucher_series = v_series;

    SET v_code = CONCAT(v_series, '-', LPAD(v_number, 8, '0'));

    CREATE TEMPORARY TABLE IF NOT EXISTS tmp_sale_details (
        id_product INT,
        quantity INT,
        discount_type VARCHAR(20),
        discount_value DECIMAL(10,2),
        product_price DECIMAL(10,2),
        product_stock INT,
        subtotal_before_discount DECIMAL(10,2),
        discount_amount DECIMAL(10,2),
        line_total DECIMAL(10,2),
        line_subtotal DECIMAL(10,2),
        line_igv DECIMAL(10,2),
        unit_price DECIMAL(10,2)
    );

    DELETE FROM tmp_sale_details;

    INSERT INTO tmp_sale_details (
        id_product,
        quantity,
        discount_type,
        discount_value
    )
    SELECT
        jt.id_product,
        jt.quantity,
        UPPER(IFNULL(jt.discount_type, 'NONE')),
        IFNULL(jt.discount_value, 0)
    FROM JSON_TABLE(
        p_details_json,
        '$[*]'
        COLUMNS (
            id_product INT PATH '$.id_product',
            quantity INT PATH '$.quantity',
            discount_type VARCHAR(20) PATH '$.discount_type',
            discount_value DECIMAL(10,2) PATH '$.discount_value'
        )
    ) jt;

    IF EXISTS (
        SELECT 1
        FROM tmp_sale_details
        WHERE id_product IS NULL
           OR quantity IS NULL
           OR quantity <= 0
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El carrito tiene productos invalidos';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM tmp_sale_details t
        LEFT JOIN products p ON p.id_product = t.id_product
        WHERE p.id_product IS NULL
           OR p.status <> 1
           OR p.deleted_at IS NOT NULL
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Uno o mas productos no estan disponibles';
    END IF;

    UPDATE tmp_sale_details t
    INNER JOIN products p ON p.id_product = t.id_product
    SET
        t.product_price = p.price,
        t.product_stock = p.stock;

    IF EXISTS (
        SELECT 1
        FROM tmp_sale_details
        WHERE quantity > product_stock
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Stock insuficiente en uno o mas productos';
    END IF;

    UPDATE tmp_sale_details
    SET subtotal_before_discount = ROUND(product_price * quantity, 2);

    UPDATE tmp_sale_details
    SET discount_amount = CASE
        WHEN discount_type = 'PERCENT' THEN ROUND(subtotal_before_discount * discount_value / 100, 2)
        WHEN discount_type = 'FIXED' THEN discount_value
        ELSE 0
    END;

    UPDATE tmp_sale_details
    SET discount_amount = CASE
        WHEN discount_amount < 0 THEN 0
        WHEN discount_amount > subtotal_before_discount THEN subtotal_before_discount
        ELSE discount_amount
    END;

    UPDATE tmp_sale_details
    SET
        line_total = ROUND(subtotal_before_discount - discount_amount, 2),
        unit_price = ROUND((subtotal_before_discount - discount_amount) / quantity, 2);

    UPDATE tmp_sale_details
    SET
        line_subtotal = ROUND(line_total / 1.18, 2),
        line_igv = ROUND(line_total - ROUND(line_total / 1.18, 2), 2);

    SELECT
        ROUND(SUM(line_total), 2),
        ROUND(SUM(line_subtotal), 2),
        ROUND(SUM(line_igv), 2),
        ROUND(SUM(discount_amount), 2)
    INTO
        v_total,
        v_subtotal,
        v_igv,
        v_discount
    FROM tmp_sale_details;

    IF p_paid_amount IS NULL OR p_paid_amount < v_total THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El monto pagado no puede ser menor al total';
    END IF;

    SET v_change = ROUND(p_paid_amount - v_total, 2);

    INSERT INTO sales (
        id_user,
        id_payment_method,
        sale_date,
        document_kind,
        document_label,
        voucher_series,
        voucher_number,
        voucher_code,
        id_client,
        customer_name,
        customer_document_type_id,
        customer_document_number,
        subtotal,
        discount_amount,
        igv_amount,
        total,
        paid_amount,
        change_amount,
        status,
        created_at,
        updated_at,
        deleted_at
    )
    VALUES (
        p_id_user,
        p_id_payment_method,
        NOW(),
        v_document_kind,
        v_document_label,
        v_series,
        v_number,
        v_code,
        CASE
            WHEN v_document_kind = 'TICKET' THEN NULL
            ELSE v_id_client
        END,
        CASE
            WHEN v_document_kind = 'TICKET' THEN NULL
            ELSE v_customer_name
        END,
        CASE
            WHEN v_document_kind = 'TICKET' THEN NULL
            ELSE v_customer_document_type_id
        END,
        CASE
            WHEN v_document_kind = 'TICKET' THEN NULL
            ELSE v_customer_document_number
        END,
        v_subtotal,
        v_discount,
        v_igv,
        v_total,
        p_paid_amount,
        v_change,
        1,
        NOW(),
        NULL,
        NULL
    );

    SET v_id_sale = LAST_INSERT_ID();

    INSERT INTO sale_details (
        id_sale,
        id_product,
        quantity,
        original_unit_price,
        discount_type,
        discount_value,
        discount_amount,
        igv_amount,
        unit_price,
        subtotal_before_discount,
        subtotal
    )
    SELECT
        v_id_sale,
        id_product,
        quantity,
        product_price,
        discount_type,
        discount_value,
        discount_amount,
        line_igv,
        unit_price,
        subtotal_before_discount,
        line_total
    FROM tmp_sale_details;

    UPDATE products p
    INNER JOIN tmp_sale_details t ON t.id_product = p.id_product
    SET p.stock = p.stock - t.quantity,
        p.updated_at = NOW();

    INSERT INTO stock_movements (
        id_product,
        movement_type,
        quantity,
        description,
        reference,
        reference_id,
        movement_date
    )
    SELECT
        id_product,
        'SALIDA',
        quantity,
        'Salida por venta',
        'SALE',
        v_id_sale,
        NOW()
    FROM tmp_sale_details;

    COMMIT;

    SELECT
        v_id_sale AS id_sale,
        v_code AS voucher_code,
        v_total AS total,
        v_change AS change_amount;
END$$

DROP PROCEDURE IF EXISTS `sp_sale_history_list`$$
CREATE PROCEDURE sp_sale_history_list(
    IN p_search VARCHAR(200),
    IN p_id_payment_method INT,
    IN p_id_user INT,
    IN p_date_from DATE,
    IN p_date_to DATE,
    IN p_page INT,
    IN p_limit INT
)
BEGIN
    DECLARE v_offset INT DEFAULT 0;

    IF p_page IS NULL OR p_page < 1 THEN
        SET p_page = 1;
    END IF;

    IF p_limit NOT IN (10, 20, 50) THEN
        SET p_limit = 10;
    END IF;

    SET v_offset = (p_page - 1) * p_limit;

    SELECT
        s.id_sale,
        s.voucher_code,
        s.document_kind,
        s.document_label,
        s.sale_date,
        s.id_client,
        s.customer_name,
        s.customer_document_type_id,
        dt.name AS customer_document_type_name,
        s.customer_document_number,
        s.subtotal,
        s.discount_amount,
        s.igv_amount,
        s.total,
        s.paid_amount,
        s.change_amount,
        s.status,
        s.id_user,
        u.user_name,
        s.id_payment_method,
        pm.name AS payment_method_name
    FROM sales s
    INNER JOIN users u
        ON u.id_user = s.id_user
    INNER JOIN payment_methods pm
        ON pm.id_payment_method = s.id_payment_method
    LEFT JOIN document_types dt
        ON dt.id_document_type = s.customer_document_type_id
    WHERE s.status = 1
      AND s.deleted_at IS NULL
      AND (
            p_id_payment_method IS NULL
            OR p_id_payment_method = 0
            OR s.id_payment_method = p_id_payment_method
      )
      AND (
            p_id_user IS NULL
            OR p_id_user = 0
            OR s.id_user = p_id_user
      )
      AND (
            p_date_from IS NULL
            OR DATE(s.sale_date) >= p_date_from
      )
      AND (
            p_date_to IS NULL
            OR DATE(s.sale_date) <= p_date_to
      )
      AND (
            p_search IS NULL
            OR TRIM(p_search) = ''
            OR s.voucher_code LIKE CONCAT('%', TRIM(p_search), '%')
            OR u.user_name LIKE CONCAT('%', TRIM(p_search), '%')
            OR pm.name LIKE CONCAT('%', TRIM(p_search), '%')
            OR s.customer_name LIKE CONCAT('%', TRIM(p_search), '%')
            OR s.customer_document_number LIKE CONCAT('%', TRIM(p_search), '%')
            OR dt.name LIKE CONCAT('%', TRIM(p_search), '%')
            OR CAST(s.total AS CHAR) LIKE CONCAT('%', TRIM(p_search), '%')
      )
    ORDER BY s.id_sale DESC
    LIMIT v_offset, p_limit;
END$$

DROP PROCEDURE IF EXISTS `sp_sale_history_count`$$
CREATE PROCEDURE sp_sale_history_count(
    IN p_search VARCHAR(200),
    IN p_id_payment_method INT,
    IN p_id_user INT,
    IN p_date_from DATE,
    IN p_date_to DATE
)
BEGIN
    SELECT COUNT(*) AS total
    FROM sales s
    INNER JOIN users u ON u.id_user = s.id_user
    WHERE s.status = 1
      AND s.deleted_at IS NULL
      AND (p_id_payment_method IS NULL OR p_id_payment_method = 0 OR s.id_payment_method = p_id_payment_method)
      AND (p_id_user IS NULL OR p_id_user = 0 OR s.id_user = p_id_user)
      AND (p_date_from IS NULL OR DATE(s.sale_date) >= p_date_from)
      AND (p_date_to IS NULL OR DATE(s.sale_date) <= p_date_to)
      AND (
            p_search IS NULL
            OR TRIM(p_search) = ''
            OR s.voucher_code LIKE CONCAT('%', TRIM(p_search), '%')
            OR u.user_name LIKE CONCAT('%', TRIM(p_search), '%')
            OR CAST(s.total AS CHAR) LIKE CONCAT('%', TRIM(p_search), '%')
      );
END$$

DROP PROCEDURE IF EXISTS `sp_sale_history_stats`$$
CREATE PROCEDURE sp_sale_history_stats(
    IN p_search VARCHAR(200),
    IN p_id_payment_method INT,
    IN p_id_user INT,
    IN p_date_from DATE,
    IN p_date_to DATE
)
BEGIN
    SELECT
        IFNULL(SUM(s.total), 0) AS total_sales,
        IFNULL(AVG(s.total), 0) AS average_ticket,
        0 AS total_returns,
        IFNULL(SUM(CASE WHEN s.discount_amount > 0 THEN 1 ELSE 0 END), 0) AS discount_sales_count
    FROM sales s
    INNER JOIN users u ON u.id_user = s.id_user
    WHERE s.status = 1
      AND s.deleted_at IS NULL
      AND (p_id_payment_method IS NULL OR p_id_payment_method = 0 OR s.id_payment_method = p_id_payment_method)
      AND (p_id_user IS NULL OR p_id_user = 0 OR s.id_user = p_id_user)
      AND (p_date_from IS NULL OR DATE(s.sale_date) >= p_date_from)
      AND (p_date_to IS NULL OR DATE(s.sale_date) <= p_date_to)
      AND (
            p_search IS NULL
            OR TRIM(p_search) = ''
            OR s.voucher_code LIKE CONCAT('%', TRIM(p_search), '%')
            OR u.user_name LIKE CONCAT('%', TRIM(p_search), '%')
            OR CAST(s.total AS CHAR) LIKE CONCAT('%', TRIM(p_search), '%')
      );
END$$

DROP PROCEDURE IF EXISTS `sp_sale_ranking`$$
CREATE PROCEDURE sp_sale_ranking(
    IN p_search VARCHAR(200),
    IN p_id_payment_method INT,
    IN p_id_user INT,
    IN p_date_from DATE,
    IN p_date_to DATE
)
BEGIN
    SELECT
        s.id_user,
        u.user_name,
        ROUND(SUM(s.total), 2) AS total_sales
    FROM sales s
    INNER JOIN users u
        ON u.id_user = s.id_user
    INNER JOIN payment_methods pm
        ON pm.id_payment_method = s.id_payment_method
    LEFT JOIN document_types dt
        ON dt.id_document_type = s.customer_document_type_id
    WHERE s.status = 1
      AND s.deleted_at IS NULL
      AND (
            p_id_payment_method IS NULL
            OR p_id_payment_method = 0
            OR s.id_payment_method = p_id_payment_method
      )
      AND (
            p_id_user IS NULL
            OR p_id_user = 0
            OR s.id_user = p_id_user
      )
      AND (
            p_date_from IS NULL
            OR DATE(s.sale_date) >= p_date_from
      )
      AND (
            p_date_to IS NULL
            OR DATE(s.sale_date) <= p_date_to
      )
      AND (
            p_search IS NULL
            OR TRIM(p_search) = ''
            OR s.voucher_code LIKE CONCAT('%', TRIM(p_search), '%')
            OR u.user_name LIKE CONCAT('%', TRIM(p_search), '%')
            OR pm.name LIKE CONCAT('%', TRIM(p_search), '%')
            OR IFNULL(s.customer_name, '') LIKE CONCAT('%', TRIM(p_search), '%')
            OR IFNULL(s.customer_document_number, '') LIKE CONCAT('%', TRIM(p_search), '%')
            OR IFNULL(dt.name, '') LIKE CONCAT('%', TRIM(p_search), '%')
            OR CAST(s.total AS CHAR) LIKE CONCAT('%', TRIM(p_search), '%')
      )
    GROUP BY s.id_user, u.user_name
    ORDER BY total_sales DESC, u.user_name ASC
    LIMIT 3;
END$$

DROP PROCEDURE IF EXISTS `sp_sale_find_by_id`$$
CREATE PROCEDURE sp_sale_find_by_id(
    IN p_id_sale INT
)
BEGIN
    SELECT
        s.id_sale,
        s.id_user,
        u.user_name,
        s.id_payment_method,
        pm.name AS payment_method_name,
        s.sale_date,
        s.document_kind,
        s.document_label,
        s.voucher_series,
        s.voucher_number,
        s.voucher_code,
        s.id_client,
        s.customer_name,
        s.customer_document_type_id,
        dt.name AS customer_document_type_name,
        s.customer_document_number,
        s.subtotal,
        s.discount_amount,
        s.igv_amount,
        s.total,
        s.paid_amount,
        s.change_amount,
        s.status,
        s.created_at,
        s.updated_at,
        s.deleted_at
    FROM sales s
    INNER JOIN users u
        ON u.id_user = s.id_user
    INNER JOIN payment_methods pm
        ON pm.id_payment_method = s.id_payment_method
    LEFT JOIN document_types dt
        ON dt.id_document_type = s.customer_document_type_id
    WHERE s.id_sale = p_id_sale
    LIMIT 1;
END$$

DROP PROCEDURE IF EXISTS `sp_sale_detail_list`$$
CREATE PROCEDURE sp_sale_detail_list(
    IN p_id_sale INT
)
BEGIN
    SELECT
        sd.id_sale_detail,
        sd.id_sale,
        sd.id_product,
        p.name AS product_name,
        sd.quantity,
        sd.original_unit_price,
        sd.discount_type,
        sd.discount_value,
        sd.discount_amount,
        sd.igv_amount,
        sd.unit_price,
        sd.subtotal_before_discount,
        sd.subtotal
    FROM sale_details sd
    INNER JOIN products p ON p.id_product = sd.id_product
    WHERE sd.id_sale = p_id_sale
    ORDER BY sd.id_sale_detail ASC;
END$$


-- ------------------------------------------------------------
-- Inventario
-- ------------------------------------------------------------

DROP PROCEDURE IF EXISTS `sp_inventory_list`$$
CREATE PROCEDURE sp_inventory_list(
    IN p_search VARCHAR(200),
    IN p_stock_filter VARCHAR(20),
    IN p_page INT,
    IN p_limit INT
)
BEGIN
    DECLARE v_offset INT DEFAULT 0;

    IF p_page IS NULL OR p_page < 1 THEN
        SET p_page = 1;
    END IF;

    IF p_limit NOT IN (10, 20, 50) THEN
        SET p_limit = 10;
    END IF;

    SET v_offset = (p_page - 1) * p_limit;

    SELECT
        p.id_product,
        p.name,
        p.description,
        p.cost,
        p.price,
        p.stock,
        p.id_category,
        c.name AS category_name,
        p.id_brand,
        b.name AS brand_name,
        p.status,
        p.created_at,
        p.updated_at,
        p.deleted_at
    FROM products p
    INNER JOIN categories c ON c.id_category = p.id_category
    INNER JOIN brands b ON b.id_brand = p.id_brand
    WHERE p.status = 1
      AND p.deleted_at IS NULL
      AND (
            p_search IS NULL
            OR TRIM(p_search) = ''
            OR p.name LIKE CONCAT('%', TRIM(p_search), '%')
            OR c.name LIKE CONCAT('%', TRIM(p_search), '%')
            OR b.name LIKE CONCAT('%', TRIM(p_search), '%')
      )
      AND (
            p_stock_filter IS NULL
            OR p_stock_filter = ''
            OR UPPER(p_stock_filter) = 'TODOS'
            OR (UPPER(p_stock_filter) = 'BAJO' AND p.stock > 0 AND p.stock <= 10)
            OR (UPPER(p_stock_filter) = 'AGOTADO' AND p.stock = 0)
      )
    ORDER BY p.stock ASC, p.name ASC
    LIMIT v_offset, p_limit;
END$$

DROP PROCEDURE IF EXISTS `sp_inventory_count`$$
CREATE PROCEDURE sp_inventory_count(
    IN p_search VARCHAR(200),
    IN p_stock_filter VARCHAR(20)
)
BEGIN
    SELECT COUNT(*) AS total
    FROM products p
    INNER JOIN categories c ON c.id_category = p.id_category
    INNER JOIN brands b ON b.id_brand = p.id_brand
    WHERE p.status = 1
      AND p.deleted_at IS NULL
      AND (
            p_search IS NULL
            OR TRIM(p_search) = ''
            OR p.name LIKE CONCAT('%', TRIM(p_search), '%')
            OR c.name LIKE CONCAT('%', TRIM(p_search), '%')
            OR b.name LIKE CONCAT('%', TRIM(p_search), '%')
      )
      AND (
            p_stock_filter IS NULL
            OR p_stock_filter = ''
            OR UPPER(p_stock_filter) = 'TODOS'
            OR (UPPER(p_stock_filter) = 'BAJO' AND p.stock > 0 AND p.stock <= 10)
            OR (UPPER(p_stock_filter) = 'AGOTADO' AND p.stock = 0)
      );
END$$

DROP PROCEDURE IF EXISTS `sp_inventory_find_by_id`$$
CREATE PROCEDURE sp_inventory_find_by_id(
    IN p_id_product INT
)
BEGIN
    SELECT
        p.id_product,
        p.name,
        p.description,
        p.cost,
        p.price,
        p.stock,
        p.id_category,
        c.name AS category_name,
        p.id_brand,
        b.name AS brand_name,
        p.status,
        p.created_at,
        p.updated_at,
        p.deleted_at
    FROM products p
    INNER JOIN categories c ON c.id_category = p.id_category
    INNER JOIN brands b ON b.id_brand = p.id_brand
    WHERE p.id_product = p_id_product
    LIMIT 1;
END$$


-- ------------------------------------------------------------
-- Movimientos de stock
-- ------------------------------------------------------------

DROP PROCEDURE IF EXISTS `sp_stock_movement_create`$$
CREATE PROCEDURE sp_stock_movement_create(
    IN p_id_product INT,
    IN p_movement_type VARCHAR(10),
    IN p_quantity INT,
    IN p_description TEXT,
    IN p_reference VARCHAR(80),
    IN p_reference_id INT
)
BEGIN
    DECLARE v_current_stock INT DEFAULT 0;
    DECLARE v_type VARCHAR(10);

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    START TRANSACTION;

    SET v_type = UPPER(TRIM(p_movement_type));

    IF v_type NOT IN ('ENTRADA', 'SALIDA', 'AJUSTE') THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El tipo de movimiento debe ser ENTRADA, SALIDA o AJUSTE';
    END IF;

    IF p_quantity IS NULL OR p_quantity < 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'La cantidad no puede ser negativa';
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM products
        WHERE id_product = p_id_product
          AND status = 1
          AND deleted_at IS NULL
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El producto no existe o está inactivo';
    END IF;

    SELECT stock
    INTO v_current_stock
    FROM products
    WHERE id_product = p_id_product
    FOR UPDATE;

    IF v_type IN ('ENTRADA', 'SALIDA') AND p_quantity <= 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'La cantidad debe ser mayor a cero';
    END IF;

    IF v_type = 'SALIDA' AND v_current_stock < p_quantity THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Stock insuficiente para realizar la salida';
    END IF;

    IF v_type = 'ENTRADA' THEN
        UPDATE products
        SET stock = stock + p_quantity,
            updated_at = NOW()
        WHERE id_product = p_id_product;
    END IF;

    IF v_type = 'SALIDA' THEN
        UPDATE products
        SET stock = stock - p_quantity,
            updated_at = NOW()
        WHERE id_product = p_id_product;
    END IF;

    IF v_type = 'AJUSTE' THEN
        UPDATE products
        SET stock = p_quantity,
            updated_at = NOW()
        WHERE id_product = p_id_product;
    END IF;

    INSERT INTO stock_movements (
        id_product,
        movement_type,
        quantity,
        description,
        reference,
        reference_id,
        movement_date
    )
    VALUES (
        p_id_product,
        v_type,
        p_quantity,
        NULLIF(TRIM(p_description), ''),
        IFNULL(NULLIF(TRIM(p_reference), ''), 'MANUAL'),
        p_reference_id,
        NOW()
    );

    COMMIT;
END$$

DROP PROCEDURE IF EXISTS `sp_stock_movement_list`$$
CREATE PROCEDURE sp_stock_movement_list(
    IN p_search VARCHAR(200),
    IN p_id_product INT,
    IN p_movement_type VARCHAR(10),
    IN p_date_from DATE,
    IN p_date_to DATE,
    IN p_page INT,
    IN p_limit INT
)
BEGIN
    DECLARE v_offset INT DEFAULT 0;

    IF p_page IS NULL OR p_page < 1 THEN
        SET p_page = 1;
    END IF;

    IF p_limit NOT IN (10, 20, 50) THEN
        SET p_limit = 10;
    END IF;

    SET v_offset = (p_page - 1) * p_limit;

    SELECT
        sm.id_stock_movement,
        sm.id_product,
        p.name AS product_name,
        c.name AS category_name,
        b.name AS brand_name,
        sm.movement_type,
        sm.quantity,
        sm.description,
        sm.reference,
        sm.reference_id,
        sm.movement_date
    FROM stock_movements sm
    INNER JOIN products p ON p.id_product = sm.id_product
    INNER JOIN categories c ON c.id_category = p.id_category
    INNER JOIN brands b ON b.id_brand = p.id_brand
    WHERE (p_id_product IS NULL OR p_id_product = 0 OR sm.id_product = p_id_product)
      AND (
            p_movement_type IS NULL
            OR TRIM(p_movement_type) = ''
            OR UPPER(p_movement_type) = 'TODOS'
            OR sm.movement_type = UPPER(TRIM(p_movement_type))
      )
      AND (p_date_from IS NULL OR DATE(sm.movement_date) >= p_date_from)
      AND (p_date_to IS NULL OR DATE(sm.movement_date) <= p_date_to)
      AND (
            p_search IS NULL
            OR TRIM(p_search) = ''
            OR p.name LIKE CONCAT('%', TRIM(p_search), '%')
            OR sm.description LIKE CONCAT('%', TRIM(p_search), '%')
            OR sm.reference LIKE CONCAT('%', TRIM(p_search), '%')
      )
    ORDER BY sm.id_stock_movement DESC
    LIMIT v_offset, p_limit;
END$$

DROP PROCEDURE IF EXISTS `sp_stock_movement_count`$$
CREATE PROCEDURE sp_stock_movement_count(
    IN p_search VARCHAR(200),
    IN p_id_product INT,
    IN p_movement_type VARCHAR(10),
    IN p_date_from DATE,
    IN p_date_to DATE
)
BEGIN
    SELECT COUNT(*) AS total
    FROM stock_movements sm
    INNER JOIN products p ON p.id_product = sm.id_product
    WHERE (p_id_product IS NULL OR p_id_product = 0 OR sm.id_product = p_id_product)
      AND (
            p_movement_type IS NULL
            OR TRIM(p_movement_type) = ''
            OR UPPER(p_movement_type) = 'TODOS'
            OR sm.movement_type = UPPER(TRIM(p_movement_type))
      )
      AND (p_date_from IS NULL OR DATE(sm.movement_date) >= p_date_from)
      AND (p_date_to IS NULL OR DATE(sm.movement_date) <= p_date_to)
      AND (
            p_search IS NULL
            OR TRIM(p_search) = ''
            OR p.name LIKE CONCAT('%', TRIM(p_search), '%')
            OR sm.description LIKE CONCAT('%', TRIM(p_search), '%')
            OR sm.reference LIKE CONCAT('%', TRIM(p_search), '%')
      );
END$$

DROP PROCEDURE IF EXISTS `sp_stock_movement_find_by_id`$$
CREATE PROCEDURE sp_stock_movement_find_by_id(
    IN p_id_stock_movement INT
)
BEGIN
    SELECT
        sm.id_stock_movement,
        sm.id_product,
        p.name AS product_name,
        c.name AS category_name,
        b.name AS brand_name,
        sm.movement_type,
        sm.quantity,
        sm.description,
        sm.reference,
        sm.reference_id,
        sm.movement_date
    FROM stock_movements sm
    INNER JOIN products p ON p.id_product = sm.id_product
    INNER JOIN categories c ON c.id_category = p.id_category
    INNER JOIN brands b ON b.id_brand = p.id_brand
    WHERE sm.id_stock_movement = p_id_stock_movement
    LIMIT 1;
END$$

DROP PROCEDURE IF EXISTS `sp_stock_movement_product_options`$$
CREATE PROCEDURE sp_stock_movement_product_options()
BEGIN
    SELECT id_product AS id, name
    FROM products
    WHERE status = 1
      AND deleted_at IS NULL
    ORDER BY name ASC;
END$$


-- ------------------------------------------------------------
-- Perfil de usuario
-- ------------------------------------------------------------

DROP PROCEDURE IF EXISTS `sp_user_profile_find_by_id`$$
CREATE PROCEDURE sp_user_profile_find_by_id(
    IN p_id_user INT
)
BEGIN
    SELECT
        u.id_user,
        u.user_name,
        u.full_name,
        u.email,
        u.phone,
        u.profile_image_path,
        u.password,
        u.status,
        u.created_at,
        u.updated_at
    FROM users u
    WHERE u.id_user = p_id_user
    LIMIT 1;
END$$

DROP PROCEDURE IF EXISTS `sp_user_profile_update`$$
CREATE PROCEDURE sp_user_profile_update(
    IN p_id_user INT,
    IN p_user_name VARCHAR(100),
    IN p_full_name VARCHAR(150),
    IN p_email VARCHAR(150),
    IN p_phone VARCHAR(20),
    IN p_profile_image_path VARCHAR(500),
    IN p_password VARCHAR(255)
)
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM users
        WHERE id_user = p_id_user
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'No se encontró el usuario.';
    END IF;

    IF p_user_name IS NULL OR TRIM(p_user_name) = '' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Ingrese un nombre de usuario.';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM users
        WHERE LOWER(user_name) = LOWER(TRIM(p_user_name))
          AND id_user <> p_id_user
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El nombre de usuario ya está en uso.';
    END IF;

    UPDATE users
    SET
        user_name = TRIM(p_user_name),
        full_name = NULLIF(TRIM(p_full_name), ''),
        email = NULLIF(TRIM(p_email), ''),
        phone = NULLIF(TRIM(p_phone), ''),
        profile_image_path = NULLIF(TRIM(p_profile_image_path), ''),
        password = CASE
            WHEN p_password IS NULL OR TRIM(p_password) = '' THEN password
            ELSE p_password
        END,
        updated_at = NOW()
    WHERE id_user = p_id_user;
END$$


DELIMITER ;
