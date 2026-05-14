# Documentacion tecnica del proyecto `Ventas_Facturacion`

## 1. Objetivo del proyecto

`Ventas_Facturacion` es una aplicacion de escritorio desarrollada en Java para administrar:

- autenticacion de usuarios
- ventas y comprobantes
- historial de ventas
- productos e inventario
- movimientos de stock
- clientes, proveedores y empleados
- configuracion maestra: roles, tipos de documento, ubicaciones, catalogos
- dashboard con indicadores y alertas

La solucion esta construida con una arquitectura de escritorio por capas:

`UI Swing (Presentacion / Paneles) -> Controllers -> DAO -> MySQL Stored Procedures -> Tablas`

Adicionalmente tiene una capa de servicios para integraciones externas:

- `DecolectaService` para consultar DNI y RUC
- `VoucherPrintService` para generar e imprimir comprobantes con QR

---

## 2. Resumen de arquitectura

### 2.1 Capas principales

| Capa | Responsabilidad | Archivos base |
|---|---|---|
| `App` | Punto de entrada de la aplicacion | `src/main/java/App/Main.java` lineas `1-23` |
| `Presentacion` | Ventanas principales, contenedor global, navegacion, helpers visuales | `src/main/java/Presentacion/*.java` |
| `Paneles` | Modulos funcionales visibles para el usuario | `src/main/java/Paneles/*.java` |
| `Controllers` | Validacion, orquestacion y reglas de aplicacion | `src/main/java/Controllers/*.java` |
| `DAO` | Acceso a datos por JDBC y llamada a stored procedures | `src/main/java/DAO/*.java` |
| `Models` | Objetos de dominio y DTOs | `src/main/java/Models/*.java` |
| `Config` | Configuracion de BD, empresa, seguridad y API externa | `src/main/java/Config/*.java` |
| `Services` | Integraciones tecnicas y servicios de infraestructura | `src/main/java/Services/*.java` |
| SQL | Definicion de base de datos, tablas y SP | `Tablas+SP.txt` lineas `1-6524` |

### 2.2 Patron de comunicacion

El patron dominante del proyecto es:

1. El usuario interactua con un `JFrame` o `JPanel`.
2. El panel llama a un `Controller`.
3. El `Controller` valida, normaliza y decide la accion.
4. El `Controller` usa un `DAO`.
5. El `DAO` abre conexion con `Database.getConnection()`.
6. El `DAO` ejecuta un stored procedure con `CallableStatement`.
7. MySQL responde con `ResultSet` o con cambios persistidos.
8. El `DAO` mapea el resultado a un `Model`.
9. El `Controller` devuelve el resultado a la UI.
10. La UI refresca tablas, indicadores o muestra mensajes.

---

## 3. Flujos funcionales clave

### 3.1 Flujo de login

Ruta tecnica:

`Main -> LoginJFrame -> UserController -> UserDAO -> sp_user_login -> DashboardJFrame`

Referencias:

- `src/main/java/App/Main.java` lineas `9-21`: inicia LookAndFeel y abre `LoginJFrame`.
- `src/main/java/Presentacion/LoginJFrame.java` lineas `244-267`: metodo `loginAction()`.
- `src/main/java/Controllers/UserController.java` lineas `141-164`: metodo `loginUser()`.
- `src/main/java/DAO/UserDAO.java` lineas `108-125`: metodo `login()`.
- `Tablas+SP.txt` linea `6011`: inicio de `sp_user_login`.

Que ocurre:

- se captura usuario y password
- el controlador valida campos
- `PasswordUtil` compara el hash PBKDF2 guardado
- si todo es correcto se abre `DashboardJFrame`

### 3.2 Flujo de registro de usuario

Ruta tecnica:

`RegisterJFrame -> UserController -> PasswordUtil -> UserDAO -> sp_user_register`

Referencias:

- `src/main/java/Presentacion/RegisterJFrame.java` lineas `288-319`: metodo `registerAction()`
- `src/main/java/Controllers/UserController.java` lineas `19-37`: metodo `registerUser()`
- `src/main/java/Config/PasswordUtil.java` lineas `20-76`: hash y verificacion
- `src/main/java/DAO/UserDAO.java` lineas `16-27`: metodo `register()`
- `Tablas+SP.txt` linea `6339`: inicio de `sp_user_register`

### 3.3 Flujo de venta

Ruta tecnica:

`SaleJPanel -> SaleController -> SaleDAO -> sp_sale_create -> VoucherPrintService -> DashboardJFrame`

Referencias:

