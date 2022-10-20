package br.com.alura.ecommerce;

public class Message<T> {

    private final CorrelationId id;
    private final T paylod;

    public CorrelationId getId() {
        return id;
    }

    public T getPaylod() {
        return paylod;
    }

    public Message(CorrelationId id, T paylod ) {
        this.id = id;
        this.paylod = paylod;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", paylod=" + paylod +
                '}';
    }
}
