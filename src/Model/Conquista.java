package Model;

public class Conquista {
    private int idConquista;
    private int moedas;
    private int idCosmeticos;

    public Conquista(int idConquista, int moedas, int idCosmeticos) {
        this.idConquista = idConquista;
        this.moedas = moedas;
        this.idCosmeticos = idCosmeticos;
    }

    public int getIdConquista() {
        return idConquista;
    }

    public void setIdConquista(int idConquista) {
        this.idConquista = idConquista;
    }

    public int getMoedas() {
        return moedas;
    }

    public void setMoedas(int moedas) {
        this.moedas = moedas;
    }

    public int getIdCosmeticos() {
        return idCosmeticos;
    }

    public void setIdCosmeticos(int idCosmeticos) {
        this.idCosmeticos = idCosmeticos;
    }
}
