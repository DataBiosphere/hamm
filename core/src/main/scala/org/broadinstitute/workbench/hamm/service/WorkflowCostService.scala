package org.broadinstitute.workbench.hamm.service

import org.broadinstitute.workbench.hamm.CostCalculator
import org.broadinstitute.workbench.hamm.auth.HttpSamDAO
import org.broadinstitute.workbench.hamm.dao.{GooglePriceListDAO, WorkflowMetadataDAO}
import org.broadinstitute.workbench.hamm.model._

class WorkflowCostService(pricing: GooglePriceListDAO,
                          workflowDAO: WorkflowMetadataDAO,
                          samDAO: HttpSamDAO) {

  def getWorkflowCost(userInfo: UserInfo, workflowId: WorkflowId): WorkflowCostResponse = {
    // ToDo: some work here to make this less messy
    val cromwellMetadata = workflowDAO.getMetadata(userInfo, workflowId)
    val authResponse     = samDAO.queryAction(userInfo.token, SamResource(cromwellMetadata.workflowCollectionId.uuid.toString), "get_cost") // throw on this
    val rawPriceList     = pricing.getGcpPriceList()
    val priceList        = GooglePriceListDAO.parsePriceList(rawPriceList, getComputePriceKeysFromMetadata(cromwellMetadata), getStoragePriceKeysFromMetadata(cromwellMetadata))
    val result           = CostCalculator.getPriceOfWorkflow(cromwellMetadata, priceList)

    WorkflowCostResponse(result)
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

final case class WorkflowCostResponse(cost: Double)