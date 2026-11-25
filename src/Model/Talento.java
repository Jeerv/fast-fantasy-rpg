package Model;

public class Talento {
    private int idTalento;
    private String nome;
    private String preRequisito;
    private String descricao;
    private Integer idCriador;

    public Talento() {
    }

    public Talento(int id, String nome, String preRequisito, String descricao, Integer idCriador) {
        this.idTalento = id;
        this.nome = nome;
        this.preRequisito = preRequisito;
        this.descricao = descricao;
        this.idCriador = idCriador;
    }

    public int getIdTalento() {
        return idTalento;
    }

    public void setIdTalento(int v) {
        idTalento = v;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String v) {
        nome = v;
    }

    public String getPreRequisito() {
        return preRequisito;
    }

    public void setPreRequisito(String v) {
        preRequisito = v;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String v) {
        descricao = v;
    }

    public Integer getIdCriador() {
        return idCriador;
    }

    public void setIdCriador(Integer v) {
        idCriador = v;
    }
}