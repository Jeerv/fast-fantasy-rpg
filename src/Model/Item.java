package Model;

public class Item {
    private int idItem;
    private String nome;
    private String descricao;
    private int idCriador;

    public Item(int idItem, String nome, String descricao, int idCriador) {
        this.idItem = idItem;
        this.nome = nome;
        this.descricao = descricao;
        this.idCriador = idCriador;
    }

    public int getIdItem() {
        return idItem;
    }

    public void setIdItem(int idItem) {
        this.idItem = idItem;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getIdCriador() {
        return idCriador;
    }

    public void setIdCriador(int idCriador) {
        this.idCriador = idCriador;
    }
}
