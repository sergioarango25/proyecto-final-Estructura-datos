import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;

public class AgenciaViajes {

    // ---------- ESTRUCTURA ESTÁTICA ----------
    static String[][] destinos = {
        {"1", "Cartagena", "Colombia", "1200", "USD"},
        {"2", "Cancún", "México", "1500", "USD"},
        {"3", "Madrid", "España", "2000", "USD"}
    };

    // ---------- LISTA ENLAZADA ----------
    static class Nodo {
        String nombre;
        String cedula;
        Nodo siguiente;

        Nodo(String nombre, String cedula) {
            this.nombre = nombre;
            this.cedula = cedula;
            this.siguiente = null;
        }
    }

    static class ListaClientes {
        Nodo cabeza;

        void agregar(String nombre, String cedula) {
            Nodo nuevo = new Nodo(nombre, cedula);
            if (cabeza == null) cabeza = nuevo;
            else {
                Nodo actual = cabeza;
                while (actual.siguiente != null) actual = actual.siguiente;
                actual.siguiente = nuevo;
            }
        }

        String mostrarComoTexto() {
            StringBuilder sb = new StringBuilder();
            Nodo actual = cabeza;
            while (actual != null) {
                sb.append("Cliente: ").append(actual.nombre)
                  .append(" - Cédula: ").append(actual.cedula).append("\n");
                actual = actual.siguiente;
            }
            return sb.toString();
        }

        Nodo buscarPorCedula(String cedula) {
            Nodo actual = cabeza;
            while (actual != null) {
                if (actual.cedula.equals(cedula)) return actual;
                actual = actual.siguiente;
            }
            return null;
        }
    }

    // ---------- PILA ----------
    static class Pila {
        Stack<String> pila = new Stack<>();
        void apilar(String mensaje) { pila.push(mensaje); }
        String desapilar() { return pila.isEmpty() ? "Sin historial" : pila.pop(); }
        String mostrar() { return pila.isEmpty() ? "[]" : pila.toString(); }
    }

    // ---------- COLA ----------
    static class Cola {
        Queue<String> cola = new LinkedList<>();
        void encolar(String cliente) { cola.add(cliente); }
        String desencolar() { return cola.isEmpty() ? "Sin clientes en espera" : cola.poll(); }
        String mostrar() { return cola.isEmpty() ? "[]" : cola.toString(); }
        java.util.List<String> obtenerLista() { return new ArrayList<>(cola); }
    }

    // ---------- RESERVAS ----------
    static class Destino {
        String nombre;
        Destino(String nombre) { this.nombre = nombre; }
        String getNombre() { return nombre; }
    }

    static class Reserva {
        String nombreCliente;
        String cedulaCliente;
        String destino;

        Reserva(String nombreCliente, String cedulaCliente, String destino) {
            this.nombreCliente = nombreCliente;
            this.cedulaCliente = cedulaCliente;
            this.destino = destino;
        }

        public String toString() {
            return "Cliente: " + nombreCliente + " | Cédula: " + cedulaCliente + " | Destino: " + destino;
        }
    }

    // ---------- ÁRBOL BINARIO ----------
    static class NodoArbol {
        String destino;
        NodoArbol izquierda, derecha;

        NodoArbol(String destino) {
            this.destino = destino;
        }
    }

    static class ArbolDestinos {
        NodoArbol raiz;

        void insertar(String destino) {
            raiz = insertarRec(raiz, destino);
        }

        NodoArbol insertarRec(NodoArbol nodo, String destino) {
            if (nodo == null) return new NodoArbol(destino);

            if (destino.compareToIgnoreCase(nodo.destino) < 0)
                nodo.izquierda = insertarRec(nodo.izquierda, destino);
            else
                nodo.derecha = insertarRec(nodo.derecha, destino);

            return nodo;
        }

        void mostrarInOrden(StringBuilder sb) {
            recorrerInOrden(raiz, sb);
        }

        void recorrerInOrden(NodoArbol nodo, StringBuilder sb) {
            if (nodo != null) {
                recorrerInOrden(nodo.izquierda, sb);
                sb.append(nodo.destino).append("\n");
                recorrerInOrden(nodo.derecha, sb);
            }
        }
    }

    // ---------- ORDENAMIENTO INSERTION SORT ----------
    static void ordenarReservasPorNombre(java.util.List<Reserva> lista) {
        for (int i = 1; i < lista.size(); i++) {
            Reserva actual = lista.get(i);
            int j = i - 1;

            while (j >= 0 && lista.get(j).nombreCliente.compareToIgnoreCase(actual.nombreCliente) > 0) {
                lista.set(j + 1, lista.get(j));
                j--;
            }
            lista.set(j + 1, actual);
        }
    }

