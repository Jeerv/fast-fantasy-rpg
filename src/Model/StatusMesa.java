package Model;

public enum StatusMesa {
    ATIVA("ativa"),
    ENCERRADA("encerrada");

    private final String valorBanco;

    StatusMesa(String valorBanco) {
        this.valorBanco = valorBanco;
    }

    public String getValorBanco() {
        return valorBanco;
    }

    public static StatusMesa doBanco(String valor) {
        for (StatusMesa s : values()) {
            if (s.valorBanco.equals(valor)) return s;
        }
        throw new IllegalArgumentException("Valor inválido: " + valor);
    }
}
