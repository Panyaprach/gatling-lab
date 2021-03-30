// SQS
import com.amazon.sqs.javamessaging._
import com.amazonaws.ClientConfiguration
import com.amazonaws.retry.PredefinedBackoffStrategies.ExponentialBackoffStrategy
import com.amazonaws.retry.RetryPolicy
import com.amazonaws.services.sqs._

import scala.concurrent.duration.DurationInt

// Gatling JMS DSL
import io.gatling.jms.Predef._
import io.gatling.core.Predef._

class MessageDsl extends Simulation {

  val sqsClient = AmazonSQSAsyncClientBuilder.defaultClient()

  val jmsProtocol = jms.connectionFactory(new SQSConnectionFactory(new ProviderConfiguration(), sqsClient))

  val scn = scenario("SQS Load Test")
    .exec(jms("SendMessage").send
    .queue("204892-Message-Simulation-POC")
    .textMessage("SomeText"))

  setUp(
    scn.inject(
      heavisideUsers(100000).during(60.seconds)
    )
  ).protocols(jmsProtocol)

  after {
    sqsClient.shutdown();
  }
}