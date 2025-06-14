public class Gato extends Pet {
    public Gato(String nome, String raca, String imagem) {
        super(nome, raca, imagem);
    }

    @Override
    public String falar() {
        return "Miau!";
    }
}