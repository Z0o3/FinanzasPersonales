package com.example.finanzas.service;

import com.example.finanzas.dao.MovimientoDAO;
import com.example.finanzas.dao.PresupuestoDAO;
import com.example.finanzas.dao.UsuarioDAO;
import com.example.finanzas.model.Movimiento;
import com.example.finanzas.model.Presupuesto;
import com.example.finanzas.model.Usuario;
import com.example.finanzas.util.Tasks;
import javafx.concurrent.Task;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Carga datos de ejemplo (usuario demo / demo123) para que reportes y gráficos tengan información. */
public class DemoDataSeeder {
    private final UsuarioDAO usuarioDao;
    private final AuthService auth;
    private final PresupuestoDAO presupuestoDao;
    private final MovimientoDAO movimientoDao;

    private final Random rnd = new Random(42);
    private final LocalDate hoy = LocalDate.now();
    private List<Movimiento> lista;
    private String uid;
    private YearMonth ym;

    public DemoDataSeeder(UsuarioDAO usuarioDao, AuthService auth, PresupuestoDAO presupuestoDao, MovimientoDAO movimientoDao) {
        this.usuarioDao = usuarioDao;
        this.auth = auth;
        this.presupuestoDao = presupuestoDao;
        this.movimientoDao = movimientoDao;
    }

    public Task<Void> seedTask() {
        return Tasks.of(() -> {
            sembrarSiHaceFalta();
            return null;
        });
    }

    private void sembrarSiHaceFalta() {
        if (usuarioDao.findByNombre("demo") != null) return;
        Usuario u = auth.registrar("demo", "demo123", "Usuario de Demostración");
        uid = u.getId();
        lista = new ArrayList<>();

        presupuestoDao.insertAll(List.of(
                new Presupuesto(null, uid, "Comida", 4000, 0.8),
                new Presupuesto(null, uid, "Transporte", 1800, 0.8),
                new Presupuesto(null, uid, "Entretenimiento", 1200, 0.8),
                new Presupuesto(null, uid, "Servicios", 2500, 0.8),
                new Presupuesto(null, uid, "Ropa", 800, 0.8)));

        for (ym = YearMonth.of(hoy.getYear() - 1, 1); !ym.isAfter(YearMonth.from(hoy)); ym = ym.plusMonths(1)) {
            generarMes();
        }
        movimientoDao.insertAll(lista);
    }

    private void generarMes() {
        int m = ym.getMonthValue();
        // Ingresos
        add("INGRESO", 1, 9000, "Quincena", "Nómina", "Cuenta Nómina", "SPEI");
        add("INGRESO", 15, 9000, "Quincena", "Nómina", "Cuenta Nómina", "SPEI");
        if (m % 2 == 0) add("INGRESO", 5, 2000, "Beca de manutención", "Becas", "Cuenta Digital", "Transferencia", "#becas");
        if (rnd.nextInt(100) < 55) add("INGRESO", 20, 1500 + rnd.nextInt(4500), "Proyecto freelance", "Freelance", "Cuenta Digital", "Transferencia", "#proyecto");
        // Gastos fijos
        add("GASTO", 3, 5500, "Renta", "Hogar", "Cuenta Nómina", "SPEI");
        add("GASTO", 8, 599, "Internet", "Servicios", "Cuenta Nómina", "Transferencia");
        add("GASTO", 10, 399, "Plan celular", "Servicios", "Tarjeta de crédito", "Tarjeta de crédito");
        add("GASTO", 12, 350 + rnd.nextInt(400), "Luz CFE", "Servicios", "Cuenta Nómina", "Transferencia");
        add("GASTO", 15, 299, "Streaming", "Entretenimiento", "Tarjeta de crédito", "Tarjeta de crédito", "#suscripciones");
        add("GASTO", 6, 650, "Gimnasio", "Salud", "Cuenta Nómina", "Tarjeta de débito");
        // Gastos variables
        String[] comida = {"Supermercado", "Restaurante", "Tacos", "Cafetería", "Despensa", "Comida rápida"};
        for (int i = 0; i < 8; i++)
            add("GASTO", 1 + rnd.nextInt(28), 80 + rnd.nextInt(380), comida[rnd.nextInt(comida.length)], "Comida", "Efectivo", rnd.nextBoolean() ? "Efectivo" : "Tarjeta de débito");
        String[] transp = {"Gasolina", "Uber", "Camión", "Caseta"};
        for (int i = 0; i < 5; i++)
            add("GASTO", 1 + rnd.nextInt(28), 60 + rnd.nextInt(300), transp[rnd.nextInt(transp.length)], "Transporte", "Cuenta Nómina", "Tarjeta de débito");
        String[] ocio = {"Cine", "Videojuego", "Salida con amigos", "Concierto"};
        for (int i = 0; i < 2; i++)
            add("GASTO", 1 + rnd.nextInt(28), 150 + rnd.nextInt(600), ocio[rnd.nextInt(ocio.length)], "Entretenimiento", "Tarjeta de crédito", "Tarjeta de crédito");
        if (rnd.nextInt(100) < 40) add("GASTO", 1 + rnd.nextInt(28), 300 + rnd.nextInt(900), "Compra de ropa", "Ropa", "Tarjeta de crédito", "Tarjeta de crédito");
        if (m % 3 == 0) add("GASTO", 9, 1200 + rnd.nextInt(1300), "Libros y cursos", "Educación", "Cuenta Digital", "Tarjeta de débito", "#libros");
        if (rnd.nextInt(100) < 30) add("GASTO", 1 + rnd.nextInt(28), 200 + rnd.nextInt(800), "Consulta / medicinas", "Salud", "Efectivo", "Efectivo");
        // Eventos especiales
        if (m == 4) add("GASTO", 28, 1200, "Declaración anual (contador)", "Servicios", "Cuenta Nómina", "Transferencia", "#impuestos");
        if (m == 7 || m == 12) {
            add("GASTO", 10, 4500, "Hotel", "Entretenimiento", "Tarjeta de crédito", "Tarjeta de crédito", "#vacaciones");
            add("GASTO", 7, 3800, "Vuelos", "Transporte", "Tarjeta de crédito", "Tarjeta de crédito", "#vacaciones");
        }
        // El mes actual queda cerca/sobre el límite para mostrar las alertas de presupuesto
        if (ym.equals(YearMonth.from(hoy)))
            add("GASTO", Math.min(2, hoy.getDayOfMonth()), 900, "Concierto", "Entretenimiento", "Tarjeta de crédito", "Tarjeta de crédito");
    }

    private void add(String tipo, int dia, double monto, String concepto, String cat, String cuenta, String metodo, String... tags) {
        LocalDate f = ym.atDay(Math.min(dia, ym.lengthOfMonth()));
        String estado = f.isAfter(hoy) ? "Pendiente" : (f.isBefore(hoy.minusMonths(1)) ? "Conciliado" : "Aplicado");
        lista.add(new Movimiento(null, uid, f, tipo, Math.round(monto * 100) / 100.0, concepto, cat, cuenta,
                estado, metodo, String.join(" ", tags)));
    }
}
