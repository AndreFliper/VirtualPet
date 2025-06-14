public abstract class Pet {
    private String nome;
    private String raca;

    public Pet(String nome, String raca) {
        this.nome = nome;
        this.raca = raca;
    }

    public String getNome() {
        return nome;
    }

    public String getRaca() {
        return raca;
    }

    public abstract String falar();

    public String carinho() {
        return nome + " recebeu carinho.";
    }

    public String petisco() {
        return nome + " ganhou um petisco.";
    }
}