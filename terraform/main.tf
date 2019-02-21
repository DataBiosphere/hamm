provider "google" {
  region = "${var.region}"
}

resource "google_service_account" "hamm-cromwell-metadata-notification-creater" {
  account_id   = "hamm-notification-creater" # this account id can't be too long
  display_name = "Service account for hamm cromwell metadata notification creater"
  project = "${var.project}"
}
# Give this service account storage admin role for metadata bucket

resource "google_storage_bucket_iam_binding" "storage-admin" {
  bucket = "${var.metadata-bucket-name}"
  role        = "roles/storage.admin"

  members = [
    "serviceAccount:${google_service_account.hamm-cromwell-metadata-notification-creater.email}",
  ]
}
resource "google_service_account" "hamm-cromwell-metadata-notification-subscriber" {
  account_id   = "hamm-notification-subscriber" # this account id can't be too long
  display_name = "Service account for hamm cromwell metadata notification subscriber"
  project = "${var.project}"
}
# Give this service account pubsub admin role
resource "google_pubsub_topic_iam_binding" "admin" {
  topic   = "${var.topic-name}"
  role    = "roles/pubsub.admin"
  project = "${var.project}"
  members = [
    "serviceAccount:${google_service_account.hamm-cromwell-metadata-notification-subscriber.email}",
  ]
}