- `src/main/java/Paneles/SaleJPanel.java` lineas `809-918`: metodo `confirmSale()`
- `src/main/java/Controllers/SaleController.java` lineas `35-43`: metodo `createSale()`
- `src/main/java/DAO/SaleDAO.java` lineas `92-120`: metodo `createSale()`
- `Tablas+SP.txt` linea `4540`: inicio de `sp_sale_create`
- `src/main/java/Services/VoucherPrintService.java` lineas `51-67`: `printSale()`
- `src/main/java/Presentacion/DashboardJFrame.java` lineas `617-826`: refresco y toast de alertas

Que ocurre:

- se arma el objeto `Sale`
- se envian detalles al SP
- el SP registra venta, detalle y descuenta stock
- se imprime el comprobante por PDF24
- si el stock entra en estado de alerta, la ventana principal muestra toast global

### 3.4 Flujo de historial de ventas

Ruta tecnica:

`SaleHistoryJPanel -> SaleController -> SaleDAO -> SP de historial`

Referencias:

- `src/main/java/Paneles/SaleHistoryJPanel.java` lineas `403-476`: carga de datos y metricas
- `src/main/java/Controllers/SaleController.java` lineas `104-130`: `listHistory()`, `getStats()`, `getRanking()`
- `src/main/java/DAO/SaleDAO.java` lineas `155-223`: consultas de historial
- `Tablas+SP.txt` linea `5013`: `sp_sale_history_list`
- `Tablas+SP.txt` linea `5101`: `sp_sale_history_stats`
- `Tablas+SP.txt` linea `5179`: `sp_sale_ranking`

### 3.5 Flujo de alertas de stock

Ruta tecnica:

`Operacion de stock o venta -> DashboardWindowSupport -> DashboardJFrame -> AlertController -> AlertDAO -> SP alertas`

Referencias:

- `src/main/java/Paneles/SaleJPanel.java` lineas `904-904`: dispara `notifyStockAlertsChanged()`
- `src/main/java/Paneles/StockMovementJPanel.java` lineas `456-462`: dispara alerta tras movimiento
- `src/main/java/Paneles/ProductJPanel.java` lineas `465-471`: dispara alerta tras crear/editar producto
- `src/main/java/Presentacion/DashboardJFrame.java` lineas `617-826`: polling, cola y toast global
- `src/main/java/Controllers/AlertController.java` lineas `17-54`
- `src/main/java/DAO/AlertDAO.java` lineas `16-102`
- `Tablas+SP.txt` lineas `6371-6524`: bloque `-- SP ALERTAS`

### 3.6 Flujo de consulta DNI / RUC

Ruta tecnica:

`ClientController o EmployeeController o SupplierController -> DecolectaService -> API Decolecta`

Referencias:

- `src/main/java/Services/DecolectaService.java` lineas `27-74`: consultas DNI y RUC
- `src/main/java/Services/DecolectaService.java` lineas `102-135`: envio HTTP
- `src/main/java/Controllers/ClientController.java` linea `174`
- `src/main/java/Controllers/EmployeeController.java` linea `183`
- `src/main/java/Controllers/SupplierController.java` linea `174`

---

## 4. Tecnologias utilizadas y librerias

Esta es la seccion mas importante para entender la base tecnica del proyecto.

### 4.1 Tabla general

