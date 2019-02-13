package org.broadinstitute.workbench.hamm.service

import cats.effect.Sync
import cats.implicits._
import io.chrisdavenport.log4cats.Logger
import org.broadinstitute.workbench.hamm.CostCalculator
import org.broadinstitute.workbench.hamm.auth.HttpSamDAO
import org.broadinstitute.workbench.hamm.dao.{GooglePriceListDAO, WorkflowMetadataDAO}
import org.broadinstitute.workbench.hamm.model._

class WorkflowCostService[F[_]: Sync: Logger](pricing: GooglePriceListDAO[F],
                                              workflowDAO: WorkflowMetadataDAO[F],
                                              samDAO: HttpSamDAO[F]) extends AuthedService(samDAO) {

  def getWorkflowCost(workflowId: WorkflowId): F[WorkflowCostResponse] = {
    withAuthenticatedUser(clientHeaders) { userInfo =>
      //ToDo: some work here to make this less messy
      for {
        cromwellMetadata: MetadataResponse <- workflowDAO.getMetadata(workflowId)
        _ <- checkAuthorization(cromwellMetadata.workflowCollectionId, "get_cost", userInfo.token)
        rawPriceList <- pricing.getGcpPriceList()
        priceList <- Sync[F].rethrow(Sync[F].delay[Either[Throwable, PriceList]](GooglePriceListDAO.parsePriceList(rawPriceList, getComputePriceKeysFromMetadata(cromwellMetadata), getStoragePriceKeysFromMetadata(cromwellMetadata))))
        result <- Sync[F].rethrow(Sync[F].delay[Either[Throwable, Double]](CostCalculator.getPriceOfWorkflow(cromwellMetadata, priceList)))
      } yield {
        WorkflowCostResponse(result)
      }
    }
  }
}

final case class WorkflowCostResponse(cost: Double)