package Model;

public class SubRacaAtributo {
    private int idSubRaca;
    private Atributo atributo;
    private int bonus;

    public SubRacaAtributo(int idSubRaca, Atributo atributo, int bonus) {
        this.idSubRaca = idSubRaca;
        this.atributo = atributo;
        this.bonus = bonus;
    }

    public int getIdSubRaca() {
        return idSubRaca;
    }

    public void setIdSubRaca(int idSubRaca) {
        this.idSubRaca = idSubRaca;
    }

    public Atributo getAtributo() {
        return atributo;
    }

    public void setAtributo(Atributo atributo) {
        this.atributo = atributo;
    }

    public int getBonus() {
        return bonus;
    }

    public void setBonus(int bonus) {
        this.bonus = bonus;
    }
}
