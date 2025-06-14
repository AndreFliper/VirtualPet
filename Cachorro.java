public class Cachorro extends Pet {
    public Cachorro(String nome, String raca, String imagem) {
        super(nome, raca, imagem);
    }

    @Override
    public String falar() {
        return nome + " diz: Au Au!";
    }
}