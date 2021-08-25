package pl.knirski.akka.persistence.aggregator

import scala.concurrent.{ExecutionContext, Future}

import akka.persistence.snapshot.SnapshotStore
import akka.persistence.{SelectedSnapshot, SnapshotMetadata, SnapshotSelectionCriteria}


class AggregatedSnapshotStore(storesToWrite: List[SnapshotStore], storeToRead: SnapshotStore)(implicit ec: ExecutionContext) extends SnapshotStore {

  override def loadAsync(persistenceId: String,
                         criteria: SnapshotSelectionCriteria): Future[Option[SelectedSnapshot]] =
    storeToRead.loadAsync(persistenceId, criteria)

  override def saveAsync(metadata: SnapshotMetadata,
                         snapshot: Any): Future[Unit] =
    Future.traverse(storesToWrite)(_.saveAsync(metadata, snapshot)).map(_ => ())

  override def deleteAsync(metadata: SnapshotMetadata): Future[Unit] =
    Future.traverse(storesToWrite)(_.deleteAsync(metadata)).map(_ => ())


  override def deleteAsync(persistenceId: String,
                           criteria: SnapshotSelectionCriteria): Future[Unit] =
    Future.traverse(storesToWrite)(_.deleteAsync(persistenceId, criteria)).map(_ => ())

}