    static void ordenarReservasPorDestino(java.util.List<Reserva> lista) {
        for (int i = 1; i < lista.size(); i++) {
            Reserva actual = lista.get(i);
            int j = i - 1;

            while (j >= 0 && lista.get(j).destino.compareToIgnoreCase(actual.destino) > 0) {
                lista.set(j + 1, lista.get(j));
                j--;
            }
            lista.set(j + 1, actual);
        }
    }

    // ---------- LISTAS GLOBALES ----------
    static ListaClientes listaClientes = new ListaClientes();
    static Pila pilaCancelaciones = new Pila();
    static Pila pilaHistorial = new Pila();
    static Cola colaAtencion = new Cola();
    static java.util.List<Reserva> reservas = new ArrayList<>();
    static java.util.List<Destino> listaDestinos = new ArrayList<>();
    static ArbolDestinos arbolDestinos = new ArbolDestinos();

    static {
        for (String[] d : destinos) {
            listaDestinos.add(new Destino(d[1]));
            arbolDestinos.insertar(d[1]);
        }
    }

    // ---------- VENTANA PRINCIPAL ----------
    public static class VentanaPrincipal extends JFrame {
        public VentanaPrincipal() {
            setTitle("Sistema Agencia de Viajes");
            setSize(600, 600);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JPanel panel = new JPanel(new GridLayout(13, 1, 6, 6));
            panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

            JLabel titulo = new JLabel("Bienvenido a la Agencia de Viajes", SwingConstants.CENTER);
            titulo.setFont(new Font("Arial", Font.BOLD, 16));
            panel.add(titulo);

            JButton btnReservas = new JButton("Sistema de reservas");
            JButton btnVerReservas = new JButton("Ver reservas");
            JButton btnAtencion = new JButton("Atender cliente");
            JButton btnCancelarReserva = new JButton("Registrar cancelación");
            JButton btnHistorial = new JButton("Historial de atenciones");
            JButton btnHistorialCancelaciones = new JButton("Historial de cancelaciones");
            JButton btnVerClientes = new JButton("Ver clientes");
            JButton btnCrearCliente = new JButton("Registrar cliente");

            JButton btnArbolDestinos = new JButton("Ver árbol de destinos");
            JButton btnOrdenarReservas = new JButton("Ordenar reservas");

            JButton btnSalir = new JButton("Salir");

            panel.add(btnReservas);
            panel.add(btnVerReservas);
            panel.add(btnAtencion);
            panel.add(btnCancelarReserva);
            panel.add(btnHistorial);
            panel.add(btnHistorialCancelaciones);
            panel.add(btnVerClientes);
            panel.add(btnCrearCliente);
            panel.add(btnArbolDestinos);
            panel.add(btnOrdenarReservas);
            panel.add(btnSalir);

            add(panel);

            btnReservas.addActionListener(e -> new VentanaReservas(this).setVisible(true));
            btnVerReservas.addActionListener(e -> new VentanaVerReservas(this, reservas).setVisible(true));

            btnAtencion.addActionListener(e -> {
                if (colaAtencion.cola.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No hay clientes en espera.");
                    return;
                }
                String siguiente = colaAtencion.desencolar();
                int opcion = JOptionPane.showConfirmDialog(this,
                        "Atender a: " + siguiente + " ?",
                        "Atención al cliente",
                        JOptionPane.YES_NO_OPTION);
                if (opcion == JOptionPane.YES_OPTION) {
                    pilaHistorial.apilar(siguiente);
                    JOptionPane.showMessageDialog(this, "Cliente atendido: " + siguiente);
                } else {
                    colaAtencion.encolar(siguiente);
                }
            });

            btnCancelarReserva.addActionListener(e -> {
                if (reservas.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No hay reservas para cancelar.");
                    return;
                }
                String[] opciones = reservas.stream().map(r -> r.toString()).toArray(String[]::new);
                String seleccion = (String) JOptionPane.showInputDialog(this,
                        "Seleccione reserva a cancelar",
                        "Cancelar reserva",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        opciones,
                        opciones[0]);
                if (seleccion != null) {
                    Reserva res = reservas.stream().filter(r -> r.toString().equals(seleccion)).findFirst().get();
                    reservas.remove(res);
                    pilaCancelaciones.apilar(res.toString());
                    JOptionPane.showMessageDialog(this, "Reserva cancelada y registrada en historial de cancelaciones.");
                }
            });

            btnHistorial.addActionListener(e -> {
                StringBuilder sb = new StringBuilder();
                if (pilaHistorial.pila.isEmpty()) sb.append("No hay historial de atenciones.");
                else pilaHistorial.pila.forEach(h -> sb.append(h).append("\n"));
                JOptionPane.showMessageDialog(this, sb.toString(), "Historial de atenciones", JOptionPane.INFORMATION_MESSAGE);
            });

            btnHistorialCancelaciones.addActionListener(e -> {
                StringBuilder sb = new StringBuilder();
                if (pilaCancelaciones.pila.isEmpty()) sb.append("No hay cancelaciones registradas.");
                else pilaCancelaciones.pila.forEach(c -> sb.append(c).append("\n"));
                JOptionPane.showMessageDialog(this, sb.toString(), "Historial de cancelaciones", JOptionPane.INFORMATION_MESSAGE);
            });

            btnVerClientes.addActionListener(e -> {
                String texto = listaClientes.mostrarComoTexto();
                if (texto.isEmpty()) texto = "No hay clientes registrados.";
                JOptionPane.showMessageDialog(this, texto, "Clientes", JOptionPane.INFORMATION_MESSAGE);
            });

            btnCrearCliente.addActionListener(e -> {
                String nombre = JOptionPane.showInputDialog(this, "Nombre del cliente:");
                if (nombre == null) return;

                String cedula = JOptionPane.showInputDialog(this, "Cédula del cliente:");
                if (cedula == null) return;

                if (!nombre.trim().isEmpty() && !cedula.trim().isEmpty()) {
                    listaClientes.agregar(nombre.trim(), cedula.trim());
                    JOptionPane.showMessageDialog(this, "Cliente agregado.");
                } else {
                    JOptionPane.showMessageDialog(this, "Datos inválidos.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            btnArbolDestinos.addActionListener(e -> {
                StringBuilder sb = new StringBuilder();
                sb.append("Destinos en orden (InOrden):\n\n");
                arbolDestinos.mostrarInOrden(sb);
                JOptionPane.showMessageDialog(this, sb.toString());
            });

            btnOrdenarReservas.addActionListener(e -> {
                String[] opciones = {"Por nombre", "Por destino"};
                String seleccion = (String) JOptionPane.showInputDialog(
                        this,
                        "Seleccione método de ordenamiento:",
                        "Ordenar reservas",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        opciones,
                        opciones[0]
                );

                if (seleccion == null) return;

                if (seleccion.equals("Por nombre"))
                    ordenarReservasPorNombre(reservas);
                else
                    ordenarReservasPorDestino(reservas);

                JOptionPane.showMessageDialog(this, "Reservas ordenadas correctamente.");
            });
        }
    }

    // ---------- VENTANA RESERVAS ----------
    public static class VentanaReservas extends JFrame {
        public VentanaReservas(JFrame parent) {
            setTitle("Reservas");
            setSize(420, 300);
            setLocationRelativeTo(parent);

            JPanel panel = new JPanel(new GridLayout(4, 2, 6, 6));
            panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

            panel.add(new JLabel("Nombre del cliente:"));
            JTextField tfNombre = new JTextField();
            panel.add(tfNombre);

            panel.add(new JLabel("Cédula del cliente:"));
            JTextField tfCedula = new JTextField();
            panel.add(tfCedula);

            panel.add(new JLabel("Destino:"));
            JComboBox<String> cbDestinos = new JComboBox<>();
            for (Destino d : listaDestinos) cbDestinos.addItem(d.getNombre());
            panel.add(cbDestinos);

            JButton btnReservar = new JButton("Reservar");
            panel.add(btnReservar);

            add(panel);

            btnReservar.addActionListener(e -> {
                String nombre = tfNombre.getText().trim();
                String cedula = tfCedula.getText().trim();
                String destino = (String) cbDestinos.getSelectedItem();

                if (nombre.isEmpty() || cedula.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Complete todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                listaClientes.agregar(nombre, cedula);
                reservas.add(new Reserva(nombre, cedula, destino));
                colaAtencion.encolar(nombre + " | " + cedula + " | " + destino);

                JOptionPane.showMessageDialog(this, "Reserva creada:\nCliente: " + nombre +
                        "\nCédula: " + cedula + "\nDestino: " + destino);

                tfNombre.setText("");
                tfCedula.setText("");
            });
        }
    }

    // ---------- VENTANA VER RESERVAS ----------
    public static class VentanaVerReservas extends JFrame {
        public VentanaVerReservas(JFrame parent, java.util.List<Reserva> reservas) {
            setTitle("Reservas");
            setSize(400, 300);
            setLocationRelativeTo(parent);

            JTextArea area = new JTextArea();
            area.setEditable(false);

            StringBuilder sb = new StringBuilder();
            if (reservas.isEmpty()) sb.append("No hay reservas registradas.");
            else reservas.forEach(r -> sb.append(r).append("\n"));

            area.setText(sb.toString());
            add(new JScrollPane(area));
        }
    }

    // ---------- MAIN ----------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }
}
