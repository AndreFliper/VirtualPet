public class Hamster extends Pet {
    public Hamster(String nome, String raca, String imagem) {
        super(nome, raca, imagem);
    }

    @Override
    public String falar() {
        return "Squeak!";
    }
}