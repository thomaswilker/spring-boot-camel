package com.example;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class CustomRouter extends RouteBuilder {

	@Value("${responseQueue}")
	public String responseQueue;

	public String getResponseQueue() {
		return "activemq:queue:" + responseQueue;
	}
	
	@Override
	public void configure() throws Exception {
		from("activemq:queue:incoming").process(e -> {
			Message msg = e.getIn();
			List<Integer> jobs = Arrays.asList(1, 2, 3, 4);

			msg.setHeader("uuid", UUID.randomUUID().toString());
			msg.setHeader("jobCount", jobs.size());
			msg.setBody(jobs, List.class);

		}).split(body()).parallelProcessing().process(e -> {
			Thread.sleep(Math.round(Math.random() * 1000));
		}).log("processed ${body}").to(getResponseQueue());

		from(getResponseQueue())
				.log("${body} ${header.uuid}")
				.aggregate(
						header("uuid"),
						(Exchange last, Exchange next) -> {
							if (last == null) {
								return next;
							} else {
								Message in = last.getIn();
								int count = in.getHeader("jobCount", Integer.class) - 1;
								in.setHeader("jobCount", count);
								return last;
							}
						}).completionPredicate(header("jobCount").isEqualTo(1))
				.log("finished als job for uuid: ${header.uuid}")
				.to("mock:result");
	}

}