| Tecnologia / libreria | Donde se declara | Para que sirve | Como ayuda al proyecto | Implementacion principal |
|---|---|---|---|---|
| Java 25 | `pom.xml` lineas `8-11` | Lenguaje principal y runtime | Permite construir toda la aplicacion de escritorio, seguridad, impresiones, HTTP y JDBC | Uso transversal en todo `src/main/java` |
| Maven | `pom.xml` lineas `1-47` | Gestion de dependencias y build | Centraliza versiones y facilita compilar/empaquetar | `pom.xml` completo |
| Swing / AWT | No se declara como dependencia porque es parte del JDK | Construccion de la interfaz grafica de escritorio | Permite ventanas, formularios, tablas, paneles, timers, layouts, impresiones y renderizados personalizados | `App/Main.java` lineas `9-21`, `Presentacion/LoginJFrame.java` lineas `62-354`, `Presentacion/DashboardJFrame.java` lineas `70-1405`, `Paneles/*.java` |
| MySQL Connector/J | `pom.xml` lineas `13-19` | Driver JDBC para MySQL | Permite abrir conexiones y ejecutar SP desde Java | `Config/Database.java` lineas `3-18`, DAO package |
| JDBC (`Connection`, `CallableStatement`, `ResultSet`) | Parte del JDK | Acceso a datos desde Java | Permite comunicacion directa con stored procedures y mapeo de resultados | `DAO/UserDAO.java` lineas `16-205`, `DAO/SaleDAO.java` lineas `23-313`, `DAO/DashboardDAO.java` lineas `21-199`, `DAO/AlertDAO.java` lineas `16-102` |
| MySQL Stored Procedures | `Tablas+SP.txt` lineas `263-6524` | Encapsulan logica de persistencia, filtros y reglas SQL | Reducen logica SQL en Java y centralizan consultas y operaciones complejas | Todo `Tablas+SP.txt` por secciones |
| PBKDF2 con HmacSHA256 | `Config/PasswordUtil.java` lineas `12-76` | Hash seguro de contrasenas | Evita guardar passwords en texto plano | `PasswordUtil.java` y `UserController.java` lineas `19-37`, `141-163` |
| `jiconfont-swing` | `pom.xml` lineas `21-26` | Renderizado de iconos en Swing | Mejora la experiencia visual de botones, menus y acciones | `Presentacion/LoginJFrame.java` lineas `25-26`, `44-46`; `DashboardJFrame.java` lineas `67-68`, `138-140`; `Paneles/*.java` |
| `jiconfont-font_awesome` | `pom.xml` lineas `28-33` | Paquete de iconos Font Awesome | Provee iconografia consistente sin imagenes rasterizadas | `LoginJFrame.java` lineas `90-108`, `213-239`; `DashboardJFrame.java` lineas `341-419`; `AlertsJPanel.java` lineas `28-29`, `136-166` |
| `LGoodDatePicker` | `pom.xml` lineas `35-40` | Selector de fechas para Swing | Facilita filtros por fecha en dashboard, historial y movimientos | `DashboardJPanel.java` lineas `9-10`, `198-218`, `275-280`; `SaleHistoryJPanel.java` lineas `11-12`, `144-147`, `240-245`; `StockMovementJPanel.java` lineas `9-10`, `130-131`, `181-186` |
| ZXing (`core`) | `pom.xml` lineas `42-46` | Generacion de codigos QR | Permite incrustar QR en comprobantes | `VoucherPrintService.java` lineas `6-11`, `253-269`, `344-349` |
| Java Print Service / AWT Printing | Parte del JDK | Impresion y generacion del ticket | Permite enviar el comprobante a PDF24 con formato de ticket | `VoucherPrintService.java` lineas `20-24`, `51-104`, `271-521` |
| Java HTTP Client | Parte del JDK | Consumo de APIs REST | Permite consultar DNI y RUC en Decolecta | `DecolectaService.java` lineas `9-11`, `19-24`, `102-135` |
| `SwingWorker` | Parte del JDK | Trabajo asincrono en Swing | Evita congelar la UI cuando se consultan alertas de stock | `DashboardJFrame.java` lineas `66`, `700-732` |
| `JLayeredPane` | Parte del JDK / Swing | Superponer componentes sobre la ventana | Permite mostrar toasts globales sin bloquear otras pantallas | `DashboardJFrame.java` lineas `806-836` |

### 4.2 Explicacion detallada por tecnologia

#### Java 25

- Declaracion: `pom.xml` lineas `8-11`
- Uso: todo el proyecto
- Aporte:
  - lenguaje principal
  - acceso a JDBC
  - impresiones
  - HTTP client
  - Swing
  - seguridad criptografica

#### Maven

- Declaracion: `pom.xml` lineas `1-47`
- Aporte:
  - organiza dependencias externas
  - fija version del compilador
  - estandariza el empaquetado del proyecto

#### Swing / AWT

- Implementacion principal:
  - `App/Main.java` lineas `9-21`
  - `Presentacion/LoginJFrame.java` lineas `62-354`
  - `Presentacion/RegisterJFrame.java` lineas `60-402`
  - `Presentacion/DashboardJFrame.java` lineas `70-1405`
  - `Paneles/*.java`
- Aporte:
  - toda la experiencia de escritorio
  - formularios CRUD
  - tablas con acciones
  - dashboards y graficas custom
  - toasts de alertas

#### MySQL Connector/J + JDBC

- Dependencia: `pom.xml` lineas `13-19`
- Conexion central: `Config/Database.java` lineas `3-18`
- Uso real:
  - `DAO/UserDAO.java` lineas `16-205`
  - `DAO/SaleDAO.java` lineas `23-313`
  - `DAO/DashboardDAO.java` lineas `21-199`
  - `DAO/AlertDAO.java` lineas `16-102`
- Aporte:
  - separa la capa SQL del resto de la aplicacion
  - permite reusar stored procedures
  - mantiene un patron consistente de acceso a datos

#### Stored Procedures MySQL

- Archivo central: `Tablas+SP.txt` lineas `1-6524`
- Aporte:
  - encapsulan logica de negocio del lado BD
  - soportan filtros, paginacion, altas, bajas y actualizaciones
  - centralizan operacion de venta, historial y dashboard

#### PBKDF2 / PasswordUtil

- Implementacion: `Config/PasswordUtil.java` lineas `12-76`
- Consumo:
  - `Controllers/UserController.java` lineas `19-37`
  - `Controllers/UserController.java` lineas `40-59`
  - `Controllers/UserController.java` lineas `62-92`
  - `Controllers/UserController.java` lineas `141-163`
  - `Controllers/UserController.java` lineas `196-223`
