package pl.knirski.akka.persistence.aggregator

import scala.collection.immutable
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

import akka.persistence.journal.AsyncWriteJournal
import akka.persistence.{AtomicWrite, PersistentRepr}


class AggregatedJournal(journalsToWrite: List[AsyncWriteJournal], journalToRead: AsyncWriteJournal)(implicit ec: ExecutionContext) extends AsyncWriteJournal {

  override def asyncWriteMessages(messages: immutable.Seq[AtomicWrite]): Future[immutable.Seq[Try[Unit]]] =
    Future.traverse(journalsToWrite)(_.asyncWriteMessages(messages)).map(_.flatten)

  override def asyncDeleteMessagesTo(persistenceId: String,
                                     toSequenceNr: Long): Future[Unit] =
    Future.traverse(journalsToWrite)(_.asyncDeleteMessagesTo(persistenceId, toSequenceNr)).map(_ => ())

  override def asyncReplayMessages(persistenceId: String, fromSequenceNr: Long, toSequenceNr: Long, max: Long)
                                  (recoveryCallback: PersistentRepr => Unit): Future[Unit] =
    journalToRead.asyncReplayMessages(persistenceId, fromSequenceNr, toSequenceNr, max)(recoveryCallback).map(_ => ())

  override def asyncReadHighestSequenceNr(persistenceId: String,
                                          fromSequenceNr: Long): Future[Long] =
    journalToRead.asyncReadHighestSequenceNr(persistenceId, fromSequenceNr)

}
