public abstract class Pet {
    protected String nome;
    protected String raca;
    protected String imagem; // NOVO

    public Pet(String nome, String raca, String imagem) {
        this.nome = nome;
        this.raca = raca;
        this.imagem = imagem;
    }

    public String getNome() {
        return nome;
    }
    public String getRaca() {
        return raca;
    }
    public String getImagem() {
        return imagem;
    }

    public abstract String falar();

    public String carinho() {
        return nome + " recebeu carinho.";
    }
    public String petisco() {
        return nome + " ganhou um petisco.";
    }
}