- Aporte:
  - seguridad de contrasenas
  - validacion segura sin exponer texto plano

#### JIconFont + FontAwesome

- Dependencias:
  - `pom.xml` lineas `21-33`
- Uso principal:
  - `LoginJFrame.java` lineas `25-26`, `44-46`, `90-108`, `213-239`
  - `RegisterJFrame.java` lineas `24-25`, `50`, `97-126`, `245-283`
  - `DashboardJFrame.java` lineas `67-68`, `138-140`, `341-419`
  - `AlertsJPanel.java` lineas `28-29`, `65-67`, `136-166`
  - `SaleHistoryJPanel.java` lineas `50-51`, `89-91`, `352-353`, `869-891`
- Aporte:
  - botones mas claros
  - menus con iconografia
  - acciones visualmente reconocibles

#### LGoodDatePicker

- Dependencia: `pom.xml` lineas `35-40`
- Uso:
  - `DashboardJPanel.java` lineas `9-10`, `198-218`, `275-280`
  - `SaleHistoryJPanel.java` lineas `11-12`, `144-147`, `240-245`
  - `StockMovementJPanel.java` lineas `9-10`, `130-131`, `181-186`
- Aporte:
  - mejora filtros por rango de fechas
  - evita errores de formato manual

#### ZXing

- Dependencia: `pom.xml` lineas `42-46`
- Uso:
  - `VoucherPrintService.java` lineas `6-11`
  - `VoucherPrintService.java` lineas `253-269`
  - `VoucherPrintService.java` lineas `344-349`
- Aporte:
  - genera QR del comprobante
  - mejora trazabilidad del documento

#### Java Print Service

- Uso:
  - `VoucherPrintService.java` lineas `20-24`
  - `VoucherPrintService.java` lineas `51-104`
  - `VoucherPrintService.java` lineas `271-521`
- Aporte:
  - imprime tickets en PDF24
  - construye un layout tipo boleta/ticket

#### Java HTTP Client

- Uso:
  - `DecolectaService.java` lineas `9-11`
  - `DecolectaService.java` lineas `19-24`
  - `DecolectaService.java` lineas `102-135`
- Aporte:
  - consume API de terceros sin librerias extra
  - valida DNI/RUC desde servicios externos

---

## 5. Como se comunica la estructura del proyecto

### 5.1 Comunicacion entre `Presentacion` y `Paneles`

- `LoginJFrame` autentica y abre `DashboardJFrame`
- `DashboardJFrame` es el contenedor maestro
- `DashboardJFrame` usa `CardLayout` para cambiar entre modulos
- cada modulo de trabajo vive como `JPanel` dentro del paquete `Paneles`

Referencias:

- `src/main/java/Presentacion/LoginJFrame.java` lineas `244-267`
- `src/main/java/Presentacion/DashboardJFrame.java` lineas `543-575`
- `src/main/java/Presentacion/DashboardJFrame.java` lineas `671-694`
- `src/main/java/Presentacion/SectionRefreshable.java` lineas `1-6`

### 5.2 Comunicacion entre `Paneles` y `Controllers`

Cada panel instancia su controlador principal. Ejemplos:

- `SaleJPanel` -> `SaleController`
- `SaleHistoryJPanel` -> `SaleController`
- `InventoryJPanel` -> `InventoryController`
- `AlertsJPanel` -> `AlertController`
- `DashboardJPanel` -> `DashboardController`

### 5.3 Comunicacion entre `Controllers` y `DAO`

El controlador:

- valida reglas de negocio
- limpia y normaliza entradas
- transforma mensajes SQL en mensajes legibles
- delega persistencia al DAO

Ejemplo fuerte:

- `src/main/java/Controllers/SaleController.java` lineas `35-134`
- `src/main/java/DAO/SaleDAO.java` lineas `92-313`

### 5.4 Comunicacion entre `DAO` y MySQL

Todos los DAOs abren conexion via:

- `src/main/java/Config/Database.java` lineas `7-18`

Y luego ejecutan SP con:

- `CallableStatement`
- `connection.prepareCall(...)`

Ejemplo:

- `src/main/java/DAO/UserDAO.java` lineas `16-205`
- `src/main/java/DAO/SaleDAO.java` lineas `23-313`
- `src/main/java/DAO/AlertDAO.java` lineas `16-102`

### 5.5 Comunicacion transversal para refresco de datos

El patron `SectionRefreshable` permite que `DashboardJFrame` refresque solo el panel visible despues de un cambio.

Referencias:

- `src/main/java/Presentacion/SectionRefreshable.java` lineas `1-6`
- `src/main/java/Presentacion/DashboardJFrame.java` lineas `681-693`

### 5.6 Comunicacion transversal para alertas globales

