package Model;

public class Personagem {
    private int idPersonagem;
    private String nome;
    private int nivel;
    private int idClasse;
    private int idRaca;
    private Integer idSubclasse;
    private Integer idSubRaca;
    private Integer idAntecedente;
    private int idPlayer;
    private int idAtributos;
    private Integer idMesa; // Chave estrangeira relacionada à tabela Mesa
    private int hpAtual;
    private int hpMaximo;
    private int ca;
    private int deslocamento;

    public Personagem() {
    }

    // Getters e Setters
    public int getIdPersonagem() {
        return idPersonagem;
    }

    public void setIdPersonagem(int idPersonagem) {
        this.idPersonagem = idPersonagem;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getNivel() {
        return nivel;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }

    public int getIdClasse() {
        return idClasse;
    }

    public void setIdClasse(int idClasse) {
        this.idClasse = idClasse;
    }

    public int getIdRaca() {
        return idRaca;
    }

    public void setIdRaca(int idRaca) {
        this.idRaca = idRaca;
    }

    public Integer getIdSubclasse() {
        return idSubclasse;
    }

    public void setIdSubclasse(Integer idSubclasse) {
        this.idSubclasse = idSubclasse;
    }

    public Integer getIdSubRaca() {
        return idSubRaca;
    }

    public void setIdSubRaca(Integer idSubRaca) {
        this.idSubRaca = idSubRaca;
    }

    public Integer getIdAntecedente() {
        return idAntecedente;
    }

    public void setIdAntecedente(Integer idAntecedente) {
        this.idAntecedente = idAntecedente;
    }

    public int getIdPlayer() {
        return idPlayer;
    }

    public void setIdPlayer(int idPlayer) {
        this.idPlayer = idPlayer;
    }

    public int getIdAtributos() {
        return idAtributos;
    }

    public void setIdAtributos(int idAtributos) {
        this.idAtributos = idAtributos;
    }

    public Integer getIdMesa() {
        return idMesa;
    }

    public void setIdMesa(Integer idMesa) {
        this.idMesa = idMesa;
    }

    public int getHpAtual() {
        return hpAtual;
    }

    public void setHpAtual(int hpAtual) {
        this.hpAtual = hpAtual;
    }

    public int getHpMaximo() {
        return hpMaximo;
    }

    public void setHpMaximo(int hpMaximo) {
        this.hpMaximo = hpMaximo;
    }

    public int getCa() {
        return ca;
    }

    public void setCa(int ca) {
        this.ca = ca;
    }

    public int getDeslocamento() {
        return deslocamento;
    }

    public void setDeslocamento(int deslocamento) {
        this.deslocamento = deslocamento;
    }
}
