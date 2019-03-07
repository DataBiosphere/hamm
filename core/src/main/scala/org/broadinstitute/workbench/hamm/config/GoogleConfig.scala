package org.broadinstitute.workbench.hamm.config

import org.http4s.Uri

case class GoogleConfig(googleCloudBillingUrl: Uri,
                        googleDefaultPricingUrl: Uri,
                        serviceId: String,   // serviceId and serviceKey will go away once we get
                        serviceKey: String   // SKUs from here: https://cloud.google.com/billing/reference/rest/v1/services.skus/list
                       )
