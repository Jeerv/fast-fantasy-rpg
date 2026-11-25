package Model;

public class Cosmeticos {
    private int idCosmeticos;
    private int tokens;
    private int iconesDePerfil;
    private int dadosPersonalizados;

    public Cosmeticos(int idCosmeticos, int tokens, int iconesDePerfil, int dadosPersonalizados) {
        this.idCosmeticos = idCosmeticos;
        this.tokens = tokens;
        this.iconesDePerfil = iconesDePerfil;
        this.dadosPersonalizados = dadosPersonalizados;
    }

    public int getIdCosmeticos() {
        return idCosmeticos;
    }

    public void setIdCosmeticos(int idCosmeticos) {
        this.idCosmeticos = idCosmeticos;
    }

    public int getTokens() {
        return tokens;
    }

    public void setTokens(int tokens) {
        this.tokens = tokens;
    }

    public int getIconesDePerfil() {
        return iconesDePerfil;
    }

    public void setIconesDePerfil(int iconesDePerfil) {
        this.iconesDePerfil = iconesDePerfil;
    }

    public int getDadosPersonalizados() {
        return dadosPersonalizados;
    }

    public void setDadosPersonalizados(int dadosPersonalizados) {
        this.dadosPersonalizados = dadosPersonalizados;
    }
}
