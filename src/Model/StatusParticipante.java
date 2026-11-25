package Model;

public enum StatusParticipante {
    ATIVO("ativo"),
    INATIVO("inativo");

    private final String valorBanco;

    StatusParticipante(String valorBanco) {
        this.valorBanco = valorBanco;
    }

    public String getValorBanco() {
        return valorBanco;
    }

    public static StatusParticipante doBanco(String valor) {
        for (StatusParticipante s : values()) {
            if (s.valorBanco.equals(valor)) return s;
        }
        throw new IllegalArgumentException("Status inválido: " + valor);
    }
}
