public abstract class Pet {
    protected String nome;
    protected String raca;
    protected String imagem;
    protected double felicidade = 100.0; // %
    protected double sujeira = 0.0; // %

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

    public double getFelicidade() {
        return felicidade;
    }

    public double getSujeira() {
        return sujeira;
    }

    public void decreaseHappiness(double amount) {
        felicidade = Math.max(0, felicidade - amount);
    }

    public void increaseDirt(double amount) {
        sujeira = Math.min(100, sujeira + amount);
    }

    public String brincar() {
        felicidade = Math.min(100, felicidade + 10);
        return nome + " adorou brincar e sua felicidade aumentou";
    }

    public String passear() {
        felicidade = Math.min(100, felicidade + 50);
        return nome + " adorou o passeio e ficou feliz";
    }

    public String petisco() {
        felicidade = Math.min(100, felicidade + 5);
        return nome + " amou o petisco e ficou alegre";
    }

    public void darBanho() {
        sujeira = 0.0;
    }

    public abstract String falar();
}