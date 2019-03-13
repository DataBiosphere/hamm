package org.broadinstitute.dsp.workbench.hamm.db

import scalikejdbc.DBSession

trait PriceTableQueries {
  def insertPriceQuery(workflow: Workflow)(implicit session: DBSession): Int
  def getPriceQuery(workflowId: WorkflowId)(implicit session: DBSession): Option[Workflow]
}

class PriceTable extends PriceTableQueries {
  


}