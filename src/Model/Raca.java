package Model;

public class Raca {
    private int idRaca;
    private String nome;
    private String descricao;

    public Raca(int idRaca, String nome, String descricao) {
        this.idRaca = idRaca;
        this.nome = nome;
        this.descricao = descricao;
    }

    public int getIdRaca() {
        return idRaca;
    }

    public void setIdRaca(int idRaca) {
        this.idRaca = idRaca;
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
}
