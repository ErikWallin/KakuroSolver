package se.marfok.kakurosolver.akka

import akka.actor.ActorSystem
import com.typesafe.config.Config
import akka.actor.ActorContext
import com.typesafe.config.Config
import java.util.concurrent.ConcurrentLinkedQueue
import akka.dispatch.{
  Envelope,
  MessageQueue,
  QueueBasedMessageQueue,
  UnboundedMessageQueueSemantics
}
import akka.dispatch.PriorityGenerator
import akka.actor.PoisonPill
import java.util.concurrent.PriorityBlockingQueue
import java.util.Queue
import akka.actor.ActorRef

case class EntryMailbox() extends akka.dispatch.MailboxType {

  val comparator = PriorityGenerator {
    case UpdateWhite(_) => 0
    case Reduce => 2
    case PoisonPill => 3
    case otherwise => 1
  }

  trait UnboundedMessageQueueWithNoReduceDuplicatesSemantics extends QueueBasedMessageQueue {

    var hasReduceInQueue = false

    override def enqueue(receiver: ActorRef, handle: Envelope): Unit = {
      queue.add(handle)
      handle.message match {
        case Reduce =>
          if (!hasReduceInQueue) {
            hasReduceInQueue = true
            queue.add(handle)
          }
        case _ => queue.add(handle)
      }
    }

    override def dequeue(): Envelope = {
      val envelope = queue.poll()
      if (envelope == null) null
      else {
        envelope.message match {
          case Reduce =>
            hasReduceInQueue = false
          case _ =>
        }
        envelope
      }
    }
  }

  def this(settings: ActorSystem.Settings, config: Config) = this()

  final override def create(owner: Option[ActorContext]): MessageQueue =
    new PriorityBlockingQueue[Envelope](11, comparator) with UnboundedMessageQueueWithNoReduceDuplicatesSemantics {
      final def queue: Queue[Envelope] = this
    }
}