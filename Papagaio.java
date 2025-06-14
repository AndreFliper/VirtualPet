public class Papagaio extends Pet {
    public Papagaio(String nome, String raca, String imagem) {
        super(nome, raca, imagem);
    }

    @Override
    public String falar() {
        return "Loro qué biscoito!";
    }
}