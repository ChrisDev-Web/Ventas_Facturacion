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
);

DELIMITER $$

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

DELIMITER ;
