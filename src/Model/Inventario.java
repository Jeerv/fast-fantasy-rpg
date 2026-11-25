package Model;

public class Inventario {
    private int idInventario;
    private int idPersonagem;

    public Inventario(int idInventario, int idPersonagem) {
        this.idInventario = idInventario;
        this.idPersonagem = idPersonagem;
    }

    public int getIdInventario() {
        return idInventario;
    }

    public void setIdInventario(int idInventario) {
        this.idInventario = idInventario;
    }

    public int getIdPersonagem() {
        return idPersonagem;
    }

    public void setIdPersonagem(int idPersonagem) {
        this.idPersonagem = idPersonagem;
    }
}
