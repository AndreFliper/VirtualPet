public class Cachorro extends Pet {
    public Cachorro(String nome, String raca) {
        super(nome, raca);
    }

    @Override
    public String falar() {
        return "Au Au!";
    }
}