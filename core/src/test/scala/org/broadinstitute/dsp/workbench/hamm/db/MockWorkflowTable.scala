package org.broadinstitute.dsp.workbench.hamm.db

import org.broadinstitute.dsp.workbench.hamm.model.WorkflowId
import scalikejdbc.DBSession

import scala.collection.mutable

class MockWorkflowTable extends WorkflowTableQueries {

  val workflows: mutable.Set[Workflow] = mutable.Set()

  def insertWorkflowQuery(workflow: Workflow)(implicit session: DBSession) ={
    workflows += workflow
    workflows.size
  }

  def getWorkflowQuery(workflowId: WorkflowId)(implicit session: DBSession): Option[Workflow] = {
    workflows.find(workflow => workflow.workflowId.equals(workflowId))
  }

  def getWorkflowCostQuery(workflowId: WorkflowId)(implicit session: DBSession): Option[Double] = {
    workflows.find(workflow => workflow.workflowId.equals(workflowId)).map(workflow => workflow.cost)

  }

}
