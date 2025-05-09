package ar.edu.utn.frbb.tup.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import ar.edu.utn.frbb.tup.controllers.dto.TransferenciasDto;
import ar.edu.utn.frbb.tup.models.Banco;
import ar.edu.utn.frbb.tup.models.CuentaBancaria;
import ar.edu.utn.frbb.tup.models.Recibo;
import ar.edu.utn.frbb.tup.models.TipoEstadoTransfers;
import ar.edu.utn.frbb.tup.models.TipoMoneda;
import ar.edu.utn.frbb.tup.models.Transferencias;
import ar.edu.utn.frbb.tup.models.exceptions.ClienteNoExisteException;
import ar.edu.utn.frbb.tup.models.exceptions.CuentaNoExisteException;
import ar.edu.utn.frbb.tup.models.exceptions.CuentasIgualesException;
import ar.edu.utn.frbb.tup.models.exceptions.MonedaNoCoincideException;
import ar.edu.utn.frbb.tup.models.exceptions.MontoNoValidoException;
import ar.edu.utn.frbb.tup.models.exceptions.SaldoNoValidoException;
import ar.edu.utn.frbb.tup.persistence.implementation.CuentaBancariaDaoImp;
import ar.edu.utn.frbb.tup.persistence.implementation.TransferenciasDaoImp;
import ar.edu.utn.frbb.tup.service.implementation.TransferenciasServiceImp;

@ExtendWith(MockitoExtension.class)
public class TransferenciasServiceTest {
    @Mock
    private CuentaBancariaDaoImp cuentaBancariaDao;

    @Mock
    private TransferenciasDaoImp transferenciasDao;

    @Mock
    private Banco banco;

    @InjectMocks
    private TransferenciasServiceImp transferenciasService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testTransferirErrorIniciales() {
        TransferenciasDto transferenciasDto = new TransferenciasDto();
        transferenciasDto.setCuentaOrigen(11222333L);
        transferenciasDto.setCuentaDestino(11222333L);
        transferenciasDto.setMonto(-1000.0f);
        transferenciasDto.setMoneda("USD");

        assertThrows(CuentasIgualesException.class, () -> transferenciasService.crearTransferencia(transferenciasDto));

        transferenciasDto.setCuentaDestino(44555666L);
        assertThrows(MontoNoValidoException.class, () -> transferenciasService.crearTransferencia(transferenciasDto));
    }

    @Test
    public void testTransferirSaldoInsuficiente() throws CuentaNoExisteException, CuentasIgualesException, MontoNoValidoException, SaldoNoValidoException, ClienteNoExisteException, MonedaNoCoincideException {
        TransferenciasDto transferenciasDto = new TransferenciasDto();
        transferenciasDto.setCuentaOrigen(11222333L);
        transferenciasDto.setCuentaDestino(44555666L);
        transferenciasDto.setMonto(150000f);
        transferenciasDto.setMoneda("USD");

        CuentaBancaria cuenta = new CuentaBancaria();
        cuenta.setSaldo(40000f);
        cuenta.setMoneda(TipoMoneda.DOLARES);

        when(cuentaBancariaDao.getCuentaBancariaById(transferenciasDto.getCuentaOrigen())).thenReturn(cuenta);
        when(banco.getLimiteSobregiro()).thenReturn(-100000f);
        when(banco.getSaldoInsuficiente()).thenReturn("No tiene suficiente saldo");

        Recibo recibo = transferenciasService.crearTransferencia(transferenciasDto);
        
        assertEquals(TipoEstadoTransfers.FALLIDA, recibo.getEstado());
        
        verify(banco).getLimiteSobregiro();
    }

    @Test
    public void testTransferirExitosamenteEntreBancos() throws CuentaNoExisteException, CuentasIgualesException, MontoNoValidoException, SaldoNoValidoException, ClienteNoExisteException, MonedaNoCoincideException {
        TransferenciasDto transferenciasDto = new TransferenciasDto();
        transferenciasDto.setCuentaOrigen(11222333L);
        transferenciasDto.setCuentaDestino(44555666L);
        transferenciasDto.setMonto(150000f);
        transferenciasDto.setMoneda("USD");

        CuentaBancaria cuenta = new CuentaBancaria();
        cuenta.setSaldo(400000f);
        cuenta.setMoneda(TipoMoneda.DOLARES);

        when(cuentaBancariaDao.getCuentaBancariaById(transferenciasDto.getCuentaOrigen())).thenReturn(cuenta);
        when(banco.getLimiteSobregiro()).thenReturn(-100000f);
        when(cuentaBancariaDao.getCuentaBancariaById(transferenciasDto.getCuentaDestino())).thenThrow(CuentaNoExisteException.class);
        when(banco.getExitoEntreBancos()).thenReturn("Transferencia entre bancos diferentes exitosa");
        lenient().doNothing().when(transferenciasDao).transferBetweenBanks(any(Transferencias.class));

        Recibo recibo = transferenciasService.crearTransferencia(transferenciasDto);
        
        // Verifica específicamente que el estado es EXITOSA
        assertEquals(TipoEstadoTransfers.EXITOSA, recibo.getEstado());
        
        // Verifica que se llamaron los métodos necesarios
        verify(banco).getLimiteSobregiro();
        verify(banco).getExitoEntreBancos();
    }

    @Test
    public void testTransferirMonedaDiferente() throws CuentaNoExisteException {
        TransferenciasDto transferenciasDto = new TransferenciasDto();
        transferenciasDto.setCuentaOrigen(11222333L);
        transferenciasDto.setCuentaDestino(44555666L);
        transferenciasDto.setMonto(150000f);
        transferenciasDto.setMoneda("USD");

        CuentaBancaria cuentaOrigen = new CuentaBancaria();
        cuentaOrigen.setSaldo(400000f);
        cuentaOrigen.setMoneda(TipoMoneda.DOLARES);

        CuentaBancaria cuentaDestino = new CuentaBancaria();
        cuentaDestino.setMoneda(TipoMoneda.PESOS_ARG);

        when(cuentaBancariaDao.getCuentaBancariaById(transferenciasDto.getCuentaOrigen())).thenReturn(cuentaOrigen);
        when(cuentaBancariaDao.getCuentaBancariaById(transferenciasDto.getCuentaDestino())).thenReturn(cuentaDestino);
        when(banco.getLimiteSobregiro()).thenReturn(-100000f);

        Exception exception = assertThrows(MonedaNoCoincideException.class, () -> 
            transferenciasService.crearTransferencia(transferenciasDto)
        );
        
        assertEquals("La moneda no coincide", exception.getMessage());
    }
}