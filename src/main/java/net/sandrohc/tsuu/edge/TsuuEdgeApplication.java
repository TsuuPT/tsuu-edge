package net.sandrohc.tsuu.edge;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.filter.factory.DedupeResponseHeaderGatewayFilterFactory.Strategy;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@EnableDiscoveryClient
@SpringBootApplication
public class TsuuEdgeApplication {

	@Value("${service.web.uri}")
	private String serviceWebUri;


	public static void main(String[] args) {
		SpringApplication.run(TsuuEdgeApplication.class, args);
	}

	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				.route("tsuu-api", r -> r
						.path("/api/**")
						.filters(f -> f
								.stripPrefix(1)
								.dedupeResponseHeader("Access-Control-Allow-Credentials Access-Control-Allow-Origin", Strategy.RETAIN_UNIQUE.toString()))
//						.filters(f -> f.filter(throttle.apply(1, 1, 10, TimeUnit.SECONDS)))
						.uri("lb://TSUU-API"))
				.route("tsuu-web", r -> r
						.path("/**")
						.filters(f -> f
								.dedupeResponseHeader("Access-Control-Allow-Credentials Access-Control-Allow-Origin", Strategy.RETAIN_UNIQUE.toString()))
						.uri(serviceWebUri))
				.build();
	}

}
