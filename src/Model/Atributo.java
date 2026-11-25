package Model;

public enum Atributo {
    FORCA("forca"),
    DESTREZA("destreza"),
    CONSTITUICAO("constituicao"),
    INTELIGENCIA("inteligencia"),
    SABEDORIA("sabedoria"),
    CARISMA("carisma");

    private final String valorBanco;

    Atributo(String valorBanco) {
        this.valorBanco = valorBanco;
    }

    public String getValorBanco() {
        return valorBanco;
    }

    public static Atributo doBanco(String valor) {
        for (Atributo s : values()) {
            if (s.valorBanco.equals(valor)) return s;
        }
        throw new IllegalArgumentException("Valor inválido: " + valor);
    }
}
