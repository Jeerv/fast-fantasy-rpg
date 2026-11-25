package Model;

public class Antecedente {
    private int idAntecedente;
    private String nome;
    private String descricao;
    private String caracteristicaNome;
    private String caracteristicaDescricao;
    private int idiomasAdicionais;
    private String ferramentasProficiencia;
    private String equipamentoInicial;

    public Antecedente() {
    }

    public Antecedente(int id, String nome, String descricao, String caracteristicaNome, String caracteristicaDescricao, int idiomasAdicionais, String ferramentasProficiencia, String equipamentoInicial) {
        this.idAntecedente = id;
        this.nome = nome;
        this.descricao = descricao;
        this.caracteristicaNome = caracteristicaNome;
        this.caracteristicaDescricao = caracteristicaDescricao;
        this.idiomasAdicionais = idiomasAdicionais;
        this.ferramentasProficiencia = ferramentasProficiencia;
        this.equipamentoInicial = equipamentoInicial;
    }

    public int getIdAntecedente() {
        return idAntecedente;
    }

    public void setIdAntecedente(int v) {
        idAntecedente = v;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String v) {
        nome = v;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String v) {
        descricao = v;
    }

    public String getCaracteristicaNome() {
        return caracteristicaNome;
    }

    public void setCaracteristicaNome(String v) {
        caracteristicaNome = v;
    }

    public String getCaracteristicaDescricao() {
        return caracteristicaDescricao;
    }

    public void setCaracteristicaDescricao(String v) {
        caracteristicaDescricao = v;
    }

    public int getIdiomasAdicionais() {
        return idiomasAdicionais;
    }

    public void setIdiomasAdicionais(int v) {
        idiomasAdicionais = v;
    }

    public String getFerramentasProficiencia() {
        return ferramentasProficiencia;
    }

    public void setFerramentasProficiencia(String v) {
        ferramentasProficiencia = v;
    }

    public String getEquipamentoInicial() {
        return equipamentoInicial;
    }

    public void setEquipamentoInicial(String v) {
        equipamentoInicial = v;
    }
}