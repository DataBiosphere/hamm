package org.broadinstitute.workbench.ccm

import java.time.Instant
import java.util.UUID

import doobie.util._
import doobie.postgres.implicits._

package object db {
  implicit val instantPut: Put[Instant] = Meta[Instant].put
  implicit val instantGet: Get[Instant] = Meta[Instant].get
  implicit val workflowIdMeta: Meta[WorkflowId] = Meta[UUID].timap[WorkflowId](uuid => WorkflowId(uuid))(_.uuid) //TODO: uuid might be provided by default
  implicit val workflowIdPut: Put[WorkflowId] = workflowIdMeta.put
  implicit val workflowIdGet: Get[WorkflowId] = workflowIdMeta.get
  implicit val labelRead: Read[Option[Label]] = Read[(Option[String], Option[String])].map[Option[Label]]{
    x =>
      x match {
        case (Some("submission"), Some(v)) => Some(Label.Submission(v))
        case (Some("workspace"), Some(v)) => Some(Label.Workspace(v))
        case _ => None
      }
  }
  implicit val labelWrite: Write[Option[Label]] = Write[(Option[(String, String)])].contramap[Option[Label]]{
    x =>
      x.map(label => (label.name, label.labelValue))
  }
  implicit val workflowDBRead: Read[WorkflowDB] = Read[(WorkflowId, Instant, Option[Label], Double)].map(x => WorkflowDB(x._1, x._2, x._3, x._4))
  implicit val workflowDBWrite: Write[WorkflowDB] = Write[(WorkflowId, Instant, Option[Label], Double)].contramap[WorkflowDB](x => (x.workflowId, x.endTime, x.label, x.cost))
}