El proyecto ahora tiene un segundo canal transversal:

- venta o movimiento cambia stock
- el panel busca el `DashboardJFrame` actual
- el `DashboardJFrame` consulta alertas activas
- si hay un nuevo nivel de severidad, muestra toast en esquina superior derecha

Referencias:

- `src/main/java/Presentacion/DashboardWindowSupport.java` lineas `1-25`
- `src/main/java/Presentacion/DashboardJFrame.java` lineas `617-836`
- `src/main/java/Presentacion/StockAlertToastPanel.java` lineas `1-140`

---

## 6. Estructura del proyecto por paquetes y archivos

Las lineas indicadas son el rango completo del archivo.

### 6.1 Paquete `App`

| Archivo | Lineas | Responsabilidad |
|---|---:|---|
| `Main.java` | `1-23` | Punto de entrada. Configura LookAndFeel y abre el login. |

### 6.2 Paquete `Config`

| Archivo | Lineas | Responsabilidad |
|---|---:|---|
| `CompanyConfig.java` | `1-43` | Datos de la empresa usados en impresion de comprobantes. |
| `Database.java` | `1-20` | Conexion JDBC central a MySQL. |
| `DecolectaConfig.java` | `1-48` | URL base, token y timeout de la API Decolecta. |
| `PasswordUtil.java` | `1-78` | Hash y verificacion de contrasenas con PBKDF2. |

### 6.3 Paquete `Controllers`

| Archivo | Lineas | Responsabilidad |
|---|---:|---|
| `AlertController.java` | `1-91` | Orquesta filtros, paginacion y severidad de alertas. |
| `BrandController.java` | `1-173` | Reglas de negocio de marcas. |
| `CategoryController.java` | `1-183` | Reglas de negocio de categorias. |
| `ClientController.java` | `1-283` | CRUD de clientes y consulta DNI. |
| `DashboardController.java` | `1-103` | Indicadores y datos del dashboard. |
| `DocumentTypeController.java` | `1-183` | Gestion de tipos de documento. |
| `EmployeeController.java` | `1-296` | CRUD de empleados y consulta DNI. |
| `InventoryController.java` | `1-103` | Filtros y metricas de inventario. |
| `LocationController.java` | `1-218` | Paises, regiones, provincias y distritos. |
| `PaymentMethodController.java` | `1-183` | Metodos de pago. |
| `ProductController.java` | `1-244` | CRUD de productos, stock y catalogo base. |
| `RoleController.java` | `1-183` | Gestion de roles. |
| `SaleController.java` | `1-255` | Reglas de venta, historial, ranking y validacion de cliente. |
| `StockMovementController.java` | `1-175` | Entradas, salidas y ajustes de stock. |
| `SupplierController.java` | `1-291` | CRUD de proveedores y consulta RUC. |
| `UserController.java` | `1-392` | Registro, login, perfil, mantenimiento y seguridad de usuarios. |

### 6.4 Paquete `DAO`

| Archivo | Lineas | Responsabilidad |
|---|---:|---|
| `AlertDAO.java` | `1-103` | Acceso a SP de alertas de stock. |
| `BrandDAO.java` | `1-202` | Llamadas SQL de marcas. |
| `CategoryDAO.java` | `1-205` | Llamadas SQL de categorias. |
| `ClientDAO.java` | `1-385` | Persistencia y consultas de clientes. |
| `DashboardDAO.java` | `1-206` | Consultas del dashboard e indicadores. |
| `DocumentTypeDAO.java` | `1-205` | Persistencia de tipos de documento. |
| `EmployeeDAO.java` | `1-342` | Persistencia de empleados. |
| `InventoryDAO.java` | `1-147` | Consulta de stock y metricas de inventario. |
| `LocationDAO.java` | `1-263` | Persistencia de ubicaciones jerarquicas. |
| `PaymentMethodDAO.java` | `1-205` | Persistencia de metodos de pago. |
| `ProductDAO.java` | `1-259` | Persistencia y catalogo de productos. |
| `RoleDAO.java` | `1-205` | Persistencia de roles. |
| `SaleDAO.java` | `1-387` | Registro de ventas, historial, ranking y detalle. |
| `StockMovementDAO.java` | `1-185` | Persistencia de movimientos de stock. |
| `SupplierDAO.java` | `1-330` | Persistencia de proveedores. |
| `UserDAO.java` | `1-253` | Login, registro, perfil y mantenimiento de usuarios. |

### 6.5 Paquete `Models`

