package devilSpiderX.server.webServer.module.bemfa.configuration;

import devilSpiderX.server.webServer.module.bemfa.property.MqttProperties;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Configuration
public class MqttConfig {
    private final MqttProperties prop;

    public MqttConfig(MqttProperties prop) {
        this.prop = prop;
    }

    public MqttPahoClientFactory mqttClientFactory() {
        final DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        final MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[]{"tcp://bemfa.com:9501"});
        factory.setConnectionOptions(options);
        return factory;
    }

    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    @Bean
    @ConditionalOnProperty({"mqtt.bemfa.topic", "mqtt.bemfa.client-id"})
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler outbound() {
        // 发送消息和消费消息Channel可以使用相同MqttPahoClientFactory
        final MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(
                prop.getClientId(),
                mqttClientFactory()
        );
        messageHandler.setAsync(true); // 如果设置成true，即异步，发送消息时将不会阻塞。
        messageHandler.setDefaultTopic("%s/set".formatted(prop.getTopic()));
        messageHandler.setDefaultQos(1); // 设置默认QoS

        // Paho消息转换器
        final var defaultPahoMessageConverter = new DefaultPahoMessageConverter();
        messageHandler.setConverter(defaultPahoMessageConverter);
        return messageHandler;
    }

}
