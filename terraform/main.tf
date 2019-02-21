provider "google" {
  region = "${var.region}"
}

resource "google_pubsub_topic" "topic" {
    name = "${var.cromwell_metadata_pubsub_topic_name}"
}
data "google_storage_project_service_account" "gcs_account" {}

resource "google_storage_notification" "cromwell_metadata_notification" {
    bucket            = "${var.cromwell_metadata_bucket_name}"
    payload_format    = "JSON_API_V1"
    topic             = "${google_pubsub_topic.topic.id}"
    event_types       = ["OBJECT_FINALIZE"] # OBJECT_FINALIZE means notification will be created when object is created
    depends_on        = ["google_pubsub_topic_iam_binding.binding"]
}

resource "google_pubsub_topic_iam_binding" "binding" {
    topic       = "${google_pubsub_topic.topic.name}"
    role        = "roles/pubsub.publisher"
    members     = ["serviceAccount:${data.google_storage_project_service_account.gcs_account.email_address}"]
}
# resource "google_compute_network" "default" {
#   name                    = "${var.network_name}"
#   auto_create_subnetworks = "false"
# }

# resource "google_compute_subnetwork" "default" {
#   name                     = "${var.network_name}"
#   ip_cidr_range            = "10.127.0.0/20"
#   network                  = "${google_compute_network.default.self_link}"
#   region                   = "${var.region}"
#   private_ip_google_access = true
# }

# data "google_client_config" "current" {}

# resource "random_id" "name" {
#   byte_length = 2
# }

# module "postgresql-db" {
#   source           = "../../"
#   name             = "example-postgresql-${random_id.name.hex}"
#   user_host        = ""
#   database_version = "${var.postgresql_version}"

#   ip_configuration = [{
#     authorized_networks = [{
#       name  = "${var.network_name}"
#       value = "${google_compute_subnetwork.default.ip_cidr_range}"
#     }]
#   }]
# }

# output "psql_conn" {
#   value = "${data.google_client_config.current.project}:${var.region}:${module.postgresql-db.instance_name}"
# }

# output "psql_user_pass" {
#   value = "${module.postgresql-db.generated_user_password}"
# }