| Archivo | Lineas | Responsabilidad |
|---|---:|---|
| `Brand.java` | `1-73` | Entidad marca. |
| `Category.java` | `1-82` | Entidad categoria. |
| `Client.java` | `1-209` | Entidad cliente. |
| `DashboardAlertItem.java` | `1-34` | DTO de alerta resumida del dashboard. |
| `DashboardChartItem.java` | `1-48` | DTO generico para series y graficas. |
| `DashboardLatestSale.java` | `1-57` | DTO de ultima venta para dashboard. |
| `DashboardSummary.java` | `1-145` | DTO de metricas principales del dashboard. |
| `DocumentType.java` | `1-93` | Entidad tipo de documento. |
| `Employee.java` | `1-228` | Entidad empleado. |
| `InventoryMetrics.java` | `1-66` | DTO de metricas agregadas de inventario. |
| `InventoryProduct.java` | `1-142` | DTO de producto para el modulo inventario. |
| `LocationItem.java` | `1-119` | DTO de pais/region/provincia/distrito. |
| `PaymentMethod.java` | `1-82` | Entidad metodo de pago. |
| `Product.java` | `1-165` | Entidad producto. |
| `ReniecPerson.java` | `1-53` | DTO para respuesta RENIEC/Decolecta. |
| `Role.java` | `1-93` | Entidad rol. |
| `Sale.java` | `1-242` | Entidad venta y cabecera de comprobante. |
| `SaleDetail.java` | `1-136` | Entidad detalle de venta. |
| `SaleHistoryStats.java` | `1-49` | DTO de metricas del historial de ventas. |
| `SaleProductItem.java` | `1-97` | DTO de producto listo para vender. |
| `SaleRanking.java` | `1-34` | DTO de ranking de vendedores. |
| `SelectOption.java` | `1-41` | DTO reutilizable para combos. |
| `StockAlert.java` | `1-88` | DTO detallado de alerta de stock. |
| `StockAlertSummary.java` | `1-41` | DTO agregado de alertas. |
| `StockMovement.java` | `1-111` | Entidad movimiento de stock. |
| `SunatCompany.java` | `1-62` | DTO para respuesta SUNAT/Decolecta. |
| `Supplier.java` | `1-192` | Entidad proveedor. |
| `User.java` | `1-139` | Entidad usuario. |

### 6.6 Paquete `Paneles`

| Archivo | Lineas | Responsabilidad |
|---|---:|---|
| `AlertsJPanel.java` | `1-402` | Vista completa del modulo de alertas de stock. |
| `BrandJPanel.java` | `1-873` | CRUD visual de marcas. |
| `CatalogJPanel.java` | `1-81` | Contenedor de submodulos catalogo. |
| `CategoryJPanel.java` | `1-908` | CRUD visual de categorias. |
| `ClientJPanel.java` | `1-1179` | CRUD visual de clientes con apoyo RENIEC. |
| `DashboardJPanel.java` | `1-1302` | Dashboard analitico con metricas y graficas custom. |
| `DocumentTypeJPanel.java` | `1-924` | CRUD visual de tipos de documento. |
| `EmployeeJPanel.java` | `1-1202` | CRUD visual de empleados con apoyo RENIEC. |
| `InventoryJPanel.java` | `1-559` | Consulta de stock, metricas y acceso a movimientos. |
| `LocationJPanel.java` | `1-1058` | CRUD de ubicaciones jerarquicas. |
| `PaymentMethodJPanel.java` | `1-908` | CRUD visual de metodos de pago. |
| `ProductJPanel.java` | `1-1134` | CRUD de productos, imagenes y stock inicial. |
| `ProfileJPanel.java` | `1-510` | Edicion del perfil del usuario actual. |
| `RoleJPanel.java` | `1-924` | CRUD visual de roles. |
| `SaleHistoryJPanel.java` | `1-943` | Historial, filtros, ranking e impresion de ventas. |
| `SaleJPanel.java` | `1-1075` | Venta en punto de atencion con carrito y comprobante. |
| `StockMovementJPanel.java` | `1-686` | Registro y consulta de movimientos de stock. |
| `SupplierJPanel.java` | `1-1180` | CRUD visual de proveedores con apoyo SUNAT. |
| `UserJPanel.java` | `1-715` | Mantenimiento de usuarios del sistema. |

### 6.7 Paquete `Presentacion`

| Archivo | Lineas | Responsabilidad |
|---|---:|---|
| `DashboardJFrame.java` | `1-1405` | Ventana principal, menu lateral, CardLayout, refresh global y toasts. |
| `DashboardWindowSupport.java` | `1-25` | Helper para localizar el `DashboardJFrame` activo desde cualquier panel. |
| `LoginJFrame.java` | `1-354` | Pantalla de ingreso al sistema. |
| `RegisterJFrame.java` | `1-402` | Pantalla de registro rapido de usuario. |
| `SectionRefreshable.java` | `1-6` | Contrato para refrescar paneles visibles. |
| `StockAlertToastPanel.java` | `1-140` | Componente flotante de alerta global. |

### 6.8 Paquete `Services`

