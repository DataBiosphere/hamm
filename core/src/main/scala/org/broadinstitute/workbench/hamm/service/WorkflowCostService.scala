package org.broadinstitute.workbench.hamm.service

import org.broadinstitute.workbench.hamm.{CostCalculator, HammLogger}
import org.broadinstitute.workbench.hamm.auth.SamAuthProvider
import org.broadinstitute.workbench.hamm.dao.{GooglePriceListDAO, WorkflowMetadataDAO}
import org.broadinstitute.workbench.hamm.db.DbReference
import org.broadinstitute.workbench.hamm.model._

class WorkflowCostService(pricing: GooglePriceListDAO,
                          workflowDAO: WorkflowMetadataDAO,
                          samAuthProvider: SamAuthProvider,
                          dbRef: DbReference) extends HammLogger {

  def getWorkflowCost(token: String, workflowId: WorkflowId): WorkflowCostResponse = {
    // ToDo: some work here to make this less messy

    val cromwellMetadata = workflowDAO.getMetadata(token, workflowId)
    val authResponse     = samAuthProvider.hasWorkflowCollectionPermission(token, SamResource(cromwellMetadata.workflowCollectionId.asString)) // ToDo: throw on this!!!
    val rawPriceList     = pricing.getGcpPriceList()
    val priceList        = GooglePriceListDAO.parsePriceList(rawPriceList, getComputePriceKeysFromMetadata(cromwellMetadata), getStoragePriceKeysFromMetadata(cromwellMetadata))
    val result           = CostCalculator.getPriceOfWorkflow(cromwellMetadata, priceList)

    WorkflowCostResponse(workflowId, result)
  }

  private def getComputePriceKeysFromMetadata(metadata: MetadataResponse): List[ComputePriceKey] = {
    metadata.calls.map { call =>
      ComputePriceKey(call.region, call.machineType, UsageType.booleanToUsageType(call.preemptible))
    }
  }

  private def getStoragePriceKeysFromMetadata(metadata: MetadataResponse): List[StoragePriceKey] = {
    metadata.calls.map { call =>
      StoragePriceKey(call.region, call.runtimeAttributes.disks.diskType)
    }
  }
}

final case class WorkflowCostResponse(workflowId: WorkflowId, cost: Double)