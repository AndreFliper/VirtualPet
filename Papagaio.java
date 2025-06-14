public class Papagaio extends Pet {
    public Papagaio(String nome, String raca) {
        super(nome, raca);
    }

    @Override
    public String falar() {
        return "Loro qué biscoito!";
    }
}