| Archivo | Lineas | Responsabilidad |
|---|---:|---|
| `DecolectaService.java` | `1-179` | Integracion HTTP con DNI y RUC. |
| `VoucherPrintService.java` | `1-521` | Armado e impresion de comprobantes con QR. |

---

## 7. Archivo SQL central: `Tablas+SP.txt`

Este archivo es la base estructural del sistema. Contiene:

- creacion de la base de datos
- creacion de tablas
- definicion de stored procedures por modulo

### 7.1 Estructura general del archivo SQL

| Bloque | Lineas | Contenido |
|---|---:|---|
| Base de datos y tablas | `2-251` | `CREATE DATABASE`, `CREATE TABLE` y relaciones principales |
| SP BRAND | `263-513` | CRUD y consultas de marcas |
| SP CATEGORY | `515-777` | CRUD y consultas de categorias |
| SP CLIENT | `779-1145` | Clientes, busquedas y DNI |
| SP DASHBOARD | `1147-1653` | Indicadores, charts, alertas del dashboard |
| SP DOCUMENT_TYPE | `1655-1917` | Tipos de documento |
| SP EMPLOYEE | `1919-2482` | Empleados |
| SP INVENTORY | `2484-2597` | Inventario y filtros de stock |
| SP LOCATION | `2599-3419` | Paises, regiones, provincias y distritos |
| SP PAYMENT_METHOD | `3421-3683` | Metodos de pago |
| SP PRODUCT | `3685-4253` | Productos |
| SP ROLE | `4255-4517` | Roles |
| SP SALE | `4519-5245` | Venta, detalle, historial y ranking |
| SP STOCK_MOVEMENT | `5247-5472` | Movimientos de stock |
| SP SUPPLIER | `5474-6006` | Proveedores |
| SP LOGIN | `6008-6028` | Login SQL |
| SP LOGOUT | `6030-6054` | Logout SQL |
| SP USER | `6056-6334` | Gestion y perfil de usuarios |
| SP REGISTER | `6336-6369` | Registro inicial de usuario |
| SP ALERTAS | `6371-6524` | Alertas de stock bajo, critico y urgente |

### 7.2 Stored procedures clave para el negocio

| SP | Linea inicial | Uso en Java |
|---|---:|---|
| `sp_user_login` | `6011` | `UserDAO.login()` |
| `sp_user_register` | `6339` | `UserDAO.register()` |
| `sp_user_profile_update` | `6285` | `UserDAO.updateProfile()` |
| `sp_sale_create` | `4540` | `SaleDAO.createSale()` |
| `sp_sale_history_list` | `5013` | `SaleDAO.listHistory()` |
| `sp_sale_history_stats` | `5101` | `SaleDAO.getStats()` |
| `sp_sale_ranking` | `5179` | `SaleDAO.getRanking()` |
| `sp_dashboard_alerts` | `1150` | `DashboardDAO.listAlerts()` |
| `sp_alerts_low_stock_summary` | `6374` | `AlertDAO.getSummary()` |
| `sp_alerts_low_stock_count` | `6386` | `AlertDAO.count()` |
| `sp_alerts_low_stock_list` | `6433` | `AlertDAO.list()` |

---

## 8. Modulos mas importantes y como se relacionan

### 8.1 Autenticacion y usuarios

Archivos principales:

- `Presentacion/LoginJFrame.java` lineas `1-354`
- `Presentacion/RegisterJFrame.java` lineas `1-402`
- `Controllers/UserController.java` lineas `1-392`
- `DAO/UserDAO.java` lineas `1-253`
- `Config/PasswordUtil.java` lineas `1-78`

Relacion:

- Login y registro viven en `Presentacion`
- reglas de negocio en `UserController`
- persistencia en `UserDAO`
- seguridad criptografica en `PasswordUtil`

### 8.2 Ventas

Archivos principales:

- `Paneles/SaleJPanel.java` lineas `1-1075`
- `Controllers/SaleController.java` lineas `1-255`
- `DAO/SaleDAO.java` lineas `1-387`
- `Services/VoucherPrintService.java` lineas `1-521`
- `Models/Sale.java` lineas `1-242`
- `Models/SaleDetail.java` lineas `1-136`
- `Tablas+SP.txt` lineas `4519-5245`

Relacion:

- `SaleJPanel` arma el carrito y los datos del comprobante
- `SaleController` valida metodo de pago, cliente y detalles
- `SaleDAO` llama `sp_sale_create`
- `VoucherPrintService` imprime en PDF24

### 8.3 Historial de ventas

Archivos principales:

- `Paneles/SaleHistoryJPanel.java` lineas `1-943`
- `Controllers/SaleController.java` lineas `104-130`
- `DAO/SaleDAO.java` lineas `155-223`
- `Tablas+SP.txt` lineas `5013-5245`

### 8.4 Dashboard

Archivos principales:

