package Model;

public class InventarioItem {
    private int idInventario;
    private int idItem;
    private int quantidade;

    public InventarioItem(int idInventario, int idItem, int quantidade) {
        this.idInventario = idInventario;
        this.idItem = idItem;
        this.quantidade = quantidade;
    }

    public int getIdInventario() {
        return idInventario;
    }

    public void setIdInventario(int idInventario) {
        this.idInventario = idInventario;
    }

    public int getIdItem() {
        return idItem;
    }

    public void setIdItem(int idItem) {
        this.idItem = idItem;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }
}
