public class Hamster extends Pet {
    public Hamster(String nome, String raca) {
        super(nome, raca);
    }

    @Override
    public String falar() {
        return "Squeak!";
    }
}