package Model;

public enum StatusMesaMonstro {
    ATIVO("ativo"),
    DERROTADO("derrotado"),
    FUGIU("fugiu");

    private final String valorBanco;

    StatusMesaMonstro(String valorBanco) {
        this.valorBanco = valorBanco;
    }

    public String getValorBanco() {
        return valorBanco;
    }

    public static StatusMesaMonstro doBanco(String valor) {
        for (StatusMesaMonstro s : values()) {
            if (s.valorBanco.equals(valor)) return s;
        }
        throw new IllegalArgumentException("Valor inválido: " + valor);
    }
}