- `Presentacion/DashboardJFrame.java` lineas `1-1405`
- `Paneles/DashboardJPanel.java` lineas `1-1302`
- `Controllers/DashboardController.java` lineas `1-103`
- `DAO/DashboardDAO.java` lineas `1-206`
- `Tablas+SP.txt` lineas `1147-1653`

Relacion:

- `DashboardJFrame` contiene la app
- `DashboardJPanel` dibuja metricas y charts
- `DashboardDAO` alimenta los widgets con stored procedures

### 8.5 Inventario y alertas

Archivos principales:

- `Paneles/InventoryJPanel.java` lineas `1-559`
- `Paneles/StockMovementJPanel.java` lineas `1-686`
- `Paneles/AlertsJPanel.java` lineas `1-402`
- `Controllers/InventoryController.java` lineas `1-103`
- `Controllers/StockMovementController.java` lineas `1-175`
- `Controllers/AlertController.java` lineas `1-91`
- `DAO/InventoryDAO.java` lineas `1-147`
- `DAO/AlertDAO.java` lineas `1-103`
- `Presentacion/StockAlertToastPanel.java` lineas `1-140`
- `Tablas+SP.txt` lineas `2484-2597`, `5247-5472`, `6371-6524`

Relacion:

- inventario consulta estado actual
- movimientos modifica stock manualmente
- alertas lista productos con stock `<= 10`
- `DashboardJFrame` escucha cambios y dispara toast global

### 8.6 Clientes, empleados y proveedores

Archivos principales:

- `Paneles/ClientJPanel.java` lineas `1-1179`
- `Paneles/EmployeeJPanel.java` lineas `1-1202`
- `Paneles/SupplierJPanel.java` lineas `1-1180`
- `Services/DecolectaService.java` lineas `1-179`

Relacion:

- clientes y empleados usan DNI
- proveedores usan RUC
- la consulta remota vive en `DecolectaService`

---

## 9. Patrones y decisiones tecnicas del proyecto

### 9.1 Stored procedures como frontera de persistencia

La aplicacion casi no ejecuta SQL inline complejo. La mayor parte del trabajo se hace con SP.  
Ventaja:

- centraliza reglas SQL
- facilita mantenimiento de consultas
- permite reusar logica desde varios modulos

### 9.2 Controllers ligeros pero con validacion

Los controladores no son simples wrappers. Tambien:

- validan entrada
- normalizan texto, pagina, limites y filtros
- encapsulan mensajes de error SQL

### 9.3 Paneles desacoplados del contenedor

Cada panel conoce su controlador, pero no la ventana completa.  
Cuando necesita algo global, usa:

- `DashboardWindowSupport.java` lineas `1-25`

### 9.4 Refresco de datos basado en interfaz

`SectionRefreshable` evita recargar toda la aplicacion cuando cambia un modulo.  
Solo se refresca el panel visible.

### 9.5 Servicios externos aislados

Las consultas a terceros y la impresion estan separadas de los paneles.  
Esto simplifica pruebas, cambios de proveedor e integraciones nuevas.

---

## 10. Archivos especialmente criticos

Si alguien nuevo entra al proyecto, estos son los primeros archivos que deberia leer:

1. `pom.xml` lineas `1-47`
2. `src/main/java/App/Main.java` lineas `1-23`
3. `src/main/java/Presentacion/LoginJFrame.java` lineas `244-267`
4. `src/main/java/Presentacion/DashboardJFrame.java` lineas `543-575`, `617-836`
5. `src/main/java/Controllers/UserController.java` lineas `19-37`, `141-163`
6. `src/main/java/Controllers/SaleController.java` lineas `35-134`
7. `src/main/java/DAO/SaleDAO.java` lineas `92-313`
8. `src/main/java/Services/VoucherPrintService.java` lineas `51-521`
9. `src/main/java/Services/DecolectaService.java` lineas `19-135`
10. `Tablas+SP.txt` lineas `4519-5245`, `6056-6524`

---

## 11. Conclusiones tecnicas

Este proyecto esta organizado como una aplicacion de escritorio Java con una separacion bastante clara entre:

- interfaz de usuario
- logica de negocio
- acceso a datos
- servicios externos
- base de datos procedural

Las piezas mas importantes para entender su comportamiento real son:

- `DashboardJFrame` como contenedor principal
- `SectionRefreshable` como mecanismo de refresco
- `UserController` + `PasswordUtil` para autenticacion
- `SaleController` + `SaleDAO` + `sp_sale_create` para el corazon del negocio
- `VoucherPrintService` para comprobantes
- `DecolectaService` para integraciones externas
- `AlertController` + `AlertDAO` + `DashboardJFrame` para alertas globales de stock

Si se mantiene este mismo patron al seguir creciendo el sistema, el proyecto puede escalar de forma ordenada.
