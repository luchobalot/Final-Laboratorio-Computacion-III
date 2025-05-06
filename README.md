# API de Transferencias Bancarias

## 1. Endpoint y Recepción de Datos

El sistema expone dos endpoints para transferencias:
- `/transferencias` (principal)
- `/api/transfer` (alternativo)

Ambos endpoints reciben un objeto JSON (`TransferenciasDto`) con los siguientes campos:
- `cuentaOrigen`: ID de la cuenta que envía dinero
- `cuentaDestino`: ID de la cuenta que recibe dinero
- `monto`: Cantidad a transferir
- `moneda`: Tipo de moneda (USD o ARS)

## 2. Validaciones Iniciales

El controlador `TransferenciasController` realiza las siguientes validaciones:
- Verifica que todos los campos requeridos estén presentes mediante anotaciones `@NotNull` y `@NotBlank`.
- Llama al servicio `transferenciasService.crearTransferencia(transferenciasDto)`.

## 3. Lógica de Negocio (`TransferenciasServiceImpl`)

El servicio `TransferenciasServiceImpl` implementa las siguientes validaciones y lógica de negocio:

### 3.1 Validaciones Básicas
- Verifica que la cuenta origen y destino sean diferentes.
- Verifica que el monto sea positivo.

### 3.2 Verificación de Existencia de Cuentas
- Obtiene la cuenta origen mediante `cuentaBancariaDao.getCuentaBancariaById()`.
- Intenta obtener la cuenta destino:
  - Si la cuenta destino existe, se trata de una transferencia dentro del mismo banco.
  - Si la cuenta destino no existe, simula una transferencia a otro banco (simula Banelco).

### 3.3 Verificación de Saldo Suficiente
- Comprueba si la cuenta origen tiene fondos suficientes considerando el límite de sobregiro.
- Si no tiene saldo suficiente, genera un Recibo con estado `FALLIDA`.

### 3.4 Simulación de Servicio Externo (Banelco)
- El método `exitoTransferencia()` simula la probabilidad de éxito/fallo:
  - 5% de probabilidad de fallo para transferencias entre bancos.
  - 1% de probabilidad de fallo para transferencias dentro del mismo banco.

### 3.5 Aplicación de Comisiones
- El método `sacarComision()` aplica las siguientes reglas de comisiones:
  - Para transferencias en pesos (ARS): 2% si supera $1,000,000.
  - Para transferencias en dólares (USD): 0.5% si supera $5,000.

### 3.6 Verificación de Compatibilidad de Monedas
- Si es transferencia interna, verifica que la moneda de ambas cuentas sea la misma.
- Si alguna de las cuentas tiene moneda distinta, lanza `MonedaNoCoincideException`.

## 4. Persistencia (`TransferenciasDaoImpl`)

Dependiendo del tipo de transferencia, se realiza lo siguiente:

### 4.1 Transferencia entre bancos distintos
- Método `transferBetweenBanks()`:
  - Actualiza solo la cuenta origen (resta monto + comisión).
  - Registra la transferencia en la cuenta origen.
  - Actualiza el cliente propietario.
  - Persiste los cambios en todos los archivos JSON.

### 4.2 Transferencia dentro del mismo banco
- Método `transferInBank()`:
  - Actualiza cuenta origen (resta monto + comisión).
  - Actualiza cuenta destino (suma monto).
  - Registra la transferencia en ambas cuentas.
  - Actualiza el/los cliente(s) propietario(s).
  - Persiste los cambios en todos los archivos JSON.

## 5. Generación y Retorno del Recibo

El servicio genera un objeto `Recibo` con los siguientes campos:
- `estado`: `EXITOSA`, `FALLIDA` o `ERROR`.
- `mensaje`: Descripción del resultado.

El controlador devuelve el recibo con código HTTP 201 (Created).

## Manejo de Excepciones

El controlador maneja diferentes tipos de errores y sus respectivos códigos HTTP:

- `CuentasIgualesException` o `MontoNoValidoException`: Error 400 (Bad Request).
- `CuentaNoExisteException` o `ClienteNoExisteException`: Error 404 (Not Found).
- `SaldoNoValidoException` o `MonedaNoCoincideException`: Error 400 (Bad Request).
