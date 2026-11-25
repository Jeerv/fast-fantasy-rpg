package Model;

public class Monstro {
    private int idMonstro;
    private String nome;
    private TamanhoMonstro tamanho;
    private String tipo;
    private String tendencia;
    private int ca;
    private int hpMaximo;
    private String deslocamento;
    private int forca;
    private int destreza;
    private int constituicao;
    private int inteligencia;
    private int sabedoria;
    private int carisma;
    private String desafio;
    private int xp;
    private String descricao;
    private int idCriador; // 0 = monstro oficial do livro (id_criador NULL)

    public Monstro() {
    }

    public Monstro(int idMonstro, String nome, TamanhoMonstro tamanho, String tipo, String tendencia, int ca,
                   int hpMaximo, String deslocamento, int forca, int destreza, int constituicao, int inteligencia,
                   int sabedoria, int carisma, String desafio, int xp, String descricao, int idCriador) {
        this.idMonstro = idMonstro;
        this.nome = nome;
        this.tamanho = tamanho;
        this.tipo = tipo;
        this.tendencia = tendencia;
        this.ca = ca;
        this.hpMaximo = hpMaximo;
        this.deslocamento = deslocamento;
        this.forca = forca;
        this.destreza = destreza;
        this.constituicao = constituicao;
        this.inteligencia = inteligencia;
        this.sabedoria = sabedoria;
        this.carisma = carisma;
        this.desafio = desafio;
        this.xp = xp;
        this.descricao = descricao;
        this.idCriador = idCriador;
    }

    // Getters e Setters
    public int getIdMonstro() {
        return idMonstro;
    }

    public void setIdMonstro(int idMonstro) {
        this.idMonstro = idMonstro;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public TamanhoMonstro getTamanho() {
        return tamanho;
    }

    public void setTamanho(TamanhoMonstro tamanho) {
        this.tamanho = tamanho;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getTendencia() {
        return tendencia;
    }

    public void setTendencia(String tendencia) {
        this.tendencia = tendencia;
    }

    public int getCa() {
        return ca;
    }

    public void setCa(int ca) {
        this.ca = ca;
    }

    public int getHpMaximo() {
        return hpMaximo;
    }

    public void setHpMaximo(int hpMaximo) {
        this.hpMaximo = hpMaximo;
    }

    public String getDeslocamento() {
        return deslocamento;
    }

    public void setDeslocamento(String deslocamento) {
        this.deslocamento = deslocamento;
    }

    public int getForca() {
        return forca;
    }

    public void setForca(int forca) {
        this.forca = forca;
    }

    public int getDestreza() {
        return destreza;
    }

    public void setDestreza(int destreza) {
        this.destreza = destreza;
    }

    public int getConstituicao() {
        return constituicao;
    }

    public void setConstituicao(int constituicao) {
        this.constituicao = constituicao;
    }

    public int getInteligencia() {
        return inteligencia;
    }

    public void setInteligencia(int inteligencia) {
        this.inteligencia = inteligencia;
    }

    public int getSabedoria() {
        return sabedoria;
    }

    public void setSabedoria(int sabedoria) {
        this.sabedoria = sabedoria;
    }

    public int getCarisma() {
        return carisma;
    }

    public void setCarisma(int carisma) {
        this.carisma = carisma;
    }

    public String getDesafio() {
        return desafio;
    }

    public void setDesafio(String desafio) {
        this.desafio = desafio;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
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
