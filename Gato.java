public class Gato extends Pet {
    public Gato(String nome, String raca) {
        super(nome, raca);
    }

    @Override
    public String falar() {
        return "Miau!";
    }
}