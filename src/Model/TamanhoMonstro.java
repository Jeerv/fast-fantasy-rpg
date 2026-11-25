package Model;

public enum TamanhoMonstro {
    MIUDO("Miúdo"),
    PEQUENO("Pequeno"),
    MEDIO("Médio"),
    GRANDE("Grande"),
    ENORME("Enorme"),
    IMENSO("Imenso");

    private final String valorBanco;

    TamanhoMonstro(String valorBanco) {
        this.valorBanco = valorBanco;
    }

    public String getValorBanco() {
        return valorBanco;
    }

    public static TamanhoMonstro doBanco(String valor) {
        for (TamanhoMonstro t : values()) {
            if (t.valorBanco.equals(valor)) return t;
        }
        throw new IllegalArgumentException("Valor inválido: " + valor);
    }
}
