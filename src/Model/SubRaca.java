package Model;

public class SubRaca {
    private int idSubRaca;
    private String nome;
    private String descricao;
    private int idRaca;

    public SubRaca(int idSubRaca, String nome, String descricao, int idRaca) {
        this.idSubRaca = idSubRaca;
        this.nome = nome;
        this.descricao = descricao;
        this.idRaca = idRaca;
    }

    public int getIdSubRaca() {
        return idSubRaca;
    }

    public void setIdSubRaca(int idSubRaca) {
        this.idSubRaca = idSubRaca;
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

    public int getIdRaca() {
        return idRaca;
    }

    public void setIdRaca(int idRaca) {
        this.idRaca = idRaca;
    }
}
