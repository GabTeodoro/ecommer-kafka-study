package br.com.alura.ecommerce;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.Closeable;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class KafkaDispatcher<T> implements Closeable {

    private final KafkaProducer<String, Message<T>> producer;

    public KafkaDispatcher() {
        this.producer = new KafkaProducer(properties());
    }

    private static Properties properties() {
        var properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GsonSerializer.class.getName());
        properties.setProperty(ProducerConfig.ACKS_CONFIG, "all");

        return properties;
    }

    /*
    O método (parse) pode ser assincrono, mas nem sempre
    o método que envia as mensagens pro Kafka é também
    Você pode escolher que ele seja async ou não
    */
    void send(String topic, String key, CorrelationId id, T payload) throws ExecutionException, InterruptedException {
        var future = sendAsync(topic, key, id, payload);
        future.get();

    }

    Future sendAsync(String topic, String key, CorrelationId id, T payload) {
        var value = new Message(id, payload);
        // O record é o objeto que é enviado pelo Kafka
        var record = new ProducerRecord(topic, key, value);

        // Callback é o que eu quero que faça quando a mensagem for enviada e caso de algum erro
        Callback callback = (data, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
            }
            System.out.println("Success! " + data.topic() + ":::partition " + data.partition() + "/offset " + data.offset() +
                    "/timestamp " + data.timestamp());
        };
        return producer.send(record, callback);
    }

    @Override
    public void close() {
        producer.close();
    }
}
