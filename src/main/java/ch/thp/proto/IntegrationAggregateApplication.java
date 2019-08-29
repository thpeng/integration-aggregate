package ch.thp.proto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.aggregator.DefaultAggregatingMessageGroupProcessor;
import org.springframework.integration.aggregator.TimeoutCountSequenceSizeReleaseStrategy;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableIntegration
@EnableAsync
public class IntegrationAggregateApplication {

	public static void main(String[] args) {
		SpringApplication.run(IntegrationAggregateApplication.class, args);
	}

	@Bean
	public IntegrationFlow sourceAFlow(){
		return IntegrationFlows.from(new Source("A"), "from", e -> e.poller(Pollers.fixedRate(300, TimeUnit.MILLISECONDS)))
				.channel(inChannel())
				.get();
	}

	@Bean
	public IntegrationFlow sourceCFlow(){
		return IntegrationFlows.from(new Source("B"), "from", e -> e.poller(Pollers.fixedRate(300, TimeUnit.MILLISECONDS)))
				.channel(inChannel())
				.get();
	}

	@Bean(name = PollerMetadata.DEFAULT_POLLER)
	public PollerMetadata poller() { // 12
		return Pollers.fixedRate(100).get();
	}

	@Bean
	public IntegrationFlow sourceBFlow(){
		return IntegrationFlows.from(new Source("C"), "from", e -> e.poller(Pollers.fixedRate(200, TimeUnit.MILLISECONDS)))
				.channel(inChannel())
				.get();
	}

	@Bean
	public PublishSubscribeChannel inChannel(){
		return MessageChannels.publishSubscribe().get();
	}

	@Bean
	public IntegrationFlow aggregateFlow(){
		return IntegrationFlows.from(inChannel())
				.aggregate(aggregator -> aggregator
						.outputProcessor(processor())
						.correlationStrategy(m -> ((Datatype) m.getPayload()).getGroup())
				.releaseStrategy(new TimeoutCountSequenceSizeReleaseStrategy(100, 2000))
				.requiresReply(false)
				.expireGroupsUponCompletion(true))
				.<List<Datatype>, Set<Datatype>>transform(t -> new HashSet<>(t))
				.split()
				.handle(new LoggingHandler("INFO"))
				.get();

	}
	@Bean
	public DefaultAggregatingMessageGroupProcessor processor(){
		return new DefaultAggregatingMessageGroupProcessor();
	}
}
