package notebook.jonathan.com.acidfy.model;

/**
 * Created by Jonathan on 18-05-2017.
 */

public class Artist {
    private String nombre;
    private String imagen;
    private String id;

    public Artist(String nombre, String imagen, String id) {
        this.nombre = nombre;
        this.imagen = imagen;
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public Artist setNombre(String nombre) {
        this.nombre = nombre;
        return this;
    }

    public String getImagen() {
        return imagen;
    }

    public Artist setImagen(String imagen) {
        this.imagen = imagen;
        return this;
    }

    public String getId() {
        return id;
    }

    public Artist setId(String id) {
        this.id = id;
        return this;
    }
}
