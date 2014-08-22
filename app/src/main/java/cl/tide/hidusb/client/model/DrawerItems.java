package cl.tide.hidusb.client.model;

/**
 * Created by eDelgado on 17-06-14.
 */
public class DrawerItems {

    public int getIcono() {
        return icono;
    }

    public void setIcono(int icono) {
        this.icono = icono;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public DrawerItems(String titulo, int icono) {
        this.titulo = titulo;
        this.icono = icono;
    }

    private String titulo;
    private int icono;
}
