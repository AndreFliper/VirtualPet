public class Cachorro extends Pet {
    public Cachorro(String nome, String raca, String imagem) {
        super(nome, raca, imagem);
    }

    @Override
    public String falar() {
        return nome + " diz: Au Au!";
    }

    @Override
    public String passear() {
        felicidade = Math.min(100, felicidade + 60); // diferente da base: +60 em vez de +50
        return nome + " ficou felizasso com o passeio!!";
    }
}